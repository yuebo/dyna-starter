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

package com.github.yuebo.dyna.provider.validator;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.ValidateContext;
import com.github.yuebo.dyna.core.ValidatorProvider;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/3/14
 * Time: 2:03 PM
 */
@Component("unique")
public class UniqueValidatorProvider implements ValidatorProvider, AppConstants {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JDBCService jdbcService;

    @Override
    public boolean validate(ValidateContext validateContext, Map fieldValueMap) {
        String value = request.getParameter(validateContext.getField());
        String fieldName = validateContext.getField();
        HashMap hashMap = new HashMap();

        if (request.getParameter(DB_FIELD__ID) != null) {
            hashMap.put(DB_FIELD__ID, new BasicDBObject($ne, request.getParameter(DB_FIELD__ID)));
        }
        String table=validateContext.getViewContext().getData();
        hashMap.put(DB_FIELD__DATA, table);

        hashMap.put(fieldName,value);
        //need to check if the table if it is exist, return true if the is no table
        boolean hasTable=jdbcService.getGenerator().checkTable(table);
        if(!hasTable){
            return true;
        }
        boolean hasField=jdbcService.getGenerator().checkColumns(table).contains(StringUtils.upperCase(fieldName));
        if(!hasField){
            return true;
        }

        Map result = jdbcService.findData(hashMap);

        if (result == null || result.isEmpty()) {
            return true;
        }

        return false;
    }
}
