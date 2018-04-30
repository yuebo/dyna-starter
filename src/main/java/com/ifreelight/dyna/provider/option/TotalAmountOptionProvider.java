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

package com.ifreelight.dyna.provider.option;

import com.ifreelight.dyna.core.ViewContext;
import com.ifreelight.dyna.utils.ConvertUtils;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yuebo on 2017/12/21.
 */
@Component
public class TotalAmountOptionProvider extends DefaultOptionProvider {
    private static Logger logger= LoggerFactory.getLogger(TotalAmountOptionProvider.class);
    @Override
    protected List<Map<String, Object>> findOptions(ViewContext viewContext, String table, Map query, ListOrderedMap sort, int i, int i1) {
        List<Map<String, Object>> result=new ArrayList();

        String carId=MapUtils.getString(query,"carId");
        if(StringUtils.isEmpty(carId)){
            return Collections.emptyList();

        }

        Calendar calendar=Calendar.getInstance();
        int year=Calendar.getInstance().get(Calendar.YEAR)+MapUtils.getIntValue(query,"year",0);
        calendar.set(Calendar.MONTH,calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DATE));
        BasicDBObject param=new BasicDBObject("carId",carId);
        calendar.set(Calendar.YEAR,year);
        ConvertUtils.setProperty(param,"requestDate.$lte",calendar.getTime());
        calendar.set(Calendar.YEAR,year-1);
        ConvertUtils.setProperty(param,"requestDate.$gt",calendar.getTime());
        param.append("status",MapUtils.getString(query,"status","审核通过"));
        logger.info(param.toString());
        List<Map<String,Object>> tempResult= jdbcService.queryForListWithFilter("select sum(amount) as amount from tbl_car_topup",param,null,0,0,true);
        if(tempResult.size()>0){
            result.add(new BasicDBObject("amount",MapUtils.getIntValue(tempResult.get(0),"amount",0)));
        }
        return result;

    }
}
