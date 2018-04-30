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

package com.ifreelight.dyna.provider.menu;

import com.ifreelight.dyna.core.MenuProvider;
import com.ifreelight.dyna.core.PermissionProvider;
import com.ifreelight.dyna.service.JDBCService;
import com.ifreelight.dyna.utils.FormViewUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 11/27/14
 * Time: 1:12 PM
 */
@Component
public class DefaultMenuProvider implements MenuProvider {
    @Autowired
    private JDBCService jdbcService;
    @Autowired
    private PermissionProvider permissionProvider;
    @Autowired
    private FormViewUtils formViewUtils;

    @Override
    public Map get(String name) {

        Map result = formViewUtils.getMenu(name);
        for (Map item : (List<Map>) result.get("items")) {

            List<Map> submenu = (List) item.get("submenu");
            if (submenu != null) {
                boolean needShowParent = false;
                for (Map sub : submenu) {
                    String view = (String) sub.get("view");
                    Map re = formViewUtils.getFormView(view);
                    if (re != null) {
                        if (permissionProvider.hasPermission((List) re.get("permission"))) {
                            sub.put("show", "true");
                            needShowParent = true;
                        } else {
                            sub.put("show", "false");
                        }
                    } else {
                        sub.put("show", "false");
                    }
                }
                if (needShowParent) {
                    item.put("show", "true");
                } else {
                    item.put("show", "false");
                }


            } else {
                String view = (String) item.get("view");
                Map re = formViewUtils.getFormView(view);
                if (re != null) {
                    if (permissionProvider.hasPermission((List) re.get("permission"))) {
                        item.put("show", "true");
                    } else {
                        item.put("show", "false");
                    }
                } else {
                    item.put("show", "false");
                }
            }


        }
        return result;
    }

}
