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

package com.github.yuebo.dyna.provider.option;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.ConvertContext;
import com.github.yuebo.dyna.core.ConvertProvider;
import com.github.yuebo.dyna.core.OptionProvider;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.utils.ConvertUtils;
import com.github.yuebo.dyna.utils.FormViewUtils;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by yuebo on 9/11/2017.
 */
@Component("defaultOptionProvider")
public class DefaultOptionProvider implements OptionProvider {
    private static Logger logger= LoggerFactory.getLogger(DefaultOptionProvider.class);
    @Autowired
    protected JDBCService jdbcService;
    @Autowired
    protected FormViewUtils formViewUtils;

    @Autowired
    protected SpringUtils springUtils;

    @Override
    public List<Map<String, String>> option(ViewContext viewContext, Map item, Map param) {
        List<Map<String, String>> optionList = new ArrayList<>();
        int maxSize=0;

        if(param.containsKey("maxSize")){
            maxSize=MapUtils.getIntValue(param,"maxSize");
        }
        Map query = new HashMap();
        if (param.get("query") != null) {
            query.putAll((Map) param.get("query"));
        }

        if (param.get("depends") != null) {
            List<Map<String, Object>> fields = (List) param.get("depends");
            for (Map field : fields) {
                Map<String, Object> f = formViewUtils.getField(viewContext, (String) field.get("depend"));
                if (f != null) {
                    Object value = ConvertUtils.getProperty(f, "attributes.value");
                    if(value!=null){
                        if(field.get(AppConstants.VIEW_FIELD_FIELDS_OPERATOR)!=null){
                            query.put(field.get("name"),new BasicDBObject(MapUtils.getString(field, AppConstants.VIEW_FIELD_FIELDS_OPERATOR),value));
                        }else {
                            query.put(field.get("name"), value);
                        }
                    }else {
                        query.put(field.get("name"), null);
                    }


                }
            }
        }
        ListOrderedMap sort = new ListOrderedMap();
        Map sorted = (Map) param.get("sort");
        if (sorted != null) {
            sort.put((String) sort.get("name"), sorted.get("order"));
        }

        String table = (String) query.get("_data");
        query.remove("_data");
        List<Map<String, Object>> result= Collections.emptyList();
        if(table!=null){
            try {
                result=findOptions(viewContext,table,query,sort,maxSize,0);
            }catch (Exception e){
                logger.error("opetion provider error at view {}: {}",viewContext.getName(),e.getMessage());
            }
        }


        //processConverter
        boolean hasConverter=false;
        if(param.get("converter")!=null&&result!=null){

            List<Map<String, Object>> resultTemp=new ArrayList();

            ConvertProvider convertProvider=springUtils.getConvertProvider((Map) param.get("converter"));
            hasConverter=true;
            for (Map<String,Object> val:result){
                Map<String,Object> tempValue=new LinkedCaseInsensitiveMap();
                tempValue.putAll(val);
                String key= MapUtils.getString(param,"key");
//                String value= MapUtils.getString(param,"value");
                Object object=convertProvider.restore(MapUtils.getObject(val,key),new ConvertContext(MapUtils.getMap(param,"converter")));
                tempValue.put(key.concat("_converted"),object);
                resultTemp.add(tempValue);
            }
            result=resultTemp;

        }

        if (result == null || result.size() == 0) {
            return optionList;
        }

        for (Map m : result) {
            Map<String, String> kv = new LinkedCaseInsensitiveMap();
            kv.put(MapUtils.getString(m,MapUtils.getString(param,"key")), hasConverter?MapUtils.getString(m,MapUtils.getString(param,"key").concat("_converted")):MapUtils.getString(m,MapUtils.getString(param,"value")));
            optionList.add(kv);
        }

        return optionList;
    }

    protected List<Map<String,Object>> findOptions(ViewContext viewContext, String table, Map query, ListOrderedMap sort, int i, int i1) {
        List<Map<String, Object>> result = jdbcService.findList(table, query, sort, 0, 0);
        return result;

    }

    private boolean containsKey(List<Map<String, String>> optionList, String value) {
        boolean result = false;
        for (Map<String, String> s : optionList) {
            if (s.containsKey(value)) {
                return true;
            }
        }
        return result;


    }
}
