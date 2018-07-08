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

package com.github.yuebo.dyna.provider.converter;

import com.github.yuebo.dyna.service.JDBCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuebo on 23/11/2017.
 */
@Component("dataConverter")
public class DefaultDataConvertProvider extends DefaultConvertProvider {
    @Autowired
    JDBCService jdbcService;

    @Override
    protected Object restoreItem(Object item, Map parameter) {
        Map<String, Object> filter = new HashMap();
        String table = (String) parameter.get("table");
        String column = (String) parameter.get("column");
        filter.put("id", (String) item);
        Map result = jdbcService.find(table, filter);
        if (result == null) {
            return null;
        }
        return result.get(column);
    }

    @Override
    protected Object convertItem(Object item, Map parameter) {
        Map<String, Object> filter = new HashMap();
        String table = (String) parameter.get("table");
        String column = (String) parameter.get("column");
        filter.put(column, item);
        Map result = jdbcService.find(table, filter);
        if (result == null) {
            return null;
        }
        return result.get("id");

    }
}
