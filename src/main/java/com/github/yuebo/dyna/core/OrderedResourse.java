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
package com.github.yuebo.dyna.core;

import org.apache.commons.lang.StringUtils;

/**
 * User: yuebo
 * Date: 2018/7/1
 * Time: 12:20
 */
public class OrderedResourse implements Comparable<OrderedResourse> {
    private int order;
    private String name;
    public OrderedResourse(int order,String name){
        this.order=order;
        this.name=name;
    }

    public String getName() {
        return StringUtils.removeStart(this.name,"/static/");
    }

    @Override
    public int compareTo(OrderedResourse o) {
        return this.order-o.order;
    }
}
