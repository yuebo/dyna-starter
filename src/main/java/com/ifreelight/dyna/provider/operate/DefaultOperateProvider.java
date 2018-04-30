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

package com.ifreelight.dyna.provider.operate;

import com.ifreelight.dyna.AppConstants;
import com.ifreelight.dyna.core.OperateContext;
import com.ifreelight.dyna.core.OperateProvider;
import com.ifreelight.dyna.core.OperateResponse;
import com.ifreelight.dyna.core.ViewContext;
import com.ifreelight.dyna.service.JDBCService;
import com.ifreelight.dyna.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuebo on 2017/12/6.
 */
@Component
public class DefaultOperateProvider implements OperateProvider, AppConstants {
    @Autowired
    protected JDBCService jdbcService;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected MessageUtils messageUtils;

    @Override
    @Transactional
    public OperateResponse operate(ViewContext viewContext, OperateContext operateContext) {
        OperateResponse response=new OperateResponse();

        try {
            String message=messageUtils.getMessage(viewContext,"info","operate_success");
            boolean isRedirect=doOperate(viewContext,operateContext);
            response.setRedirect(isRedirect);
            response.setMsg(message);
        }catch (Exception e){
            String message=messageUtils.getMessage(viewContext,"error","operate_fail");
            response.setStatus(500);
            response.setMsg(message);
        }


        return response;
    }

    protected boolean doOperate(ViewContext viewContext, OperateContext operateContext) {
        return false;
    }
}
