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
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.service.JDBCService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/2/14
 * Time: 9:50 AM
 */
@Component
public class MessageUtils implements AppConstants {
    @Autowired
    private JDBCService jdbcService;

    @SuppressWarnings("rawtypes")
    public void addErrorMessage(Map messages, String field, String key, ViewContext viewContext, Object... args) {
        String value=getMessage(viewContext,"error",key,args);
        if (args == null)
            args = new String[]{};
        List<Map<String, String>> error = (List<Map<String, String>>) messages.get("error");
        if (error == null) {
            messages.put("error", new ArrayList<Map<String, String>>());
        }
        error = (List<Map<String, String>>) messages.get("error");
        Map<String, String> errorMessage = new HashMap<String, String>();
//        errorMessage.put(field+"_"+key,MessageFormat.format(value, args));
        errorMessage.put("name", field);
        errorMessage.put("msg", MessageFormat.format(value, args));
        errorMessage.put("type", key);
        error.add(errorMessage);
//        ConvertUtils.setProperty(messages, "error."+field+"_" + key, MessageFormat.format(value, args));
    }

    public void addMessage(Map messages, String key, ViewContext viewContext, Object... args) {
        String value=getMessage(viewContext,"info",key,args);
        if (args == null)
            args = new String[]{};

        ConvertUtils.setProperty(messages, "info." + key, MessageFormat.format(value, args));


    }

    public String getMessage(ViewContext viewContext, String type, String key, Object... args){
        Map<String, String> infoMap = (Map) MapUtils.getObject(viewContext.getMessages(), type);
        String value = null;
        if (infoMap != null && infoMap.containsKey(key)) {
            value = infoMap.get(key);
        }
        if (value == null) {
            Map<String,String> map = new HashMap();
            map.put(DB_FIELD__DATA, TBL_MESSAGE);
            map.put("key", key);
            Map result = jdbcService.findData(map);
            if (result != null) {
                value = (String) result.get("value");
            }
        }
        if (value == null) {
            value = "no info message found for key " + key;
        }

        return value;
    }
}
