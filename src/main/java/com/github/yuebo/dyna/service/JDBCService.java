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

import com.github.yuebo.dyna.DbConstant;
import com.github.yuebo.dyna.core.NullDate;
import com.github.yuebo.dyna.core.NullNumber;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.*;

import static com.github.yuebo.dyna.AppConstants.DB_FIELD__ID;

/**
 * Created by yuebo on 5/18/2015.
 */
public class JDBCService implements DbConstant {
    private static Logger logger = LoggerFactory.getLogger(JDBCService.class);
    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected TableGenerator generator;

    public void setGenerator(TableGenerator generator) {
        this.generator = generator;
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Map find(String table, Map param) {
        boolean tableExist = generator.checkTable(table);
        if (!tableExist)
            return null;
        StringBuffer buffer = new StringBuffer("select * from ");
        buffer.append(table);
        buffer.append(" ");
        if (!param.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(param));
        }

        List<Map<String, Object>> result = queryForList(buffer.toString(), param, null, 1, 0);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;

    }

    public Map findData(Map param) {
        String data = (String) param.get("_data");
        HashMap temp = new HashMap();
        temp.putAll(param);
        temp.remove("_data");
        return find(data, temp);
    }

    protected String prepareCondition(Map param) {
        if (param.isEmpty()) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        int a = 0;
        Set<String> keys=(Set<String>) param.keySet();
        //fix modify the hashmap in a loop
        List<String> list=new ArrayList();
        list.addAll(keys);
        for (String key : list) {

            if (a > 0) {
                buffer.append(" and ");
            }
            if ("$or".equals(key)) {
                int c = 0;
                for (Map map : (List<Map>) param.get(key)) {

                    if (c > 0) {
                        buffer.append(" or ");
                    }
                    buffer.append("(");
                    int d = 0;
                    for (String key3 : (Set<String>) map.keySet()) {
                        if (d > 0)
                            buffer.append(" and ");
                        sqlAppend(buffer, key3, map);
                        d++;
                    }
                    buffer.append(") ");
                    c++;
                }


            } else {
                sqlAppend(buffer, key, param);
            }
            a++;

        }
        return buffer.toString();
    }

    protected void sqlAppend(StringBuffer buffer, String key, Map param) {
        buffer.append("\"").append(StringUtils.upperCase(key)).append("\" ");
        if (param.get(key) instanceof Map) {
            Map nestMap = (Map) param.get(key);
            if (nestMap.containsKey($eq)) {
                buffer.append("=:").append(key);
                param.put(key, nestMap.get($eq));
            } else if (nestMap.containsKey($lt)) {
                buffer.append("<:").append(key);
                param.put(key, nestMap.get($lt));
            } else if (nestMap.containsKey($gt)) {
                buffer.append(">:").append(key);
                param.put(key, nestMap.get($gt));
            } else if (nestMap.containsKey($lte)) {
                buffer.append("<=:").append(key);
                param.put(key, nestMap.get($lte));
            } else if (nestMap.containsKey($gte)) {
                buffer.append(">=:").append(key);
                param.put(key, nestMap.get($gte));
            } else if (nestMap.containsKey($ne)) {
                buffer.append("<>:").append(key);
                param.put(key, nestMap.get($ne));
            } else if (nestMap.containsKey($in)) {
                buffer.append("in (:").append(key).append(")");
                param.put(key, nestMap.get($in));
            } else if (nestMap.containsKey($like)) {
                buffer.append("like '%'||:").append(key).append("||'%' escape '\\'");
                param.put(key, escapeLike((String) nestMap.get($like)));
            }

        } else {
            buffer.append("=:").append(key);
            param.put(key, param.get(key));
        }

    }

    protected String escapeLike(String $like) {
        return StringUtils.replaceEach($like, new String[]{
                "%", "_"
        }, new String[]{"\\%", "\\_"});
    }


    public void save(String collection, Map o) {
        ensureTable(collection);
        ensureColumns(collection, o);
        if (o.get(DB_FIELD__ID) == null) {
            o.put(DB_FIELD__ID, new ObjectId().toHexString());
        }
        StringBuffer buffer = new StringBuffer("insert into ");
        buffer.append(collection);
        buffer.append("(");
        int i = 0;
        StringBuffer values = new StringBuffer(") values(");
        for (String key : (Set<String>) o.keySet()) {
            if (i > 0) {
                buffer.append(",");
                values.append(",");
            }
            buffer.append("\"");
            buffer.append(StringUtils.upperCase(key));
            buffer.append("\"");
            values.append(":");
            values.append(key);

            i++;
        }
        values.append(")");
        buffer.append(values);

        logger.info(buffer.toString());
        Map param = convertUpdateParam(o);
        jdbcTemplate.update(buffer.toString(), param);


    }

