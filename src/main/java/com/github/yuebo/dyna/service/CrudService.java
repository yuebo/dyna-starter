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
package com.github.yuebo.dyna.service;

import com.github.yuebo.dyna.service.jdbc.*;
import com.mongodb.BasicDBObject;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.yuebo.dyna.DbConstant.*;

/**
 * User: yuebo
 * Date: 2018/7/5
 * Time: 18:34
 * 用于仿照Spring data JPA来操作数据库
 */
@Service
public class CrudService<T>  implements NameMapper,CrudSupport<T>{
    @Autowired
    private JDBCService jdbcService;
    public T findById(String id){
        T t=instance();
        Map<String,Object> result=jdbcService.find(table().value(),new BasicDBObject("_id",id));
        return convert(result);
    }

    private T convert(Map<String, Object> result) {
        if (result==null){
            return null;
        }
        T t=instance();
        for (String key:result.keySet()){
            String name=convertToName(key);
            try {
                PropertyUtils.setProperty(t,name,result.get(key));
            } catch (Exception e) {
                continue;
            }
        }
        return t;
    }

    private T instance(){
        Class<T> tClass=(Class<T>) ResolvableType.forClass(getClass()).as(CrudSupport.class).getGeneric(0).resolve();
        T t=BeanUtils.instantiate(tClass);
        return t;
    }
    private Table table(){
        Class<T> tClass=(Class<T>) ResolvableType.forClass(getClass()).as(CrudSupport.class).getGeneric(0).resolve();

        Table table=tClass.getAnnotation(Table.class);
        if(StringUtils.isEmpty(table.value())){
            throw new RuntimeException("no table find");
        }
        return table;
    }

    public void save(T t){
        String id= id(t);
        Map bean=new BeanMap(t);
        BasicDBObject update=new BasicDBObject();
        for (Object key:bean.keySet()){
            if("class".equals(key)){
                continue;
            }
            update.put(convertToColumn(key.toString()),bean.get(key));
        }
        if(findById(id)!=null){
            jdbcService.update(table().value(),new BasicDBObject("_id",id),update);
        }else {
            jdbcService.save(table().value(),update);
        }
    }

    private String id(T t) {
        String id;
        try {
            id = (String)PropertyUtils.getProperty(t,"id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public void delete(T t){
        jdbcService.delete(table().value(),new BasicDBObject("_id",id(t)));
    }
    public void deleteById(String id){
        jdbcService.delete(table().value(),new BasicDBObject("_id",id));
    }

    public <DTO> List<T> findAll(DTO dto){
        Map filter=toParamMap(dto);
       List<Map<String,Object>> list=jdbcService.findList(table().value(),filter,null,0,0);
       return list.stream().map(result->convert(result)).collect(Collectors.toList());
    }
    public List<T> findAll(){
        return findAll(Collections.emptyMap());
    }

    public <DTO> Page<T> findPage(DTO dto, Page<T> page){
        LinkedMap linkedMap=new LinkedMap();
        if(StringUtils.isNotEmpty(page.getSort())){
            linkedMap.put(page.getSort(),page.getDir()?1:-1);
        }
        Map filter=toParamMap(dto);
        int total=jdbcService.count(table().value(),filter);
        List<Map<String,Object>> list=new ArrayList();
        if(total>0){
            list=jdbcService.findList(table().value(),filter,linkedMap,page.getPageSize(),page.getCurrentPage());
        }
        page.setTotalPages(total);
        page.setContent(list.stream().map(result->convert(result)).collect(Collectors.toList()));
        return page;
    }

    private <DTO>  Map toParamMap(DTO dto) {
        Map filter=new HashMap();
        Map beanMap=new BeanMap(dto);
        for (Object key:beanMap.keySet()){
            if("class".equals(key)){
                continue;
            }
            String column =convertToColumn(key.toString());
            Query query;
            try {
                query=dto.getClass().getDeclaredField(key.toString()).getAnnotation(Query.class);
            } catch (NoSuchFieldException e) {
                continue;
            }
            Object value=beanMap.get(key);
            if (query!=null&&(query.matchIfNull()||value!=null)){
                if (StringUtils.isNotEmpty(query.name())){
                    column=convertToColumn(query.name());
                }
                switch (query.match()){
                    case eq:
                        filter.put(column,value);
                        break;
                    case gt:
                        filter.put(column,new BasicDBObject($gt,value));
                        break;
                    case lt:
                        filter.put(column,new BasicDBObject($lt,value));
                        break;
                    case lte:
                        filter.put(column,new BasicDBObject($lte,value));
                        break;
                    case gte:
                        filter.put(column,new BasicDBObject($gte,value));
                        break;
                    case isnull:
                        filter.put(column,new BasicDBObject($null,null));
                        break;
                    case notnull:
                        filter.put(column,new BasicDBObject($null,false));
                        break;
                    case ne:
                        filter.put(column,new BasicDBObject($ne,value));
                        break;
                    case in:
                        filter.put(column,new BasicDBObject($in,value));
                        break;
                    case like:
                        filter.put(column,new BasicDBObject($like,value));
                        break;
                    default:
                        filter.put(column,value);
                }
            }

        }
        return filter;
    }
}
