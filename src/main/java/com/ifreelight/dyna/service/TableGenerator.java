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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 27/11/2017.
 */
@Component
public class TableGenerator {
    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    public String getColumnName(String orignal) {
        return "\"" + StringUtils.upperCase(orignal) + "\"";
    }

    @Cacheable(value = "meta", key = "#table+'.columns'")
    public List<String> checkColumns(String table) {
        String sql = "select * from user_tab_columns where TABLE_NAME=:table order by column_id";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, new HashMap<String, Object>() {{
            put("table", StringUtils.upperCase(table));
        }});

        List<String> dbColumns = new ArrayList<>();
        for (Map<String, Object> map : result) {
            dbColumns.add((String) map.get("column_name"));
        }
        return dbColumns;
    }

    @Cacheable(value = "meta", key = "#table")
    public boolean checkTable(String table) {
        if (jdbcTemplate == null) {
            return false;
        }
        String sql = "select * from USER_TABLES where TABLE_NAME=:table";
        List list = jdbcTemplate.queryForList(sql, new HashMap<String, Object>() {
            {
                put("table", StringUtils.upperCase(table));
            }
        });

        return list != null && list.size() > 0;
    }

    @CacheEvict(value = "meta", allEntries = true)
    public void removeMetaCache() {

    }
    @Cacheable(value = "meta", key = "#table+'.columns.length'")
    public Map<String,Integer> checkColumnsLength(String table) {
        return MapUtils.EMPTY_MAP;
    }
}
