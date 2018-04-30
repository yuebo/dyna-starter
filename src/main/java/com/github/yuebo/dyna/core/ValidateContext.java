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

package com.github.yuebo.dyna.core;

import com.github.yuebo.dyna.AppConstants;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * Created by yuebo on 29/11/2017.
 */
public class ValidateContext implements AppConstants {
    private ViewContext viewContext;
    private Map<String, Object> validator;

    public ValidateContext(ViewContext viewContext, Map<String, Object> validator) {
        this.validator = validator;
        this.viewContext = viewContext;
    }

    public String getField() {
        return MapUtils.getString(validator, VIEW_FIELD_VALIDATORS_FIELD);
    }

    public String getProvider() {
        return MapUtils.getString(validator, VIEW_FIELD_VALIDATORS_PROVIDER);
    }

    public String getMessage() {
        return MapUtils.getString(validator, VIEW_FIELD_VALIDATORS_MSG);
    }

    public Boolean isExternal() {
        return MapUtils.getBoolean(validator, VIEW_FIELD_VALIDATORS_EXTERNAL, false);
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public String getMatch(){
        return MapUtils.getString(validator, VIEW_FIELD_VALIDATORS_MATCH);
    }

    public Map<String,Object> getParameter(){
        Map result= MapUtils.getMap(this.validator,"parameter");
        if(result==null){
            return MapUtils.EMPTY_MAP;
        }
        return result;
    }
}
