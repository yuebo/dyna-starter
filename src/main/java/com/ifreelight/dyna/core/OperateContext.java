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

package com.ifreelight.dyna.core;

import com.ifreelight.dyna.AppConstants;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
public class OperateContext implements AppConstants {
    private Map<String,Object> operateMap;
    public OperateContext(Map<String,Object> operateMap){
        this.operateMap=operateMap;
    }

    public String getProvider() {
        return MapUtils.getString(operateMap,VIEW_FIELD_OPERATE_PROVIDER);
    }

    public Map<String,Object> getParameter(){
        return MapUtils.getMap(operateMap,VIEW_FIELD_OPERATE_PARAMETER,MapUtils.EMPTY_MAP);
    }
}