    protected Map convertUpdateParam(Map o) {
        HashMap map = new HashMap();
        for (String field : (Set<String>) o.keySet()) {
            Object val = o.get(field);
            if (val instanceof NullDate || val instanceof NullNumber) {
                map.put(field, null);
            } else {
                map.put(field, val);
            }
        }


        return map;
    }


    public int ensureLength(String collection,String field,int length){

        return length;
    }

    protected void ensureColumns(String collection, Map o) {
        for (String key : (Set<String>) o.keySet()) {
            if (!generator.checkColumns(collection).contains(StringUtils.upperCase(key))) {
                StringBuffer buffer = new StringBuffer("alter table ");
                buffer.append(collection);
                buffer.append(" add ");
                buffer.append("\"");
                buffer.append(StringUtils.upperCase(key));
                buffer.append("\" ");
                if (o.get(key) instanceof Date) {
                    buffer.append("date");
                } else if (o.get(key) instanceof Number) {
                    buffer.append("number");
                } else {
                    buffer.append("varchar2(1000)");
                }
                logger.info(buffer.toString());
                jdbcTemplate.getJdbcOperations().execute(buffer.toString());
                generator.removeMetaCache();
            }
        }


    }

    protected void ensureTable(String collection) {
        if (!generator.checkTable(collection)) {
            //create table with _id column only
            StringBuffer buffer = new StringBuffer("create table ");
            buffer.append(collection);
            buffer.append(" ( \"ID\" varchar2(50) primary key)");
            logger.info(buffer.toString());
            jdbcTemplate.getJdbcOperations().execute(buffer.toString());
            generator.removeMetaCache();
        }
    }


    public void saveData(Map o) {
        String table = (String) o.get("_data");
        Map temp = new HashMap<>();
        temp.putAll(o);
        temp.remove("_data");
        save(table, temp);
        o.putAll(temp);

    }

