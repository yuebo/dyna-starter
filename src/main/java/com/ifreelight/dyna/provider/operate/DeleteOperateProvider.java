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

package com.ifreelight.dyna.provider.operate;

import com.ifreelight.dyna.core.OperateContext;
import com.ifreelight.dyna.core.ViewContext;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
@Component
public class DeleteOperateProvider extends DefaultOperateProvider {
    private Logger logger= LoggerFactory.getLogger(DeleteOperateProvider.class);

    @Override
    protected boolean doOperate(ViewContext viewContext, OperateContext operateContext) {

        String id=request.getParameter("_id");
        String table=viewContext.getData();

        Map<String,Object> parameter=operateContext.getParameter();

        List<Map<String,Object>> args= (List)MapUtils.getObject(parameter,"sub", Collections.emptyList());

        if(args.size()>0){
            for(Map<String,Object> sub:args){
                String subTable=MapUtils.getString(sub,"table");
                String joinFiled=MapUtils.getString(sub,"field");
                logger.debug("delete from table: {} with {}:{}",subTable,joinFiled,id);
                jdbcService.delete(subTable,new BasicDBObject(joinFiled,id));
            }
        }
        if(StringUtils.isNotEmpty(table)){
            logger.debug("delete from table: {} with {}:{}",table,"_id",id);
            jdbcService.delete(table,new BasicBSONObject("_id",id));
        }
        return false;
    }
}
