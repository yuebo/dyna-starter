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

package com.github.yuebo.dyna.core;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/18.
 */
public class ChartContext {
    private Map<String,Object> chart;
    private ViewContext viewContext;
    public ChartContext(Map<String, Object> chart) {
        this.chart=chart;
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public void setViewContext(ViewContext viewContext) {
        this.viewContext = viewContext;
    }

    public String toJsonOption(){
        Map<String,Object> option= MapUtils.getMap(chart,"option");
        return JSON.toJSONString(option);
    }

    public Map<String,Object> getDataSource(){
        Map<String,Object> datasource= MapUtils.getMap(chart,"datasource");
        return datasource;
    }

    public void setOptionSeries(List<Map<String,Object>> mapList){
        Map<String,Object> option= MapUtils.getMap(chart,"option",new HashMap());
        option.put("series",mapList);
    }

    public void setXAxisData(List<String> xAxisData) {
        Map<String,Object> option= MapUtils.getMap(chart,"option",new HashMap());
        Map<String,Object> xAxis=MapUtils.getMap(option,"xAxis",new HashMap());
        xAxis.put("data",xAxisData);
    }

    public void setLegendData(List<String> legendData) {
        Map<String,Object> option= MapUtils.getMap(chart,"option",new HashMap());
        Map<String,Object> legend=MapUtils.getMap(option,"legend",new HashMap());
        legend.put("data",legendData);
    }
}
