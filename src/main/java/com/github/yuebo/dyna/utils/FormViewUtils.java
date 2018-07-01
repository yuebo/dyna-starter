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

package com.github.yuebo.dyna.utils;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.OrderedResourse;
import com.github.yuebo.dyna.core.PermissionProvider;
import com.github.yuebo.dyna.core.UIComponent;
import com.github.yuebo.dyna.core.ViewContext;
import com.mongodb.util.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by yuebo on 5/18/2015.
 */
@Component

public class FormViewUtils implements AppConstants {
    @Autowired
    HttpServletRequest request;
    @Autowired
    PermissionProvider permissionProvider;
    @Autowired
    JsonUtils jsonUtils;
    @Autowired
    SpringUtils springUtils;

    public Map<String, Object> getFormView(String viewname) {
        Map view = getViewFromDB(viewname);
//        if(view==null){
//            view = getJsonFromClassPath(viewname, "view");
//        }
        //check permission on the data view field
        if (view != null) {
            permissionCheck(view, VIEW_FIELD_FIELDS);
            permissionCheck(view, VIEW_FIELD_RESULT);
            permissionCheck(view, VIEW_FIELD_ACTIONS);
            permissionCheck(view, VIEW_FIELD_OPERATE);
            permissionCheck(view, VIEW_FIELD_CHARTS);

            fileTypeCheck(view);

            if (view.get(VIEW_FIELD_INPUT_PARAMETER) != null && request != null) {
                List<String> inputParameter = (List) view.get(VIEW_FIELD_INPUT_PARAMETER);
                Map<String, Object> inputMap = new HashMap();
                if (inputParameter != null) {
                    for (String name : inputParameter) {
                        inputMap.put(name, request.getParameter(name));
                    }
                    view.put(VIEW_FIELD_INPUT, inputMap);
                }
            }
        }

        return view;
    }

    private Map getViewFromDB(String viewname) {
        String json=jsonUtils.getJsonStringFromDB(viewname);
        if (json != null) {
            return (Map) JSON.parse(json);
        }
        return null;
    }

    private void fileTypeCheck(Map view) {
        List<Map<String, Object>> fields = (List) view.get(VIEW_FIELD_FIELDS);
        if (fields != null) {
            for (Map<String, Object> field : fields) {
                String type = MapUtils.getString(field, VIEW_FIELD_TYPE);
                UIComponent uiComponent=getComponentByType(type);
                if (uiComponent.isBinaryType()) {
                    view.put(VIEW_FIELD_ENCTYPE, VIEW_FIELD_ENCTYPE_MULTIPART);
                }
            }
        }
    }

    private void permissionCheck(Map<String, Object> view, String attr) {
        List<Map<String, Object>> results = (List) view.get(attr);
        if (results != null) {
            List<Map<String, Object>> removeFields = (List) new ArrayList();
            for (Map<String, Object> field : results) {
                List<String> permissions = (List) field.get("permission");
                if (permissions != null) {
                    boolean hasPermission = permissionProvider.hasPermission(permissions);
                    if (!hasPermission) {
                        removeFields.add(field);
                    }
                }
            }
            results.removeAll(removeFields);
        }
    }

    public Map<String, Object> getMenu(String menu) {
        ViewContext context=new ViewContext(getViewFromDB(menu));
        if(context.getType().equals(AppConstants.VIEW_TYPE_MENU)){
            return context.getViewMap();
        }
        return null;
    }


    public Map<String, Object> getField(ViewContext viewContext, String name) {
        List<Map<String, Object>> fields = viewContext.getFields();
        if (fields != null) {
            for (Map<String, Object> field : fields) {
                if (name.equals(field.get(VIEW_FIELD_FIELDS_NAME))) {
                    return field;
                }
            }
        }

        return null;
    }

    public Object getValue(ViewContext viewContext, String name) {
        Map<String, Object> field = getField(viewContext, name);
        if (field == null) {
            return null;
        }
        return ConvertUtils.getProperty(field, "attributes.value");
    }

    public Object value(Map<String, Object> viewMap, String name) {
        //access the field value and inputParameter
        ViewContext viewContext = new ViewContext(viewMap);
        Object o = getValue(viewContext, name);
        if (o == null) {
            Map<String, Object> test = (Map) viewMap.get(VIEW_FIELD_INPUT);
            if (test != null) {
                return test.get(name) == null ? "" : test.get(name);
            }
        }
        return "";
    }

    public void prepareParameter(Map<String, Object> viewMap, Map<String, Object> subViewMap, Map<String, String> parameter) {
        //for subview to get pass the parameter from parent
        ViewContext viewContext = new ViewContext(viewMap);
        if (parameter != null) {
            Map<String, Object> objectMap = new HashMap();
            for (String key : parameter.keySet()) {
                String from = parameter.get(key);
                if (from.equals(DB_FIELD__ID)) {
                    objectMap.put(key, viewMap.get(DB_FIELD__ID));
                } else {
                    objectMap.put(key, getValue(viewContext, from));
                }

            }
            subViewMap.put(VIEW_FIELD_INPUT, objectMap);

        }
    }
//
//    public Map<String, Object> getJsonFromClassPath(String name, String sub) {
//        String json = jsonUtils.getJsonStringFromClassPath(name, sub);
//        if (json != null) {
//            return (Map) JSON.parse(json);
//        }
//        return null;
//    }

    public Map map() {
        return new HashMap();
    }

    public Map<String, Object> getFormUpload(String viewname) {
        ViewContext viewContext=new ViewContext(getViewFromDB(viewname));

        ViewContext context=new ViewContext(getViewFromDB(viewname));
        if(context.getType().equals(AppConstants.VIEW_TYPE_UPLOAD)){
            return context.getViewMap();
        }
        return null;
    }
    @Cacheable(value = "form")
    public List<UIComponent> getComponents(){
        return springUtils.getComponents();
    }

    @Cacheable(value = "form")
    public UIComponent getComponentByType(String type){
        List<UIComponent> list=getComponents();
        for (UIComponent uiComponent:list){
            if(StringUtils.equals(uiComponent.getComponentName(),type)){
                return uiComponent;
            }
        }
        return new UIComponent() {
            @Override
            public String getComponentName() {
                return "default";
            }
            @Override
            public boolean isNoLabel() {
                return true;
            }

            @Override
            public boolean isValidationRequired() {
                return false;
            }

            @Override
            public String getTemplateName() {
                return "/templates/empty.vm";
            }
        };
    }

    public List<OrderedResourse> getExtentalJs(ViewContext viewContext){
        List<String> list=viewContext.getComponentList();
        List<OrderedResourse> resources=new ArrayList();
        if (!list.isEmpty()){
            for(String type:list){
                UIComponent uiComponent=getComponentByType(type);
                if(uiComponent.getRequiredJS()!=null){
                    resources.addAll(uiComponent.getRequiredJS());
                }

            }


        }
        Collections.sort(resources);
        return resources;
    }
    public List<OrderedResourse> getExtentalCss(ViewContext viewContext){
        List<String> list=viewContext.getComponentList();
        List<OrderedResourse> resources=new ArrayList();
        if (!list.isEmpty()){
            for(String type:list){
                UIComponent uiComponent=getComponentByType(type);
                if(uiComponent.getRequiredCss()!=null){
                    resources.addAll(uiComponent.getRequiredCss());
                }

            }


        }
        Collections.sort(resources);
        return resources;
    }
}
