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

package com.ifreelight.dyna.provider.chart;

import com.ifreelight.dyna.core.ChartContext;
import com.ifreelight.dyna.core.ChartDataSourceProvider;
import com.ifreelight.dyna.service.JDBCService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yuebo on 2017/12/18.
 */
@Component
public class DefaultChartDatasourceProvider implements ChartDataSourceProvider {
    private static Logger logger= LoggerFactory.getLogger(DefaultChartDatasourceProvider.class);

    @Autowired
    protected JDBCService jdbcService;

    protected ChartContext chartContext;

    @Override
    public String toJson(ChartContext chartContext) {
        this.chartContext=chartContext;

        Map<String,Object> datasource=chartContext.getDataSource();

        Map<String,Object> parameter=MapUtils.getMap(datasource,"parameter", Collections.emptyMap());

        List<String> groups=(List)MapUtils.getObject(parameter,"groups",Collections.emptyList());
        String data=MapUtils.getString(parameter,"sql");
        String legendGroup=MapUtils.getString(parameter,"legendGroup");
        String seriesGroup=MapUtils.getString(parameter,"seriesGroup");
        String seriesType=MapUtils.getString(parameter,"seriesType","bar");
        String valueField=MapUtils.getString(parameter,"valueField");
        List<String> legend=new ArrayList();
        List<String> series=new ArrayList();
        List<Map<String,Object>> mapList=new ArrayList();



        List<Map<String,Object>> result=Collections.emptyList();

        try {
            result=getDataList(data);
        }catch (Exception e){
            logger.error("error to query the data {}: {}",chartContext.getViewContext().getName(),e.getMessage());
        }


        Map<String,Object> cache=new HashMap();

        if(result!=null){
            for(Map<String,Object> r:result){
                String l=MapUtils.getString(r,legendGroup, StringUtils.EMPTY);
                if(!legend.contains(l)){
                    legend.add(l);
                }
                String s=MapUtils.getString(r,seriesGroup, StringUtils.EMPTY);
                if(!series.contains(s)){
                    series.add(s);
                }
                String key=l+"@"+s;
                cache.put(key,MapUtils.getNumber(r,valueField));
            }
            chartContext.setLegendData(legend);
            chartContext.setXAxisData(series);
            if("pie".equals(seriesType)) {
                for(String s:series){
                    Map<String,Object> serie=new HashMap();
                    ArrayList list=new ArrayList();
                    for(String l:legend){
                        String key=l+"@"+s;
                        Map<String,Object> values=new HashMap();
                        values.put("name",l);
                        values.put("value",MapUtils.getNumber(cache,key,0));
                        list.add(values);
                    }
                    serie.put("name",s);
                    serie.put("type",seriesType);
                    serie.put("data",list);
                    mapList.add(serie);
                }
            }else{

                for(String l:legend){
                    Map<String,Object> serie=new HashMap();
                    ArrayList list=new ArrayList();
                    for(String s:series){
                        String key=l+"@"+s;
                        Map<String,Object> values=new HashMap();
                        values.put("name",s);
                        values.put("value",MapUtils.getNumber(cache,key,0));
                        list.add(values);
                    }
                    serie.put("name",l);
                    serie.put("type",seriesType);
                    serie.put("data",list);
                    mapList.add(serie);
                }

            }

            chartContext.setOptionSeries(mapList);
        }

        return chartContext.toJsonOption();
    }
    protected List<Map<String,Object>> getDataList(String data) {
        List<Map<String,Object>> result=jdbcService.queryForList(data,Collections.emptyMap(),null,0,0);
        return result;
    }
}
