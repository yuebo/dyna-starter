/*
 *
 *  * Copyright 2002-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.yuebo.dyna.event;

import com.github.yuebo.dyna.core.*;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by yuebo on 28/11/2017.
 */
@Component
public class XlsxFileProcessor extends DefaultFileProcessor {
    public boolean accept(FileUploadEvent event) {
        Map<String, Object> uploadView = formViewUtils.getFormUpload(event.getUpload());
        return "xlsx".equals(MapUtils.getString(uploadView, "file"));
    }

    @Override
    @Async
    @Transactional
    public void processFile(FileUploadEvent event) {


        logger.info("-------------job started:" + new Date());
        //prepare job

        String jobId = prepareJob(event);

        Map<String, Object> uploadView = formViewUtils.getFormUpload(event.getUpload());
        ViewContext viewContext = new ViewContext(uploadView);
        viewContext.setFileUploadEvent(event);

        //load the job to interface table
        String data = viewContext.getData();


        if (uploadView == null || data == null) {
            logError(event, jobId, ERROR_TYPE_TABLE, "Empty processor data type", null);
            updateJob(event, "Failed", jobId, 0);
            return;
        }


        int start = MapUtils.getIntValue(uploadView, "startRow", 1);
        long errorCount = 0;
        try {
            List<Map<String, Object>> fields = (List) MapUtils.getObject(uploadView, "fields");
            //clear buffer table
            clearBufferTable(viewContext);
            File fileToLoad = new File(event.getFileName());
            File folder = fileToLoad.getParentFile();
            File newFile = new File(folder, "err_" + fileToLoad.getName());
            boolean hasError = false;

            if (fields != null) {
                try (FileInputStream inputStream = new FileInputStream(fileToLoad)) {
                    Workbook workbook = WorkbookFactory.create(inputStream);
                    final CellStyle cs = workbook.createCellStyle();
                    cs.setFillForegroundColor(org.apache.poi.hssf.util.HSSFColor.RED.index);
                    cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
                    final Sheet sheet = workbook.getSheetAt(0);

                    HashMap<String, Integer> fieldIndexMap = new HashMap();
                    int index = 0;
                    for (Map<String, Object> field : fields) {

                        fieldIndexMap.put((String) field.get(VIEW_FIELD_FIELDS_NAME), index++);
                    }


                    loadToInterface(workbook, sheet, start, viewContext);
                    //buffer table process end

                    errorCount = processAfterLoad(viewContext, fieldIndexMap, sheet, cs, event, jobId);

                    //save the errors
                    hasError = errorCount > 0;
                    if (hasError) {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                            workbook.write(fileOutputStream);
                        }
                    }
                }

                if (hasError) {
                    fileToLoad.delete();
                    newFile.renameTo(fileToLoad);
                }


            }


            updateJob(event, "Completed", jobId, errorCount);
        } catch (Exception e) {
            logError(event, jobId, ERROR_TYPE_TABLE, e.getMessage(), null);
            updateJob(event, "Failed", jobId, 0);

        } finally {
            logger.info("----------job end:" + new Date());
        }


    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long processAfterLoad(ViewContext viewContext, Map<String, Integer> fieldIndexMap, Sheet sheet, CellStyle cs, FileUploadEvent event, String jobId) {
        List<Map<String, Object>> fields = viewContext.getFields();
        String bufferTable = getBufferTable(viewContext);
        long totalRead = 0;
        long totalError = 0;

        ListOrderedMap listOrderedMap = new ListOrderedMap();
        listOrderedMap.put("row", "asc");


        //read the result from interface table
        SqlRowSet results = jdbcService.queryForRowSet(getBufferTable(viewContext), Collections.emptyMap(), listOrderedMap, 0, 0);
        while (results.next()) {
            Map<String, List<Map<String, Object>>> messages = new HashMap();
            HashMap row = new HashMap();

            for (Map<String, Object> field : fields) {
                String name = MapUtils.getString(field, VIEW_FIELD_FIELDS_NAME);
                String type = MapUtils.getString(field, VIEW_FIELD_FIELDS_TYPE);
                Object value = results.getObject(name);

                boolean update = Boolean.FALSE.equals(MapUtils.getBoolean(field, VIEW_FIELD_FIELDS_UPDATE, false));
                if (!update) {
                    continue;
                }
                Map converter = (Map) field.get(VIEW_FIELD_FIELDS_CONVERTER);
                if (converter != null) {
                    ConvertProvider convertProvider = springUtils.getConvertProvider(converter);
                    value = convertProvider.convert(value, new ConvertContext(converter));
                }
                value = checkType(type, value);
                row.put(name, value);
            }
            //process validator
            boolean isValid = true;
            List<Map<String, Object>> validators = viewContext.getValidators();
            for (Map<String, Object> validator : validators) {
                if (validator != null) {
                    ValidateContext validateContext = new ValidateContext(viewContext, validator);
                    String provider = (String) validator.get(VIEW_FIELD_VALIDATORS_PROVIDER);
                    String msg = (String) validator.get(VIEW_FIELD_VALIDATORS_MSG);
                    String field = (String) validator.get(VIEW_FIELD_VALIDATORS_FIELD);

                    ValidatorProvider validatorProvider = springUtils.getValidatorProvider(provider);
                    if (!(validatorProvider instanceof FileValidatorProvider)) {
                        isValid = false;
                        logError(event, jobId, ERROR_TYPE_RECORD, "Invalid Validator", results.getInt("ROW"));
                    } else {
                        boolean isSuccess = validatorProvider.validate(validateContext, row);
                        if (!isSuccess) {
                            Map<String, Object> fieldMap = formViewUtils.getField(viewContext, field);
                            String label = MapUtils.getString(fieldMap, VIEW_FIELD_FIELDS_LABEL);
                            messageUtils.addErrorMessage(messages, field, msg, viewContext, label == null ? "column" + fieldIndexMap.get(field) : label);
                            if (fieldIndexMap.containsKey(field)) {
                                sheet.getRow(results.getInt("row")).getCell(fieldIndexMap.get(field)).setCellStyle(cs);
                            } else {
                                sheet.getRow(results.getInt("row")).setRowStyle(cs);
                            }
                            logError(event, jobId, ERROR_TYPE_RECORD, lastError(messages), results.getInt("ROW"));
                            totalError++;
                            isValid = false;
                        }
                    }


                }
            }

            //prepare the key value map;
            List<String> keys = viewContext.getKeys();
            Map keyMap = new HashMap();
            if (keys != null) {
                for (String key : keys) {
                    keyMap.put(key, row.get(key));
                }

            }
            //put the default value to the row
            Map value=viewContext.getDefaultValue();
            if(value!=null){
                row.putAll(value);
            }
            processUpdateData(viewContext, row, keyMap, totalError, isValid);
            //after validation, the save the data

            totalRead = 1 + totalRead;

        }
        return totalError;
    }

