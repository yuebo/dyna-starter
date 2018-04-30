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

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/7.
 */
@Component
public class ValuesConvertProvider extends DefaultConvertProvider {
    @Override
    protected Object restoreItem(Object item, Map parameter) {
        Map<String,String> values= MapUtils.getMap(parameter,"values", Collections.emptyMap());
        return values.get(String.valueOf(item));
    }

    @Override
    protected Object convertItem(Object item, Map parameter) {
        Map<String,String> values= MapUtils.getMap(parameter,"values", Collections.emptyMap());
        if(values!=null){
            Map<String,String> reverseValues=new HashMap();
            for(String key:values.keySet()){
                reverseValues.put(values.get(key),key);
            }
            return reverseValues.get(String.valueOf(item));
        }
        return null;
    }
}
