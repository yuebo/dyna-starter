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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: yuebo
 * Date: 2018/7/1
 * Time: 10:28
 */
@Component
public class RichTextComponent implements UIComponent {
    @Override
    public String getComponentName() {
        return "richtext";
    }

    @Override
    public List<OrderedResourse> getRequiredJS() {
        List<OrderedResourse> list=new ArrayList();
        list.add(new OrderedResourse(0,"/static/resources/tinymce/tinymce.min.js"));
        list.add(new OrderedResourse(10,"/static/resources/tinymce/jquery.tinymce.min.js"));
        return list;
    }
}
