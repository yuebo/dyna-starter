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

package com.github.yuebo.dyna.provider.processor;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.FormProcessor;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.utils.*;
import com.github.yuebo.dyna.service.JDBCService;
import com.github.yuebo.dyna.utils.*;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: yuebo
 * Date: 12/2/14
 * Time: 9:44 AM
 */
@Component
public class DefaultFormProcessor implements FormProcessor, AppConstants {
    @Autowired
    protected JDBCService jdbcService;
    @Autowired
    protected MessageUtils messageUtils;

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected FormUtils formUtils;
    @Autowired
    protected FormViewUtils formViewUtils;
    @Autowired
    protected SpringUtils springUtils;


    @Override
    public void reload(ViewContext viewContext, Map saveEntity) {

    }

    @Override
    public Map<String, Object> load(ViewContext viewContext, Map condition) {
        return jdbcService.findData(condition);
    }

    @Override
    public boolean beforeStartProcess(ViewContext viewContext, Map<String, Object> elContext, Map saveEntity) {
        return true;
    }

    @Override
    public boolean beforeCompleteProcess(ViewContext viewContext, Map<String, Object> elContext, Map saveEntity) {
        return true;
    }


    @Override
    public String process(ViewContext viewContext, Map saveEntity) {
        String taskId = viewContext.getTaskId();
        String id = viewContext.getId();
        Map<String, List<Map<String, Object>>> messages = viewContext.getMessagesContext();
        //save data
        if (viewContext.getData() == null) {
            return "redirect:" + viewContext.getName();
        }
        //check for external tables
        HashMap joinedTableMap = new HashMap();
        for (String key : (Set<String>) saveEntity.keySet()) {
            if (key.startsWith($)) {
                joinedTableMap.put(key, saveEntity.get(key));
            }
        }
        for (Object key : joinedTableMap.keySet()) {
            saveEntity.remove(key);
        }


        if (id != null && StringUtils.isNotEmpty(id.toString())) {
            //update
            Map<String, String> map = new HashMap();
            map.put(DB_FIELD__ID, id);
            initUpdateInfo(saveEntity);
            jdbcService.updateData(map, saveEntity);
            saveEntity.putAll(map);
        } else {
            //create
            initCreateInfo(saveEntity);
            jdbcService.saveData(saveEntity);
            id = (String) saveEntity.get(DB_FIELD__ID);
        }

        ArrayList<Map<String, Object>> postUpdate = new ArrayList();
        for (String key : (Set<String>) joinedTableMap.keySet()) {
            Map<String, Object> field = formViewUtils.getField(viewContext, key);
            if (field.containsKey("join")) {
                Map<String, Object> join = (Map) field.get("join");
                String table = (String) join.get("table");
                String joinedKey = (String) join.get("column");
                String joinedField = (String) join.get("field");
                Object vals = ConvertUtils.getProperty(joinedTableMap, key);

                Map<String, String> map = new HashMap();
                map.put(joinedKey, id);
                if ("delete".equals(join.get("action"))) {
                    jdbcService.delete(table, map);
                    if (vals instanceof List) {
                        List list = (List) vals;
                        for (Object o : list) {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put(joinedKey, id);
                            data.put(joinedField, o);
                            if (Boolean.TRUE.equals(join.get("bindTask"))) {
                                data.put(PARAMETER_FIELD__TASKID, taskId);
                            }
                            initCreateInfo(data);
                            jdbcService.save(table, data);
                        }
                    } else {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(joinedKey, id);
                        data.put(joinedField, vals);
                        if (Boolean.TRUE.equals(join.get("bindTask"))) {
                            data.put(PARAMETER_FIELD__TASKID, taskId);
                        }
                        initCreateInfo(data);
                        jdbcService.save(table, data);
                    }
                } else {
                    Map update = jdbcService.find(table, new BasicDBObject(joinedKey, id));
                    if (update == null) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(joinedKey, id);
                        data.put(joinedField, vals);
                        if (Boolean.TRUE.equals(join.get("bindTask"))) {
                            data.put(PARAMETER_FIELD__TASKID, taskId);
                        }
                        initCreateInfo(data);
                        jdbcService.save(table, data);
                    } else {
                        update.put(joinedField, vals);
                        if (Boolean.TRUE.equals(join.get("bindTask"))) {
                            update.put(PARAMETER_FIELD__TASKID, taskId);
                        }
                        initUpdateInfo(update);
                        jdbcService.update(table, new BasicDBObject(DB_FIELD__ID, update.get(DB_FIELD__ID)), update);
                    }
                }


            }
        }
        //after update, should add the entity back to the save entity
        saveEntity.putAll(joinedTableMap);

        messageUtils.addMessage(messages, "success", viewContext);
        formUtils.copyValuesToViewMap(viewContext, saveEntity);
        if (viewContext.getRedirect() == null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("redirect:").append(viewContext.getName()).append("?id=").append(id);
            if (StringUtils.isNotEmpty(taskId)) {
                buffer.append("&_taskId=").append(taskId);
            }
            return buffer.toString();
        } else {
            return "redirect:" + String.valueOf(viewContext.getRedirect());
        }

    }


    protected void initCreateInfo(Map saveEntity) {
        saveEntity.put(AUDIT_CREATED_BY, getUserId(request));
        saveEntity.put(AUDIT_CREATED_TIME, new Date());
    }

    protected void initUpdateInfo(Map saveEntity) {
        saveEntity.put(AUDIT_UPDATED_BY, getUserId(request));
        saveEntity.put(AUDIT_UPDATED_TIME, new Date());
    }


    protected String getUserId(HttpServletRequest request) {
        Map map = (Map) request.getSession().getAttribute("user");
        return map == null ? null : String.valueOf(map.get(DB_FIELD__ID));
    }

}
