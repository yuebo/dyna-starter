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

package com.github.yuebo.dyna.provider.operate;

import com.github.yuebo.dyna.core.OperateContext;
import com.github.yuebo.dyna.core.ViewContext;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2018/3/8.
 */
@Component
public class MoveDownFieldProvider extends DefaultOperateProvider {

    @Override
    protected boolean doOperate(ViewContext viewContext, OperateContext operateContext) {

        String data=(String)operateContext.getParameter().getOrDefault("data",viewContext.getData());
        Map<String,Object> currentRow=jdbcService.findData(new BasicDBObject("_data",data).append("id",viewContext.getId()));
        ListOrderedMap sort=new ListOrderedMap();
        sort.put("seq",getSortOrder());
        List<Map<String,Object>> nextRowList=jdbcService.findList(data,new BasicDBObject("seq",new BasicDBObject(getCondition(), MapUtils.getIntValue(currentRow,"seq"))),sort,1,0);
        if(nextRowList!=null){
            Object seq=currentRow.get("seq");
            jdbcService.update(data,new BasicDBObject("id",viewContext.getId()),new BasicDBObject("seq",nextRowList.get(0).get("seq")));
            jdbcService.update(data,new BasicDBObject("id",nextRowList.get(0).get("id")),new BasicDBObject("seq",seq));
        }else {
            throw new RuntimeException("无法移动字段");
        }
        return super.doOperate(viewContext, operateContext);
    }

    protected String getCondition() {
        return $gt;
    }
    protected String getSortOrder(){
        return "asc";
    }
}
