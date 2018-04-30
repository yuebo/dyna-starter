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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 30/11/2017.
 */
@CacheConfig(cacheNames = "table")
public class CacheableMysqlJDBCService extends MysqlJDBCService {
    @Override
    @Cacheable(value = "table")
    public List<Map<String, Object>> queryForList(String sql, Map filterParam, OrderedMap sort, int limit, int skip) {
        return super.queryForList(sql, filterParam, sort, limit, skip);
    }

    @Override
    @Cacheable(value = "table")
    public List<Map<String, Object>> queryForListWithFilter(String sql, Map filterParam, OrderedMap sort, int limit, int skip) {
        return super.queryForListWithFilter(sql, filterParam, sort, limit, skip);
    }

    @Override
    @Cacheable(value = "table")
    public List<Map<String, Object>> queryForListWithFilter(String sql, Map filterParam, OrderedMap sort, int limit, int skip,boolean inner) {
        return super.queryForListWithFilter(sql, filterParam, sort, limit, skip,inner);
    }

    @Override
    @Cacheable(value = "table")
    public List<Map<String, Object>> findList(String table, Map filterParam, OrderedMap sort, int limit, int skip) {
        return super.findList(table, filterParam, sort, limit, skip);
    }

    @Override
    @CacheEvict(value = "table", allEntries = true)
    public void save(String collection, Map o) {
        super.save(collection, o);
    }

    @Override
    @CacheEvict(value = "table", allEntries = true)
    public void update(String collection, Map condition, Map newvalue) {
        super.update(collection, condition, newvalue);
    }

    @Override
    public SqlRowSet queryForRowSet(String table, Map filterParam, OrderedMap sort, int limit, int skip) {
        return super.queryForRowSet(table, filterParam, sort, limit, skip);
    }

    @Override
    @Cacheable(value = "table")
    public Map find(String table, Map param) {
        return super.find(table, param);
    }

    @Override
    @Cacheable(value = "table")
    public Map findData(Map param) {
        return super.findData(param);
    }

    @Override
    @CacheEvict(value = "table", allEntries = true)
    public void saveData(Map o) {
        super.saveData(o);
    }

    @Override
    @Cacheable(value = "table")
    public int count(String table, Map filter) {
        return super.count(table, filter);
    }

    @Override
    @CacheEvict(value = "table", allEntries = true)
    public void delete(String collection, Map filter) {
        super.delete(collection, filter);
    }

    @Override
    @CacheEvict(value = "table", allEntries = true)
    public int queryForCount(String sql, Map filter) {
        return super.queryForCount(sql, filter);
    }

    @Override
    @CacheEvict(value = "table", allEntries = true)
    public void updateData(Map condition, Map newvalue) {
        super.updateData(condition, newvalue);
    }
}
