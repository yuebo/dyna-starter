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

package com.ifreelight.dyna.service;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.*;

/**
 * Created by yuebo on 9/11/2017.
 */

public class MysqlJDBCService extends JDBCService {
    private static Logger logger = LoggerFactory.getLogger(MysqlJDBCService.class);
    protected String schema;
    protected Boolean autoUpdate=true;

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public MysqlJDBCService() {

    }

    protected void ensureTable(String collection) {
        if(autoUpdate){
            if (!generator.checkTable(collection)) {
                try {
                    StringBuffer buffer = new StringBuffer("CREATE TABLE ");
                    buffer.append(StringUtils.upperCase(collection));
                    buffer.append(" ( `_ID` VARCHAR (50) primary key)");
                    logger.info(buffer.toString());
                    jdbcTemplate.getJdbcOperations().execute(buffer.toString());
                } finally {
                    generator.removeMetaCache();
                }
            }
        }
    }

    public int ensureLength(String collection,String field,int length){
        if(autoUpdate){
            ensureTable(collection);
            Map<String,Integer> fields=generator.checkColumnsLength(collection);
            try {
                if(fields.containsKey(field)){
                    if(fields.get(field)<length){
                        jdbcTemplate.getJdbcOperations().execute("alter table "+StringUtils.upperCase(collection)+" modify column "+getColumnName(field)+" varchar("+length+")");
                    }
                }else {
                    jdbcTemplate.getJdbcOperations().execute("alter table "+StringUtils.upperCase(collection)+" add column "+getColumnName(field)+" varchar("+length+")");
                }
            }finally {
                generator.removeMetaCache();
            }
        }
        return length;
    }
    protected void ensureColumns(String collection, Map o) {
        if(autoUpdate){
            for (String key : (Set<String>) o.keySet()) {
                if (!generator.checkColumns(collection).contains(StringUtils.upperCase(key))) {
                    try {
                        StringBuffer buffer = new StringBuffer("ALTER TABLE ");
                        buffer.append(StringUtils.upperCase(collection));
                        buffer.append(" ADD ");
                        buffer.append(getColumnName(StringUtils.upperCase(key)));
                        buffer.append(" ");
                        if (o.get(key) instanceof Date) {
                            buffer.append("DATETIME");
                        } else if (o.get(key) instanceof Number) {
                            buffer.append("DECIMAL(12,2)");
                        } else {
                            buffer.append("VARCHAR(500)");
                        }
                        logger.info(buffer.toString());
                        jdbcTemplate.getJdbcOperations().execute(buffer.toString());
                    } finally {
                        generator.removeMetaCache();
                    }
                }
            }
        }

    }

    public List<Map<String, Object>> queryForList(String sql, Map filterParam, OrderedMap sort, int limit, int skip) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        StringBuffer buffer = new StringBuffer("select a.* from (");
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
        if (limit > 0) {
            buffer.append(" limit :limit");
            filter.put("limit", limit);
        }
        if (skip > 0) {
            buffer.append(" offset :skip");
            filter.put("skip", skip);
        }


