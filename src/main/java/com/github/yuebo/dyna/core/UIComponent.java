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

import java.util.Collections;
import java.util.List;

/**
 * User: yuebo
 * Date: 1/7/18
 * Time: 10:13 AM
 * 表单组件接口
 */
public interface UIComponent {
    /**
     * 是否支持多选
     * @return
     */
    default boolean isMultiValue(){
        return false;
    }

    /**
     * 组件名称
     * @return
     */
    String getComponentName();

    /**
     * 组件模板
     * @return
     */
    default String getTemplateName(){
        return "/templates/form-".concat(getComponentName()).concat(".vm");
    }
    default List<OrderedResourse> getRequiredJS(){
        return Collections.emptyList();
    }
    default List<OrderedResourse> getRequiredCss(){
        return Collections.emptyList();
    }

    /**
     * 是否是可旋转类型
     * @return
     */
    default boolean isCheckedComponent(){
        return false;
    }
    default boolean isValidationRequired(){
        return true;
    }

    /**
     * 是否是二进制类型
     * @return
     */
    default boolean isBinaryType(){return false;}
    default boolean hasDefaultValue(){
        return false;
    }

    /**
     * 是否有选项
     * @return
     */
    default boolean hasOptions(){
        return false;
    }

    /**
     * 组件不需要生成标签
     * @return
     */
    default boolean isNoLabel(){
        return false;
    }

    /**
     * 是否是动态值，动态值的会被change事件刷新组件
     * @return
     */
    default boolean isDynamicValue(){
        return false;
    }


    default String getCustomOnChangeTemplate(){
        return "";
    }
}
