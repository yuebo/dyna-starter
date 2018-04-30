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
import com.github.yuebo.dyna.core.ValidatorProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: yuebo
 * Date: 12/2/14
 * Time: 4:13 PM
 */
@Component("match")
public class MatchValidatorProvider implements ValidatorProvider,FileValidatorProvider {
    @Override
    public boolean validate(ValidateContext validateContext, Map fieldValueMap) {
        String field=validateContext.getField();
        String match=validateContext.getMatch();
        return getParameterValue(match,fieldValueMap)==null&& getParameterValue(match,fieldValueMap)==null?true: getParameterValue(field,fieldValueMap).equals(getParameterValue(match, fieldValueMap));

    }
}
