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

import com.github.yuebo.dyna.core.UIComponent;
import org.springframework.stereotype.Component;

/**
 * User: yuebo
 * Date: 2018/7/1
 * Time: 10:56
 */
@Component
public class DynaLabelComponent implements UIComponent {
    @Override
    public String getComponentName() {
        return "dyna-label";
    }

    @Override
    public boolean isValidationRequired() {
        return false;
    }

    @Override
    public boolean hasOptions() {
        return true;
    }

    @Override
    public boolean isDynamicValue() {
        return true;
    }

    @Override
    public String getCustomOnChangeTemplate() {
        return "/templates/onchange-dyna-label.vm";
    }
}
