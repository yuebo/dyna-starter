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

package com.github.yuebo.dyna.process;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.service.JDBCService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
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
public class DefaultUserEntityManager extends UserEntityManager implements AppConstants {
    @Autowired
    protected JDBCService jdbcService;

    public void setJdbcService(JDBCService jdbcService) {
        this.jdbcService = jdbcService;
    }

    @Override
    public User createNewUser(String userId) {
        return new UserEntity(userId);
    }

    @Override
    public void insertUser(User user) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public void updateUser(User updatedUser) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public User findUserById(String userId) {
        Map map = new HashMap();
        map.put("name", userId);
        Map result = jdbcService.find(TBL_USER, map);
        return convertToUserEntity(result);
    }

    private UserEntity convertToUserEntity(Map result) {
        if (result == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId((String) result.get("name"));
        userEntity.setFirstName((String) result.get("realName"));
        userEntity.setEmail((String) result.get("email"));
        return userEntity;
    }

    @Override
    public void deleteUser(String userId) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl query) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<User> findPotentialStarterUsers(String proceDefId) {
        throw new RuntimeException("Invalid operation");
    }

    @Override
    public List<Group> findGroupsByUser(String userId) {
        Map param = new HashMap();
        param.put("USERNAME", userId);
        List<Group> result = new ArrayList();
        List<Map<String, Object>> groupList = jdbcService.queryForListWithFilter("select r.*,b.USERNAME from TBL_USER_ROLE a join TBL_USER b on a.USERID=b.id join TBL_ROLE r on r.id = a.ROLEID", param, null, 0, 0);
        for (Map<String, Object> role : groupList) {
            GroupEntity groupEntity = new GroupEntity();
            groupEntity.setId((String) role.get("name"));
            groupEntity.setType("group");
            result.add(groupEntity);
        }
        return result;
    }
}
