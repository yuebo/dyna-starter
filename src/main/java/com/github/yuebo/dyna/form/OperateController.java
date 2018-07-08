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

package com.github.yuebo.dyna.form;

import com.github.yuebo.dyna.utils.FormViewUtils;
import com.github.yuebo.dyna.utils.MessageUtils;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.OperateContext;
import com.github.yuebo.dyna.core.OperateProvider;
import com.github.yuebo.dyna.core.OperateResponse;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.security.PermissionCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
@RequestMapping("/spring/data")
@PermissionCheck
@Controller
public class OperateController implements AppConstants {
    @Autowired
    FormViewUtils formViewUtils;
    @Autowired
    MessageUtils messageUtils;
    @Autowired
    SpringUtils springUtils;

    @RequestMapping(value = "operate/{viewname}/{operate}",method = {RequestMethod.GET,RequestMethod.POST})
    @PermissionCheck
    public @ResponseBody OperateResponse operate(HttpServletRequest request, @PathVariable("viewname") String viewname, @PathVariable String operate){
        OperateResponse response=new OperateResponse();
        Map<String,Object> view=formViewUtils.getFormView(viewname);
        ViewContext viewContext=new ViewContext(view);
        viewContext.setId(request.getParameter("id"));

        Map<String,Object> op=viewContext.getOperate(operate);
        if(op==null){
            response.setStatus(404);
            response.setMsg(messageUtils.getMessage(viewContext,"error","operate_invalid"));
        }

        OperateContext operateContext=new OperateContext(op);

        String provider=operateContext.getProvider();


        OperateProvider operateProvider=springUtils.getOperateProvider(provider);

        return operateProvider.operate(viewContext,operateContext);

    }
}
