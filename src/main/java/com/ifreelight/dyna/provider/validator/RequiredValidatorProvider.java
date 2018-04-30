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

import com.ifreelight.dyna.core.FileValidatorProvider;
import com.ifreelight.dyna.core.ValidateContext;
import com.ifreelight.dyna.core.ValidatorProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by yuebo on 2014/11/30.
 */
@Component("required")
public class RequiredValidatorProvider implements ValidatorProvider, FileValidatorProvider {
    @Override
    public boolean validate(ValidateContext validateContext, Map fieldValueMap) {
        Object value = fieldValueMap.get(validateContext.getField());
        if (value instanceof Object[]) {
            value = ((Object[]) value)[0];
        }
        if (value instanceof MultipartFile) {
            return ((MultipartFile) value).getSize() > 0;
        }
        if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
            return false;
        }
        return true;

    }
}