    public int count(String table, Map filter) {
        if (!generator.checkTable(table))
            return 0;
        StringBuffer buffer = new StringBuffer("select count(1) cnt from ");
        buffer.append(table);
        if (!filter.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(filter));
        }
        logger.info(buffer.toString());
        Map result = jdbcTemplate.queryForMap(buffer.toString(), filter);
        Object o = result.get("CNT");
        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).intValue();
        }
        if (o instanceof Long) {
            return ((Long) o).intValue();
        }
        if (o instanceof Integer) {
            return (int) o;
        }
        throw new RuntimeException("Invalid return value");
    }

    public List<Map<String, Object>> findList(String table, Map filterParam, OrderedMap sort, int limit, int skip) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        if (!generator.checkTable(table))
            return null;
        StringBuffer buffer = new StringBuffer("select rownum as sn,a.* from ");
        buffer.append(table);
        buffer.append(" a ");
        if (!filter.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(filter));
        }

        if (sort != null && !sort.isEmpty()) {
            buffer.append(" order by ");
            int i = 0;
            for (String key : (Set<String>) sort.keySet()) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append("\"").append(StringUtils.upperCase(key)).append("\"");
                String sortDir = (String) sort.get(key);
                if ("-1".equals(sortDir) || "desc".equalsIgnoreCase(sortDir)) {
                    buffer.append(" desc");
                }
                i++;
            }

        }
        if (skip > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where sn>:skip");
            filter.put("skip", skip);
            buffer = buffer2;
        }

        if (limit > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where rownum<=:limit");
            filter.put("limit", limit);
            buffer = buffer2;
        }

        logger.info(buffer.toString());
        return jdbcTemplate.queryForList(buffer.toString(), filter);
    }

    public List<Map<String, Object>> queryForList(String sql, Map filterParam, OrderedMap sort, int limit, int skip) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        StringBuffer buffer = new StringBuffer("select rownum as sn, a.* from (");
        buffer.append(sql);
        buffer.append(") a ");
        if (sort != null && !sort.isEmpty()) {
            buffer.append(" order by ");
            int i = 0;
            for (String key : (Set<String>) sort.keySet()) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(key);
                String sortDir = (String) sort.get(key);
                if ("-1".equals(sortDir) || "desc".equalsIgnoreCase(sortDir)) {
                    buffer.append(" desc");
                }
                i++;
            }

        }
        if (skip > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where sn>:skip");
            filter.put("skip", skip);
            buffer = buffer2;
        }

        if (limit > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where rownum<=:limit");
            filter.put("limit", limit);
            buffer = buffer2;
        }
        logger.info(buffer.toString());
        return jdbcTemplate.queryForList(buffer.toString(), filter);
    }

    public List<Map<String, Object>> queryForListWithFilter(String sql, Map filterParam, OrderedMap sort, int limit, int skip) {
        return this.queryForListWithFilter(sql,filterParam,sort,limit,skip,false);
    }

    public List<Map<String, Object>> queryForListWithFilter(String sql, Map filterParam, OrderedMap sort, int limit, int skip,boolean inner) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        StringBuffer buffer = new StringBuffer("select rownum as sn, a.* from (");
        buffer.append(sql);
        if(inner){
            if (!filter.isEmpty()) {
                buffer.append(" where ");
                buffer.append(prepareCondition(filter));
            }

        }
        buffer.append(") a ");
        if(!inner){
            if (!filter.isEmpty()) {
                buffer.append(" where ");
                buffer.append(prepareCondition(filter));
            }
        }

        if (sort != null && !sort.isEmpty()) {
            buffer.append(" order by ");
            int i = 0;
            for (String key : (Set<String>) sort.keySet()) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(key);
                String sortDir = (String) sort.get(key);
                if ("-1".equals(sortDir) || "desc".equalsIgnoreCase(sortDir)) {
                    buffer.append(" desc");
                }
                i++;
            }

        }
        if (skip > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where sn>:skip");
            filter.put("skip", skip);
            buffer = buffer2;
        }

        if (limit > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where rownum<=:limit");
            filter.put("limit", limit);
            buffer = buffer2;
        }
        logger.info(buffer.toString());
        return jdbcTemplate.queryForList(buffer.toString(), filter);
    }

    public void delete(String collection, Map filter) {
        if (!generator.checkTable(collection)) {
            return;
        }
        StringBuffer buffer = new StringBuffer("delete from ");
        buffer.append(collection);
        buffer.append(" ");
        if (!filter.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(filter));
        }

        logger.info(buffer.toString());
        jdbcTemplate.update(buffer.toString(), filter);

    }

    public void update(String collection, Map condition, Map newvalue) {
        ensureTable(collection);
        ensureColumns(collection, newvalue);
        StringBuffer buffer = new StringBuffer("update ");
        buffer.append(collection);
        buffer.append(" set ");
        int i = 0;
        for (String key : (Set<String>) newvalue.keySet()) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append("\"").append(StringUtils.upperCase(key)).append("\"").append("=:").append(key);
            i++;
        }
        if (!collection.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(condition));
        }
        logger.info(buffer.toString());

        newvalue = convertUpdateParam(newvalue);
        Map param = new HashMap();
        param.putAll(condition);
        param.putAll(newvalue);

        jdbcTemplate.update(buffer.toString(), param);

    }

    public void updateData(Map condition, Map newvalue) {

        String table = (String) newvalue.get("_data");
        Map temp = new HashMap<>();
        temp.putAll(newvalue);
        temp.remove("_data");
        update(table, condition, temp);

    }

    public int queryForCount(String sql, Map filter) {
        StringBuffer buffer = new StringBuffer("select count(1) cnt from (");
        buffer.append(sql);
        buffer.append(") a ");
        if (!filter.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(filter));
        }
        logger.info(buffer.toString());
        Map result = jdbcTemplate.queryForMap(buffer.toString(), filter);
        Object o = result.get("CNT");
        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).intValue();
        }
        if (o instanceof Long) {
            return ((Long) o).intValue();
        }
        if (o instanceof Integer) {
            return (int) o;
        }
        throw new RuntimeException("Invalid return value");
    }

    public SqlRowSet queryForRowSet(String table, Map filterParam, OrderedMap sort, int limit, int skip) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        if (!generator.checkTable(table))
            return null;
        StringBuffer buffer = new StringBuffer("select rownum as sn,a.* from ");
        buffer.append(table);
        buffer.append(" a ");
        if (!filter.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(filter));
        }

        if (sort != null && !sort.isEmpty()) {
            buffer.append(" order by ");
            int i = 0;
            for (String key : (Set<String>) sort.keySet()) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append("\"").append(StringUtils.upperCase(key)).append("\"");
                String sortDir = (String) sort.get(key);
                if ("-1".equals(sortDir) || "desc".equalsIgnoreCase(sortDir)) {
                    buffer.append(" desc");
                }
                i++;
            }

        }
        if (skip > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where sn>:skip");
            filter.put("skip", skip);
            buffer = buffer2;
        }

        if (limit > 0) {
            StringBuffer buffer2 = new StringBuffer("select * from (");
            buffer2.append(buffer);
            buffer2.append(") where rownum<=:limit");
            filter.put("limit", limit);
            buffer = buffer2;
        }

        logger.info(buffer.toString());
        return jdbcTemplate.queryForRowSet(buffer.toString(), filter);
    }

    public TableGenerator getGenerator() {
        return generator;
    }
}