        return jdbcTemplate.queryForList(buffer.toString(), filter);
    }

    public List<Map<String, Object>> queryForListWithFilter(String sql, Map filterParam, OrderedMap sort, int limit, int skip) {
        return this.queryForListWithFilter(sql,filterParam,sort,limit,skip,false);
    }

    public List<Map<String, Object>> queryForListWithFilter(String sql, Map filterParam, OrderedMap sort, int limit, int skip,boolean inner) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        StringBuffer buffer = new StringBuffer("select a.* from (");
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
        if (limit > 0) {
            buffer.append(" limit :limit");
            filter.put("limit", limit);
        }
        if (skip > 0) {
            buffer.append(" offset :skip");
            filter.put("skip", skip);
        }

        logger.info(buffer.toString());
        return jdbcTemplate.queryForList(buffer.toString(), filter);
    }


    public List<Map<String, Object>> findList(String table, Map filterParam, OrderedMap sort, int limit, int skip) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        if (!generator.checkTable(table))
            return null;
        StringBuffer buffer = new StringBuffer("select a.* from ");
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
                buffer.append(getColumnName(StringUtils.upperCase(key)));
                String sortDir = (String) sort.get(key);
                if ("-1".equals(sortDir) || "desc".equalsIgnoreCase(sortDir)) {
                    buffer.append(" desc");
                }
                i++;
            }

        }
        if (limit > 0) {
            buffer.append(" limit :limit");
            filter.put("limit", limit);
        }
        if (skip > 0) {
            buffer.append(" offset :skip");
            filter.put("skip", skip);
        }
        logger.info(buffer.toString());
        return jdbcTemplate.queryForList(buffer.toString(), filter);
    }

    public void save(String collection, Map o) {
        ensureTable(collection);
        ensureColumns(collection, o);
        if (o.get("_id") == null) {
            o.put("_id", new ObjectId().toHexString());
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

            buffer.append(getColumnName(StringUtils.upperCase(key)));
            values.append(":");
            values.append(key);

            i++;
        }
        values.append(")");
        buffer.append(values);
        Map param = convertUpdateParam(o);
        jdbcTemplate.update(buffer.toString(), param);


    }

    protected void sqlAppend(StringBuffer buffer, String key, Map param) {
        if (param.get(key) instanceof Map) {
            buffer.append("(");
            Map<String, Object> nestMap = (Map) param.get(key);
            int index = 0;
            for (String nestKey : nestMap.keySet()) {
                buffer.append(getColumnName(StringUtils.upperCase(key)));
                String tempKey = key + index;
                if (nestKey.equals($eq)) {
                    buffer.append("=:").append(tempKey);
                    param.put(tempKey, nestMap.get($eq));
                } else if (nestKey.equals($lt)) {
                    buffer.append("<:").append(tempKey);
                    param.put(tempKey, nestMap.get($lt));
                } else if (nestKey.equals($gt)) {
                    buffer.append(">:").append(tempKey);
                    param.put(tempKey, nestMap.get($gt));
                } else if (nestKey.equals($lte)) {
                    buffer.append("<=:").append(tempKey);
                    param.put(tempKey, nestMap.get($lte));
                } else if (nestKey.equals($gte)) {
                    buffer.append(">=:").append(tempKey);
                    param.put(tempKey, nestMap.get($gte));
                } else if (nestKey.equals($ne)) {
                    buffer.append("<>:").append(tempKey);
                    param.put(tempKey, nestMap.get($ne));
                } else if (nestKey.equals($in)) {
                    buffer.append("in (:").append(tempKey).append(")");
                    param.put(tempKey, nestMap.get($in));
                } else if (nestKey.equals($like)) {
                    buffer.append("like CONCAT('%',:" + tempKey + ",'%') escape '/'");
                    param.put(tempKey, escapeLike((String) nestMap.get($like)));
                } else if (nestKey.equals($null)) {
                    Object test= nestMap.get($null);
                    if(test==null){
                        buffer.append(" is null");
                    }else {
                        buffer.append(" is not null");
                    }
                }else {
                    throw new RuntimeException("Invalid Operation found");
                }
                index++;
                if (param.containsKey(tempKey) && index < nestMap.keySet().size()) {
                    buffer.append(" and ");
                }
            }
            buffer.append(")");


        } else {
            buffer.append(getColumnName(StringUtils.upperCase(key)));
            buffer.append("=:").append(key);
            param.put(key, param.get(key));
        }

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
            buffer.append(getColumnName(StringUtils.upperCase(key))).append("=:").append(key);
            i++;
        }
        if (!collection.isEmpty()) {
            buffer.append(" where ");
            buffer.append(prepareCondition(condition));
        }

        newvalue = convertUpdateParam(newvalue);
        Map param = new HashMap();
        param.putAll(condition);
        param.putAll(newvalue);
        logger.info(buffer.toString());
        jdbcTemplate.update(buffer.toString(), param);

    }

    protected String escapeLike(String $like) {
        return StringUtils.replaceEach($like, new String[]{
                "%", "_",
        }, new String[]{"/%", "/_"});
    }

    public SqlRowSet queryForRowSet(String table, Map filterParam, OrderedMap sort, int limit, int skip) {
        Map filter = new HashMap();
        filter.putAll(filterParam);
        if (!generator.checkTable(table))
            return null;
        StringBuffer buffer = new StringBuffer("select a.* from ");
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
                buffer.append(getColumnName(StringUtils.upperCase(key)));
                String sortDir = (String) sort.get(key);
                if ("-1".equals(sortDir) || "desc".equalsIgnoreCase(sortDir)) {
                    buffer.append(" desc");
                }
                i++;
            }

        }
        if (limit > 0) {
            buffer.append(" limit :limit");
            filter.put("limit", limit);
        }
        if (skip > 0) {
            buffer.append(" offset :skip");
            filter.put("skip", skip);
        }
        logger.info(buffer.toString());
        return jdbcTemplate.queryForRowSet(buffer.toString(), filter);
    }

    public String getColumnName(String orignal) {
        return "`" + StringUtils.upperCase(orignal) + "`";
    }
}
