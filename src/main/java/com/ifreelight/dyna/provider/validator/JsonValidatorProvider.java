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

package com.ifreelight.dyna.provider.validator;

import com.alibaba.fastjson.JSON;
import com.ifreelight.dyna.core.ValidateContext;
import com.ifreelight.dyna.core.ValidatorProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yuebo on 2017/12/5.
 */
@Component("json")
public class JsonValidatorProvider implements ValidatorProvider {
    @Override
    public boolean validate(ValidateContext validateContext, Map fieldValueMap) {

        String value=(String)getParameterValue(validateContext.getField(),fieldValueMap);
        if(StringUtils.isEmpty(value)){
            return true;
        }
        try {
            JSON.parse(value);
            return true;
        }catch (Exception e){
            return false;
        }


    }
}
