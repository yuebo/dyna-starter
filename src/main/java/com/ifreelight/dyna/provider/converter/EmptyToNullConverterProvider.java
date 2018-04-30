/*
 *
 *  *
 *  *  * Copyright 2002-2017 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 *
 */

package com.ifreelight.dyna.provider.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yuebo on 2018/3/14.
 */
@Component("nullConverter")
public class EmptyToNullConverterProvider extends DefaultConvertProvider
{
    @Override
    protected Object convertItem(Object item, Map parameter) {
        if(item instanceof String){
            return StringUtils.isEmpty((String)item)?null:item;
        }
        return item;
    }

    @Override
    protected Object restoreItem(Object item, Map parameter) {
        if(item==null){
            return "";
        }
        return item;
    }
}
