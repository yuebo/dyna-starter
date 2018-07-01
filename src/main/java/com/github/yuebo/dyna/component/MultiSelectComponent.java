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
package com.github.yuebo.dyna.component;

import org.springframework.stereotype.Component;

/**
 * User: yuebo
 * Date: 2018/7/1
 * Time: 10:21
 */
@Component
public class MultiSelectComponent extends SelectComponent {
    @Override
    public boolean isMultiValue() {
        return true;
    }
    @Override
    public String getComponentName() {
        return "muti-select";
    }
    @Override
    public String getTemplateName() {
        return "/templates/form-select.vm";
    }

    @Override
    public boolean hasOptions() {
        return true;
    }
}