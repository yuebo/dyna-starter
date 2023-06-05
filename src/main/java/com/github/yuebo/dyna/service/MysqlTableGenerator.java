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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;

/**
 * Created by yuebo on 27/11/2017.
 */
public class MysqlTableGenerator extends TableGenerator {
    protected NamedParameterJdbcTemplate jdbcTemplate;
    private String schema;

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSchema(){
        if(this.schema==null){
            Map schema=jdbcTemplate.queryForMap("SELECT DATABASE() AS DB", Collections.EMPTY_MAP);
            this.schema=(String)schema.getOrDefault("DB","dyna");
        }
        return this.schema;
    }

    @Cacheable(value = "meta", key = "#table")
    public boolean checkTable(String table) {
        if (jdbcTemplate == null) {
            return false;
        }
        String sql = "select * from information_schema.tables where TABLE_NAME=:table and TABLE_SCHEMA=:schema";
        List list = jdbcTemplate.queryForList(sql, new HashMap<String, Object>() {
            {
                put("table", table);
                put("schema",getSchema());
            }
        });

        return list != null && list.size() > 0;
    }

    @Cacheable(value = "meta", key = "#table+'.columns'")
    public List<String> checkColumns(String table) {
        String sql = "select * from information_schema.columns where TABLE_NAME=:table and TABLE_SCHEMA =:schema order by COLUMN_NAME";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, new HashMap<String, Object>() {{
            put("table", table);
            put("schema", getSchema());
        }});

        List<String> dbColumns = new ArrayList<>();
        for (Map<String, Object> map : result) {
            dbColumns.add((String) map.get("COLUMN_NAME"));
        }
        return dbColumns;
    }

    @Cacheable(value = "meta", key = "#table+'.columns.length'")
    public Map<String,Integer> checkColumnsLength(String table) {
        String sql = "select * from information_schema.columns where TABLE_NAME=:table and TABLE_SCHEMA =:schema order by COLUMN_NAME";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, new HashMap<String, Object>() {{
            put("table", table);
            put("schema", getSchema());
        }});

        Map<String,Integer> columns=new LinkedCaseInsensitiveMap();
        for (Map<String, Object> map : result) {
            columns.put(MapUtils.getString(map,"COLUMN_NAME"),MapUtils.getInteger(map,"CHARACTER_MAXIMUM_LENGTH"));
        }
        return columns;
    }



    public String getColumnName(String orignal) {
        return "`" + StringUtils.upperCase(orignal) + "`";
    }
}
