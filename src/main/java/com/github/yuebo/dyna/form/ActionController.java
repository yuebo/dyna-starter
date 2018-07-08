/*
 *
 *  *
 *  *  * Copyright 2002-2017 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 *
 */

package com.github.yuebo.dyna.form;

import com.github.yuebo.dyna.utils.FormViewUtils;
import com.github.yuebo.dyna.core.ViewContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuebo on 2017/12/13.
 */
@Controller("/spring")
public class ActionController {
    @Autowired
    FormViewUtils formViewUtils;
    @RequestMapping(method = RequestMethod.POST,value = "/redirect")
    public String redirectToCreateView(@RequestParam("view") String view,@RequestParam("id")String [] ids, HttpServletRequest request){
        ViewContext viewContext=new ViewContext(formViewUtils.getFormView(view));
        StringBuffer buffer=new StringBuffer();
        for(String id:ids){
            buffer.append("&id=").append(id);
        }
        return "redirect:/spring/data/"+viewContext.getType()+"/"+viewContext.getName()+"?"+buffer.substring(1).toString();
    }
}
