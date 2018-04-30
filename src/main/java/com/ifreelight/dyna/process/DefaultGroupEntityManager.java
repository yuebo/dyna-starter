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

package com.ifreelight.dyna.process;

import com.ifreelight.dyna.AppConstants;
import com.ifreelight.dyna.service.JDBCService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 20/11/2017.
 */
@Component
public class DefaultGroupEntityManager extends GroupEntityManager implements AppConstants {
    @Autowired
    protected JDBCService jdbcService;

    public void setJdbcService(JDBCService jdbcService) {
        this.jdbcService = jdbcService;
    }

    @Override
    public Group createNewGroup(String groupId) {
        return new GroupEntity(groupId);
    }

    @Override
    public void insertGroup(Group group) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public void deleteGroup(String groupId) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public void updateGroup(Group updatedGroup) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<Group> findGroupsByUser(String userId) {
        Map param = new HashMap();
        param.put("USERNAME", userId);
        List<Group> result = new ArrayList();
        List<Map<String, Object>> groupList = jdbcService.queryForListWithFilter("select r.*,b.name AS USERNAME from "+TBL_USER_ROLE+" a join "+TBL_USER+" b on a.USERID=b._ID join "+TBL_ROLE+" r on r._ID = a.ROLEID", param, null, 0, 0);
        for (Map<String, Object> role : groupList) {
            GroupEntity groupEntity = new GroupEntity();
            groupEntity.setId((String) role.get("rolename"));
            groupEntity.setType("assignment");
            result.add(groupEntity);
        }
        return result;
    }
}
