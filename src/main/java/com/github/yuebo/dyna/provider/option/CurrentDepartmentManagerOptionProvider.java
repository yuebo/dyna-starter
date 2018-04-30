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
import com.github.yuebo.dyna.core.OptionProvider;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 23/11/2017.
 */
@Component
public class CurrentDepartmentManagerOptionProvider implements OptionProvider,AppConstants {
    @Autowired
    HttpServletRequest request;
    @Autowired
    JDBCService jdbcService;

    @Override
    public List<Map<String, String>> option(ViewContext viewContext, Map item, Map parameter) {
        List<Map<String, String>> options = new ArrayList();
        if (request.getSession().getAttribute("user") != null) {
            Map<String, Object> user = (Map) request.getSession().getAttribute("user");
            Map<String, String> option = new HashMap();
            String id = (String) user.get("_id");
            Map result = jdbcService.find("tbl_user_department", new BasicDBObject("userId", id));
            if (result != null) {
                Map department = jdbcService.find("tbl_department", new BasicDBObject("_id", result.get("departmentId")));
                if (department != null) {
                    Map manager = jdbcService.find(TBL_USER, new BasicDBObject("_id", department.get("managerId")));
                    option.put((String) manager.get("name"), (String) manager.get("name"));
                    options.add(option);
                }

            }
        }
        return options;
    }
}
