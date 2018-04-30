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

package com.ifreelight.dyna.provider.processor.sys;

import com.ifreelight.dyna.core.ViewContext;
import com.ifreelight.dyna.provider.processor.DefaultFormProcessor;
import com.ifreelight.dyna.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * User: yuebo
 * Date: 12/2/14
 * Time: 12:47 PM
 */
@Component
public class LoginProcessor extends DefaultFormProcessor {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    Md5Utils md5Utils;

    @Override
    public String process(ViewContext viewContext, Map saveEntity) {

        String userName = request.getParameter("name");
        String userPassword = request.getParameter("password");
        Map<String, Object> findMap = new HashMap<String, Object>();
        findMap.put("name", userName);
        findMap.put("_data", TBL_USER);
        findMap.put("password", md5Utils.md5(userPassword));


        findMap = jdbcService.findData(findMap);
        if (findMap == null || findMap.isEmpty()) {
            viewContext.setFieldValue("name",userName);
            messageUtils.addErrorMessage(viewContext.getMessagesContext(), "form", "fail", viewContext);
            return "show";
        }
        request.getSession().setAttribute("user", findMap);

        return "redirect:loginSuccessView";
    }

    @Override
    public String preCheck(ViewContext context) {
        String userId=getUserId(request);
        return userId==null?null:"redirect:loginSuccessView";
    }
}


