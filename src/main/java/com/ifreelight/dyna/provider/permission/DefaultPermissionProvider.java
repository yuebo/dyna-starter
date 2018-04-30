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

package com.ifreelight.dyna.provider.permission;

import com.ifreelight.dyna.AppConstants;
import com.ifreelight.dyna.core.PermissionProvider;
import com.ifreelight.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/3/14
 * Time: 2:46 PM
 */
@Component
public class DefaultPermissionProvider implements PermissionProvider,AppConstants {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JDBCService jdbcService;

    @Override
    public boolean hasPermission(List<String> permission) {
        Map user = (Map) request.getSession().getAttribute("user");
        if (user == null) {
            return (permission == null);
        } else {
            List<Map<String, Object>> list = new ArrayList();

            if (permission == null || permission.size() == 0) {
                return true;
            }

            if (request.getAttribute("permissions") == null) {
                //using native sql to check the permission
                list = jdbcService.queryForList("select b.name from "+TBL_USER_PERMISSION+" a join "+TBL_PERMISSION+" b on b._ID=a.PERMISSION  where  a.USERID=:userid" +
                        " union select b.name from "+TBL_USER+" u join "+TBL_USER_ROLE+" r on u._id=r.USERID join "+TBL_ROLE_PERMISSION+" rp on r.ROLEID=rp.ROLEID join "+TBL_PERMISSION+" b on rp.PERMISSION=b._ID where  r.USERID=:userid",
                        new BasicDBObject("userid", user.get("_ID")), new ListOrderedMap(), 0, 0);
                List<String> dbPermissions = new ArrayList();
                for (Map<String, Object> p : list) {
                    dbPermissions.add((String) p.get("name"));
                }
                request.setAttribute("permissions", dbPermissions);
            }
            List<String> dbPermissions = (List) request.getAttribute("permissions");
            return dbPermissions != null && CollectionUtils.containsAny(dbPermissions, permission);
        }
    }
}
