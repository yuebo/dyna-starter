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

package com.github.yuebo.dyna.provider.express;

import com.github.yuebo.dyna.core.ExpressionProvider;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.github.yuebo.dyna.utils.UserUtils;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.github.yuebo.dyna.AppConstants.DB_FIELD__ID;

/**
 * Created by yuebo on 17/11/2017.
 */
@Component
public class DefaultExpressionProvider implements ExpressionProvider {
    @Autowired
    SpringUtils springUtils;

    @Autowired
    JDBCService jdbcService;
    @Autowired
    UserUtils userUtils;

    @Override
    public Map<String, Object> getValue(Map<String, Object> parameter, Map<String, Object> context) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        for (String key : context.keySet()) {
            evaluationContext.setVariable(key, context.get(key));
        }
        evaluationContext.setRootObject(this);
        HashMap map = new HashMap();
        for (String key : parameter.keySet()) {
            String value = (String) parameter.get(key);
            map.put(key, springUtils.execute(evaluationContext, value));
        }
        return map;
    }

    public Object id(String table, String name, String value) {
        Map param = new HashMap();
        param.put("_data", table);
        param.put(name, value);
        return jdbcService.findData(param).get(DB_FIELD__ID);
    }
    public Object find(String table,String column,Object ... filter){

        Map<String,Object> param=new HashMap();
        for(int i=0;i<filter.length-1;i+=2){
           String key=String.valueOf(filter[i]);
           param.put(key,filter[i+1]);
        }
        return MapUtils.getObject(jdbcService.find(table,param),column);
    }
    public List<Object> list(String table, String column, Object ... filter){

        Map<String,Object> param=new HashMap();
        for(int i=0;i<filter.length-1;i+=2){
            String key=String.valueOf(filter[i]);
            param.put(key,filter[i+1]);
        }
        List<Object> results=new ArrayList();

        List<Map<String,Object>> result=jdbcService.findList(table,param,null,0,0);

        if(result!=null){
            for(Map<String,Object> map:result){
                results.add(MapUtils.getObject(map,column));
            }
        }
        return results;
    }

    public Date now(){
        return new Date();
    }
    public Map<String,Object> map(String key,Object value){
        return new BasicDBObject(key,value);
    }

    public String getUserId(){
        return MapUtils.getString(userUtils.currentUser(),DB_FIELD__ID);
    }

    public String getUserName(){
        return MapUtils.getString(userUtils.currentUser(),"name");
    }
}
