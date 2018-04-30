package com.ifreelight.dyna.security;

import com.ifreelight.dyna.core.PermissionProvider;
import com.ifreelight.dyna.service.JDBCService;
import com.ifreelight.dyna.utils.FormViewUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/30/14
 * Time: 10:05 AM
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {
    protected PermissionProvider permissionProvider;
    protected JDBCService jdbcService;
    protected FormViewUtils formViewUtils;

    public PermissionProvider getPermissionProvider() {
        return permissionProvider;
    }

    public void setPermissionProvider(PermissionProvider permissionProvider) {
        this.permissionProvider = permissionProvider;
    }

    public JDBCService getJdbcService() {
        return jdbcService;
    }

    public void setJdbcService(JDBCService jdbcService) {
        this.jdbcService = jdbcService;
    }

    public FormViewUtils getFormViewUtils() {
        return formViewUtils;
    }

    public void setFormViewUtils(FormViewUtils formViewUtils) {
        this.formViewUtils = formViewUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            PermissionCheck check = getPermissionCheck(handlerMethod);
            Map pathVar = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (check != null && pathVar != null) {
                String viewname = StringUtils.isEmpty(check.name()) ? (String) pathVar.get("viewname") : check.name();

                if (StringUtils.isEmpty(viewname)) {
                    return true;
                }
                Map map = formViewUtils.getFormView(viewname);
                if (map == null) {
                    response.sendError(404);
                    return false;
                }
                if (!permissionProvider.hasPermission((List) map.get("permission"))) {
                    response.sendError(401);
                    return false;
                }

            }
        }
        return true;
    }


    protected PermissionCheck getPermissionCheck(HandlerMethod handlerMethod) throws NoSuchMethodException {
        PermissionCheck classCheck = null;
        PermissionCheck check = null;
        if (handlerMethod.getBean().getClass().getName().contains("$$EnhancerBySpringCGLIB$$")) {
            classCheck = handlerMethod.getBean().getClass().getSuperclass().getAnnotation(PermissionCheck.class);
            check = handlerMethod.getBean().getClass().getSuperclass().getMethod(handlerMethod.getMethod().getName(), handlerMethod.getMethod().getParameterTypes()).getAnnotation(PermissionCheck.class);
        } else {
            classCheck = handlerMethod.getBean().getClass().getAnnotation(PermissionCheck.class);
            check = handlerMethod.getMethodAnnotation(PermissionCheck.class);
        }
        if (check == null) {
            check = classCheck;
        }
        return classCheck;

    }
}