    protected String lastError(Map<String, List<Map<String, Object>>> messages) {
        List<Map<String, Object>> list = messages.get("error");
        if (list != null && list.size() > 0) {
            return (String) list.get(list.size() - 1).get("msg");
        }
        return null;
    }

    protected void processUpdateData(ViewContext viewContext, HashMap row, Map keyMap, long totalError, boolean isValid) {
        if (!isValid)
            return;

        String table = viewContext.getData();
        if (!keyMap.isEmpty()) {
            Map data = jdbcService.find(table, keyMap);
            if (data != null) {
                data.putAll(row);
                initUpdateInfo(row,viewContext.getFileUploadEvent().getUser());
                jdbcService.update(table, new BasicDBObject("_id", data.get("_id")), row);
            } else {
                initCreateInfo(row,viewContext.getFileUploadEvent().getUser());
                jdbcService.save(table, row);
            }

        } else {
            jdbcService.save(table, row);
        }

    }

    protected void clearBufferTable(ViewContext viewContext) {
        jdbcService.delete(getBufferTable(viewContext), new HashMap());
    }

    protected String getBufferTable(ViewContext viewContext) {
        String bufferTable = String.valueOf(viewContext.getData()).toLowerCase().replaceFirst("tbl_", TBL_INTERFACE_PREFIX);
        return bufferTable;
    }

    protected void loadToInterface(Workbook workbook, Sheet sheet, int start, ViewContext viewContext) throws RuntimeException {
        List<Map<String, Object>> fields = viewContext.getFields();
        String bufferTable = getBufferTable(viewContext);
        int rows = sheet.getLastRowNum();
        for (int i = start; i <= rows; i++) {
            Row row = sheet.getRow(i);
            int column = row.getLastCellNum();
            int colStart = 0;
            Map rowData = new HashMap();
            rowData.put("row", i);
            for (Map<String, Object> field : fields) {
                String name = MapUtils.getString(field, "name");
                String type = MapUtils.getString(field, "type");
                Cell cell = row.getCell(colStart, Row.RETURN_BLANK_AS_NULL);
                Object value = null;
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            value = cell.getNumericCellValue();
                            break;
                        default:
                            value = cell.getStringCellValue();
                            value = org.apache.commons.lang.StringUtils.trim((String) value);
                    }

                    if (value != null) {
                        if ("date".equals(type)) {
                            try {
                                value = HSSFDateUtil.getJavaDate((Double) value);
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                try {
                    value = checkType(type, value);
                }catch (Exception e){
                    logger.error("invalid type at column {}, row {}, date type {}, value {}", colStart,i, type, value);
                    throw e;
                }

                rowData.put(name, value);
//                        rowData.put("col",colStart);
                colStart++;
            }

            processInterfaceRow(bufferTable, rowData);


        }
    }

    //Check the type if the result matches
    protected Object checkType(String type, Object value) throws RuntimeException {
        if (type == null) {
            type = "text";
        }
        if ("text".equals(type) && (value instanceof String || value == null)) {
            return value;
        }
        if ("date".equals(type) && (value instanceof Date || value == null)) {
            return value == null ? new NullDate() : value;
        }
        if ("number".equals(type) && (value instanceof Number || value == null)) {
            return value == null ? new NullNumber() : value;
        }
        throw new RuntimeException("Error Data type find");

    }

    protected void processInterfaceRow(String bufferTable, Map rowData) {
        jdbcService.save(bufferTable, rowData);
    }

}
