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

package com.github.yuebo.dyna.security;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.utils.FormViewUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

public class TokenInterceptor extends HandlerInterceptorAdapter implements AppConstants {

    protected FormViewUtils formViewUtils;
    private SessionStoreToken sessionStoreToken;

    public FormViewUtils getFormViewUtils() {
        return formViewUtils;
    }

    public void setFormViewUtils(FormViewUtils formViewUtils) {
        this.formViewUtils = formViewUtils;
    }

    public SessionStoreToken getSessionStoreToken() {
        return sessionStoreToken;
    }

    public void setSessionStoreToken(SessionStoreToken sessionStoreToken) {
        this.sessionStoreToken = sessionStoreToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        Map pathVar = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVar != null && pathVar.containsKey("viewname")) {
            String viewname = (String) pathVar.get("viewname");
            Map map = formViewUtils.getFormView(viewname);
            Boolean tokenValidate = MapUtils.getBoolean(map,VIEW_FIELD_TOKEN,false);
            if (tokenValidate) {
                String token = new BigInteger(165, new SecureRandom()).toString(36).toUpperCase();
                request.setAttribute("_session.token", token);
                sessionStoreToken.addToken(token);
//                request.getSession().setAttribute("_session_token_vector", tokens);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (request.getSession(false) != null) {
            request.getSession().setAttribute("_session.token", request.getAttribute("_session.token"));
        }
    }
}
