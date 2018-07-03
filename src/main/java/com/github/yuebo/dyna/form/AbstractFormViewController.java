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

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.utils.FormViewUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * User: yuebo
 * Date: 2018/7/3
 * Time: 21:26
 */
@RequestMapping("/spring/data")
public class AbstractFormViewController implements AppConstants {
    @Autowired
    protected FormViewUtils formViewUtils;
    @Autowired
    HttpServletRequest request;
    protected ViewContext init(String view, Model model){
        ViewContext viewContext=new ViewContext(formViewUtils.getFormView(view));
        model.addAttribute(MODEL_ATTRIBUTE_VIEW, viewContext.getViewMap());
        model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        viewContext.setId(request.getParameter(VIEW_FIELD__ID));
        return viewContext;
    }
}
