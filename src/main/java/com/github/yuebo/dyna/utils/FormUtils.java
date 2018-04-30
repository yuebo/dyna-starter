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

package com.github.yuebo.dyna.utils;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.ConvertContext;
import com.github.yuebo.dyna.core.ConvertProvider;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.event.FileUploadEvent;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: yuebo
 * Date: 1/22/15
 * Time: 11:01 AM
 */
@Component
public class FormUtils implements AppConstants {
    @Autowired
    private SpringUtils springUtils;
    @Autowired
    private FormViewUtils formViewUtils;
    @Autowired
    protected com.github.yuebo.dyna.provider.MultipartStoreHandler multipartStoreHandler;
    @Autowired
    private JDBCService jdbcService;

    public void populateForSearch(Map<String, Object> field, String name, String type, Map converter, Map parameter, String operator, HttpServletRequest request, Map saveEntity) {
        if (StringUtils.isEmpty(request.getParameter(name)) && !"false".equals(field.get("allowEmpty"))) {
            return;
        }
        if (request.getParameter(name) == null) {
            return;
        }
        ConvertProvider convertProvider = springUtils.getConvertProvider(converter);

        if (isMultiValue(type)) {
            List<String> val = request.getParameterValues(name) == null ? new ArrayList() : Arrays.asList(request.getParameterValues(name));
            if (operator == null) {
                operator = $in;
            }
            saveEntity.put(name, new BasicDBObject(operator, convertProvider.convert(val, new ConvertContext(converter))));
        } else {
            Object value = convertProvider.convert(request.getParameter(name), new ConvertContext(converter));
            if (value instanceof String && (isSingleValue(type))) {
                if (operator == null) {
                    saveEntity.put(name, value);
                } else if ($like.equals(operator)) {
                    saveEntity.put(name, new BasicDBObject(operator, value));
                } else {

                }

            } else {
                if (operator != null) {
                    saveEntity.put(name, new BasicDBObject(operator, value));
                } else {
                    ConvertUtils.setProperty(saveEntity, name, value);
                }

            }

        }
    }

    public void populateForRestore(String name, String type, Map converter, HttpServletRequest request, Map saveEntity, boolean escapeEmpty) {
        if (StringUtils.isEmpty(request.getParameter(name)) && escapeEmpty) {
            return;
        }
        String[] value = request.getParameterValues(name);
        ConvertProvider convertProvider = springUtils.getConvertProvider(converter);
        if (isMultiValue(type)) {
            List<String> val = request.getParameterValues(name) == null ? new ArrayList() : Arrays.asList(value);
            ConvertUtils.setProperty(saveEntity, name, convertProvider.convert(val, new ConvertContext(converter)));
        } else {
            ConvertUtils.setProperty(saveEntity, name, convertProvider.convert(value == null ? null : value[0], new ConvertContext(converter)));
        }
    }

    public void populateForUpdate(String name, String type, Map converter, String processor, HttpServletRequest request, Map saveEntity, boolean escapeEmpty) {
        if (StringUtils.isEmpty(request.getParameter(name)) && escapeEmpty) {
            return;
        }
        ConvertProvider convertProvider = springUtils.getConvertProvider(converter);
        if (INPUT_TYPE_FILE.equals(type) && request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartHttpServletRequest.getFile(name);
            try {
                if (file.getSize() == 0) {
                    ConvertUtils.setProperty(saveEntity, name, "");
                } else {
                    String fullPath = multipartStoreHandler.store(file);
                    if (processor != null) {
                        FileUploadEvent event = new FileUploadEvent(name, processor, file.getOriginalFilename(), fullPath, (Map) request.getSession().getAttribute(SESSION_KEY_USER));
                        getEventList(request).add(event);
                        ConvertUtils.setProperty(saveEntity, name, event.getId());
                    } else {
                        ConvertUtils.setProperty(saveEntity, name, fullPath);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                ConvertUtils.setProperty(saveEntity, name, null);
            }


        } else if (isMultiValue(type)) {
            List<String> val = request.getParameterValues(name) == null ? new ArrayList() : Arrays.asList(request.getParameterValues(name));
            ConvertUtils.setProperty(saveEntity, name, convertProvider.convert(val, new ConvertContext(converter)));
        } else {
            ConvertUtils.setProperty(saveEntity, name, convertProvider.convert(request.getParameter(name), new ConvertContext(converter)));
        }
    }

    public List<FileUploadEvent> getEventList(HttpServletRequest request) {
        List<FileUploadEvent> eventList = (List<FileUploadEvent>) request.getAttribute(EVENT_LIST);
        if (eventList == null) {
            eventList = new ArrayList<FileUploadEvent>();
            request.setAttribute(EVENT_LIST, eventList);
        }
        return eventList;
    }

    public void copyValuesToViewMap(ViewContext viewContext, Map<String, Object> value) {
        List<Map<String, Object>> list = viewContext.getFields();
        if (list != null) {
            for (Map map : list) {
                String name = MapUtils.getString(map,VIEW_FIELD_NAME);
                String type = MapUtils.getString(map,VIEW_FIELD_TYPE);
                String alias = MapUtils.getString(map,"alias");
                Map converter = (Map) map.get("converter");
                ConvertProvider provider = springUtils.getConvertProvider(converter);
                HashMap<String, Object> attr = (HashMap<String, Object>) map.get("attributes");
                if (attr == null) {
                    attr = new HashMap<String, Object>();
                    map.put("attributes", attr);
                }
                Map<String, Object> tempval = value;
                if (value == null) {
                    tempval = new HashMap();
                }

                if (name.startsWith("$")) {
                    Map<String, Object> field = formViewUtils.getField(viewContext, name);
                    if (field.containsKey("join")) {
                        Map<String, Object> join = (Map) field.get("join");
                        String table = (String) join.get("table");
                        String joinedKey = (String) join.get("column");
                        String joinedField = (String) join.get("field");
//                    Object vals= ConvertUtils.getProperty(value, name);
                        HashMap param = new HashMap();
                        param.put(joinedKey, value.get(DB_FIELD__ID));
                        if (Boolean.TRUE.equals(join.get("bindTask"))) {
                            param.put(PARAMETER_FIELD__TASKID, viewContext.getTaskId());
                        }

                        List<Map<String, Object>> result = jdbcService.findList(table, param, new ListOrderedMap(), 0, 0);
                        if (result == null || result.size() == 0) {
                        } else if (result.size() == 1) {
                            ConvertUtils.setProperty(tempval, name, result.get(0).get(joinedField));
                        } else {
                            ArrayList<Object> objects = new ArrayList<>();
                            for (Map<String, Object> objectMap : result) {
                                objects.add(objectMap.get(joinedField));
                            }
                            ConvertUtils.setProperty(tempval, name, objects);
                        }
                    }


                }

                //read alias name value to keep the value from source
                Object v = ConvertUtils.getProperty(tempval, alias==null?name:alias);
                if (v != null) {
                    if (INPUT_TYPE_FILE.equalsIgnoreCase(type)) {
                        attr.put("value", v);
                    } else if (isSingleValue(type) || isLabelType(type)) {

                        attr.put("value", provider.restore(v, new ConvertContext(converter)));
                    } else if (isMultiValue(type)) {
                        if (v instanceof List) {

                        } else {
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(v);
                            v = arrayList;
                        }

                        attr.put("value", provider.restore(v, new ConvertContext(converter)));
                    }
                }


            }
        }
        viewContext.setId(MapUtils.getString(value, DB_FIELD__ID));
    }
}
