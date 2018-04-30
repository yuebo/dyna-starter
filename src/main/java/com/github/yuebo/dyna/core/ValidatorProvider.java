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

import java.util.Map;

/**
 * An interface for validation framework to validate the data in online or file upload.
 * Created by yuebo on 2014/11/30.
 */
public interface ValidatorProvider {
    /**
     * the validation method
     * @param validateContext
     *          validate context which contains the parameter etc.
     * @param fieldValueMap
     *          all the field and values which user submitted to file uploaded
     * @return
     *          true if the validation passed
     */
    default public boolean validate(ValidateContext validateContext, Map fieldValueMap) {
        return true;
    }

    /**
     * To get the value from the field parameter map, for online, the fieldValueMap equals request.getParameterMap()
     * @param field
     *          the field name
     * @param fieldValueMap
     *          the field value map which passed by online or file upload
     * @return
     *          the field value in the fieldValueMap
     */
    default Object getParameterValue(String field, Map fieldValueMap){
        Object v=fieldValueMap.get(field);
        if(v==null){
            return null;
        }
        if(v instanceof String []){
           return ((String[])v)[0];
        }else {
            return v;
        }
    }
    /**
     * To get the values from the field parameter map, for online, the fieldValueMap equals request.getParameterMap()
     * @param field
     *          the field name
     * @param fieldValueMap
     *          the field value map which passed by online or file upload
     * @return
     *          the field values in the fieldValueMap
     */
    default Object[] getParameterValues(String field, Map fieldValueMap){
        Object v=fieldValueMap.get(field);
        if(v==null){
            return null;
        }
        if(v instanceof Object[]){
            return (Object[])v;
        }else {
            return new Object[]{v};
        }
    }
}
