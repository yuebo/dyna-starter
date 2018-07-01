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

import com.github.yuebo.dyna.core.OrderedResourse;
import com.github.yuebo.dyna.core.UIComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: yuebo
 * Date: 2018/7/1
 * Time: 10:20
 */
@Component
public class SelectComponent implements UIComponent {
    @Override
    public String getComponentName() {
        return "select";
    }

    @Override
    public boolean hasOptions() {
        return true;
    }

    @Override
    public boolean hasDefaultValue() {
        return true;
    }

    @Override
    public List<OrderedResourse> getRequiredJS() {
        List<OrderedResourse> list=new ArrayList();
        list.add(new OrderedResourse(20,"/static/resources/js/select2.min.js"));
        return list;
    }    @Override
    public List<OrderedResourse> getRequiredCss() {
        List<OrderedResourse> list=new ArrayList();
        list.add(new OrderedResourse(20,"/static/resources/css/select2.css"));
        list.add(new OrderedResourse(21,"/static/resources/css/select2-bootstrap.css"));
        return list;
    }
}
