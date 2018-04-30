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

import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by yuebo on 2017/12/22.
 */
@Component
public class CarMileageChartDatasourceProvider extends DefaultChartDatasourceProvider {
    @Autowired
    HttpServletRequest request;
    @Override
    protected List<Map<String, Object>> getDataList(String data) {

        String id=request.getParameter("_id");

        if(StringUtils.isEmpty(id)){
            id=request.getParameter("carId");
            if(StringUtils.isEmpty(id)){
                return Collections.emptyList();
            }
        }

        String sql= "select carId ,year(logDate) year,DATE_FORMAT(logDate, '%Y-%m')  yearmonth,sum(t.MILEAGE) amount,max(c.carNO) carNO from tbl_car_mileage t join tbl_car c on c._id=t.carId group by carId, year(logDate),DATE_FORMAT(logDate, '%Y-%m')";
        ListOrderedMap map=new ListOrderedMap();
        map.put("yearmonth","asc");
        int year=Calendar.getInstance().get(Calendar.YEAR);
        List<Map<String,Object>> results= jdbcService.queryForListWithFilter(sql,new BasicDBObject("year",year).append("carId",id),map,0,0);
        Map<String,Object> cache=new HashMap();

        if(results!=null){
           for (Map<String,Object> o:results){
               cache.put(MapUtils.getString(o,"yearmonth"),o);
           }
        }
        Map car=jdbcService.find("tbl_car",new BasicDBObject("_id",id));
        String carNo=MapUtils.getString(car,"carNo");
        List actualResult=new ArrayList();


        for (int i=1;i<=12;i++){
            String key=year+"-"+(i<10?"0"+i:i);
            Map<String,Object> objectMap=MapUtils.getMap(cache,key);
            if (objectMap==null){
                LinkedCaseInsensitiveMap test=new LinkedCaseInsensitiveMap();
                test.put("carId",id);
                test.put("year",year);
                test.put("yearmonth",key);
                test.put("amount",0);
                test.put("carNO",carNo);
                actualResult.add(test);
            }else {
                actualResult.add(objectMap);
            }
        }
        return actualResult;

    }

}
