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

import com.github.yuebo.dyna.core.FileValidatorProvider;
import com.github.yuebo.dyna.core.ValidateContext;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yuebo on 2017/12/11.
 */
@Component("length")
public class LengthValidator implements FileValidatorProvider {
    @Override
    public boolean validate(ValidateContext validateContext, Map fieldValueMap) {
        Map  parameter=validateContext.getParameter();
        String value=(String)getParameterValue(validateContext.getField(),fieldValueMap);
        Integer max= MapUtils.getInteger(validateContext.getParameter(),"max");
        Integer min= MapUtils.getInteger(validateContext.getParameter(),"min");
        int length= StringUtils.length(value);

        if(min!=null&&length<min){
            return false;
        }

        if(max!=null&&length>max){
            return false;
        }

        return true;
    }
}
