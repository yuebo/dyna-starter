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
package com.github.yuebo.dyna.service.jdbc;

import org.apache.commons.lang.StringUtils;

/**
 * User: yuebo
 * Date: 2018/7/5
 * Time: 20:03
 */
public interface NameMapper {
     default  String convertToName(String column){
        String [] array=StringUtils.split(StringUtils.lowerCase(column),"_");
        StringBuilder stringBuilder=new StringBuilder();
        for (String s:array){
            stringBuilder.append(StringUtils.capitalize(s));
        }
        return StringUtils.uncapitalize(stringBuilder.toString());
    }
    default String convertToColumn(String name){
        String [] array=StringUtils.splitByCharacterTypeCamelCase(name);
        return StringUtils.upperCase(StringUtils.join(array,"_"));
    }
}
