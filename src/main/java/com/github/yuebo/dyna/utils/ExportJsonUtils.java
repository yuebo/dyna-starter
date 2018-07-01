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
import com.github.yuebo.dyna.DbConstant;
import com.github.yuebo.dyna.FormConstants;
import com.github.yuebo.dyna.core.UIComponent;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.bson.BasicBSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/7.
 */
@Component
public class ExportJsonUtils implements AppConstants,FormConstants {
    @Autowired
    JDBCService jdbcService;
    @Autowired
    FormViewUtils formViewUtils;
    public String exportJson(String id){
        Map form= jdbcService.find(TBL_DYNA_FORM, new BasicBSONObject("_id",id));
        if(form==null){
            throw new RuntimeException("form not found");
        }
        BasicDBObject view=new BasicDBObject();
        String type= MapUtils.getString(form,VIEW_FIELD_TYPE);
        view.append(VIEW_FIELD_NAME,form.get(VIEW_FIELD_NAME));
        view.append(VIEW_FIELD_TYPE,form.get(VIEW_FIELD_TYPE));
        view.append(VIEW_FIELD_TITLE,form.get(VIEW_FIELD_TITLE));
        view.append(VIEW_FIELD_DATA,form.get(VIEW_FIELD_DATA));

        if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_INPUT_PARAMETER))){
            view.append(VIEW_FIELD_INPUT_PARAMETER,JSON.parse(MapUtils.getString(form,VIEW_FIELD_INPUT_PARAMETER)));
        }

        if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_PROCESSOR))){
            view.append(VIEW_FIELD_PROCESSOR,form.get(VIEW_FIELD_PROCESSOR));
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_MENU))){
            view.append(VIEW_FIELD_MENU,form.get(VIEW_FIELD_MENU));
        }
        if("true".equals(MapUtils.getString(form,VIEW_FIELD_READONLY))){
            view.append(VIEW_FIELD_READONLY,true);
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_DEFAULT_VALUE))){
            view.append(VIEW_FIELD_DEFAULT_VALUE,JSON.parse(MapUtils.getString(form,VIEW_FIELD_DEFAULT_VALUE)));
        }
        if("true".equals(MapUtils.getString(form,VIEW_FIELD_SELF_ONLY))){
            view.append(VIEW_FIELD_SELF_ONLY,true);
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_EXPORT))){
            view.append(VIEW_FIELD_EXPORT,JSON.parse(MapUtils.getString(form,VIEW_FIELD_EXPORT)));
        }
        if("true".equals(MapUtils.getString(form,"usePermission"))){
            List<String> list=new ArrayList();
            List<Map<String,Object>> result=jdbcService.findList(TBL_DYNA_FORM_PERMISSION,new BasicBSONObject("formId",id),null,0,0);
            processPermission(list,result);
            view.append("permission",list);

            List<String> list2=new ArrayList();
            List<Map<String,Object>> result2=jdbcService.findList(TBL_DYNA_FORM_DATA_PERMISSION,new BasicBSONObject("formId",id),null,0,0);
            processPermission(list,result2);
            if(list2.size()>0){
                view.append(VIEW_FIELD_DATA_PERMISSION,list2);
            }
        }

        if("search".equals(type)){
            if("false".equals(MapUtils.getString(form,VIEW_FIELD_CLEAR))){
                view.append(VIEW_FIELD_CLEAR,false);
            }
            if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_DEFAULT_SORT))){
                view.append(VIEW_FIELD_DEFAULT_SORT,JSON.parse(MapUtils.getString(form,VIEW_FIELD_DEFAULT_SORT)));
            }

        }

        if("create".equals(type)){
            if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_REDIRECT))){
                view.append(VIEW_FIELD_REDIRECT,MapUtils.getString(form,VIEW_FIELD_REDIRECT));

            }
            if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_SEARCHVIEW))){
                view.append(VIEW_FIELD_SEARCHVIEW,MapUtils.getString(form,VIEW_FIELD_SEARCHVIEW));
            }

            if(StringUtils.isNotEmpty(MapUtils.getString(form,VIEW_FIELD_PROCESS))){
                view.append(VIEW_FIELD_PROCESS,JSON.parse(MapUtils.getString(form,VIEW_FIELD_PROCESS)));
            }
        }

        //get the fields
        List<BasicDBObject> fields=new ArrayList();
        OrderedMap sort=new ListOrderedMap();
        sort.put("seq","asc");
        List<Map<String,Object>> fieldList=jdbcService.findList(TBL_DYNA_FORM_FIELD,new BasicBSONObject("formId",id),sort,0,0);
        if(fieldList!=null){
            //get fields
            for (Map<String,Object> f:fieldList){
                BasicDBObject field=new BasicDBObject();
                field.append(VIEW_FIELD_FIELDS_NAME,MapUtils.getString(f,VIEW_FIELD_FIELDS_NAME));
                field.append(VIEW_FIELD_FIELDS_LABEL,MapUtils.getString(f,VIEW_FIELD_FIELDS_LABEL));
                field.append(VIEW_FIELD_FIELDS_TYPE,MapUtils.getString(f,VIEW_FIELD_FIELDS_TYPE));
                UIComponent uiComponent=formViewUtils.getComponentByType(MapUtils.getString(f,VIEW_FIELD_FIELDS_TYPE));
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_ATTRIBUTES))){
                    field.append(VIEW_FIELD_FIELDS_ATTRIBUTES, JSON.parse(MapUtils.getString(f,VIEW_FIELD_FIELDS_ATTRIBUTES)));
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_OPERATOR))&&"search".equals(type)){
                    field.append(VIEW_FIELD_FIELDS_OPERATOR,MapUtils.getString(f,VIEW_FIELD_FIELDS_OPERATOR));
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_TIP))){
                    field.append(VIEW_FIELD_FIELDS_TIP,MapUtils.getString(f,VIEW_FIELD_FIELDS_TIP));
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_VIEW))){
                    field.append(VIEW_FIELD_FIELDS_VIEW,MapUtils.getString(f,VIEW_FIELD_FIELDS_VIEW));
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_TITLE))){
                    field.append(VIEW_FIELD_FIELDS_TITLE,MapUtils.getString(f,VIEW_FIELD_FIELDS_TITLE));
                }
                if("false".equals(MapUtils.getString(f,VIEW_FIELD_FIELDS_DEFAULT))&&uiComponent.hasDefaultValue()){
                    field.append(VIEW_FIELD_FIELDS_DEFAULT,false);
                }
                if("false".equals(MapUtils.getString(f,VIEW_FIELD_FIELDS_UPDATE))){
                    field.append(VIEW_FIELD_FIELDS_UPDATE,false);
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_PROCESSOR))){
                    view.append(VIEW_FIELD_FIELDS_PROCESSOR,f.get(VIEW_FIELD_FIELDS_PROCESSOR));
                }

                //get permission
                if("true".equals(MapUtils.getString(f,"usePermission"))){
                    List<String> list=new ArrayList();
                    List<Map<String,Object>> result=jdbcService.findList(TBL_DYNA_FIELD_PERMISSION,new BasicBSONObject("fieldId",f.get(DB_FIELD__ID)),null,0,0);
                    processPermission(list,result);
                    field.append("permission",list);
                }
                //container
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_CONTAINER))){
                    field.append(VIEW_FIELD_FIELDS_CONTAINER,JSON.parse(MapUtils.getString(f,VIEW_FIELD_FIELDS_CONTAINER)));
                }
                //join
                if(StringUtils.isNotEmpty(MapUtils.getString(f,VIEW_FIELD_FIELDS_JOIN))){
                    field.append(VIEW_FIELD_FIELDS_JOIN,JSON.parse(MapUtils.getString(f,VIEW_FIELD_FIELDS_JOIN)));
                }
                //get converter

                String converterId=MapUtils.getString(f,VIEW_FIELD_FIELDS_CONVERTER);
                BasicDBObject converterObject=getConverter(converterId);
                if(converterObject!=null){
                    field.append(VIEW_FIELD_FIELDS_CONVERTER,converterObject);
                }

                String optionId=MapUtils.getString(f,VIEW_FIELD_FIELDS_OPTION);


                if(uiComponent.hasOptions()){
                    //get option provider
                    if(StringUtils.isNotEmpty(optionId)){
                        BasicDBObject optionObject=new BasicDBObject();
                        Map<String,Object> option=jdbcService.find(TBL_DYNA_OPTION,new BasicBSONObject("_id",optionId));
                        if(option!=null){
//                            optionObject.append(VIEW_OPTION_NAME,MapUtils.getString(option,VIEW_OPTION_NAME));
                            if(StringUtils.isNotEmpty(MapUtils.getString(option,VIEW_OPTION_PROVIDER))){
                                optionObject.append(VIEW_OPTION_PROVIDER,MapUtils.getString(option,VIEW_OPTION_PROVIDER));
                            }
                            if(StringUtils.isNotEmpty(MapUtils.getString(option,VIEW_OPTION_VALUES))){
                                optionObject.append(VIEW_OPTION_VALUES,JSON.parse(MapUtils.getString(option,VIEW_OPTION_VALUES)));
                            }
                            if(StringUtils.isNotEmpty(MapUtils.getString(option,VIEW_OPTION_PARAMETER))){
                                optionObject.append(VIEW_OPTION_PARAMETER,JSON.parse(MapUtils.getString(option,VIEW_OPTION_PARAMETER)));
                            }
                            field.append(VIEW_FIELD_FIELDS_OPTION,optionObject);
                        }

                    }

                }

                fields.add(field);
            }
            if(fields.size()>0)
                view.append(VIEW_FIELD_FIELDS,fields);
        }
        //validator
        sort.clear();
        sort.put("createdDate","asc");
        List<BasicDBObject> validators=new ArrayList();
        List<Map<String,Object>> validatorList=jdbcService.findList(TBL_DYNA_FORM_VALIDATOR,new BasicBSONObject("formId",id),sort,0,0);

        if(validatorList!=null){
            for(Map<String,Object> v:validatorList){
                BasicDBObject validatorObject=new BasicDBObject();

                validatorObject.append(VIEW_FIELD_VALIDATORS_FIELD,MapUtils.getString(v,VIEW_FIELD_VALIDATORS_FIELD));
                validatorObject.append(VIEW_FIELD_VALIDATORS_PROVIDER,MapUtils.getString(v,VIEW_FIELD_VALIDATORS_PROVIDER));
                validatorObject.append(VIEW_FIELD_VALIDATORS_MSG,MapUtils.getString(v,VIEW_FIELD_VALIDATORS_MSG));
                if(VIEW_FIELD_VALIDATORS_MATCH.equals(MapUtils.getString(v,VIEW_FIELD_VALIDATORS_PROVIDER))){
                    validatorObject.append(VIEW_FIELD_VALIDATORS_MATCH,MapUtils.getString(v,VIEW_FIELD_VALIDATORS_MATCH));
                }
                if("true".equals(MapUtils.getString(v,VIEW_FIELD_VALIDATORS_EXTERNAL))){
                    validatorObject.append(VIEW_FIELD_VALIDATORS_EXTERNAL,true);
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(v,VIEW_FIELD_VALIDATORS_PARAMETER))){
                    validatorObject.append(VIEW_FIELD_VALIDATORS_PARAMETER,JSON.parse(MapUtils.getString(v,VIEW_FIELD_VALIDATORS_PARAMETER)));
                }

                validators.add(validatorObject);
            }
            if (validators.size()>0)
                view.append(VIEW_FIELD_VALIDATORS,validators);
        }

        //action
        List<BasicDBObject> actions=new ArrayList();
        List<Map<String,Object>> actionList=jdbcService.findList(TBL_DYNA_FORM_ACTION,new BasicBSONObject("formId",id),sort,0,0);

        if(actionList!=null){
            for (Map<String,Object> a:actionList){
                BasicDBObject actionObject=new BasicDBObject();
                actionObject.append(VIEW_ACTION_NAME,MapUtils.getString(a,VIEW_ACTION_NAME));
                actionObject.append(VIEW_ACTION_LABEL,MapUtils.getString(a,VIEW_ACTION_LABEL));
                if(StringUtils.isNotEmpty(MapUtils.getString(a,VIEW_ACTION_TITLE))){
                    actionObject.append(VIEW_ACTION_TITLE,MapUtils.getString(a,VIEW_ACTION_TITLE));
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(a,VIEW_ACTION_STYLE))){
                    actionObject.append(VIEW_ACTION_STYLE,MapUtils.getString(a,VIEW_ACTION_STYLE));
                }
                String action=MapUtils.getString(a,"action");
                if(VIEW_ACTION_SUBMIT.equals(action)){
                    actionObject.append(VIEW_ACTION_SUBMIT,true);
                    if(MapUtils.getBoolean(a,VIEW_ACTION_UPDATE,true)==false){
                        actionObject.append(VIEW_ACTION_UPDATE,false);
                    }

                }else if(VIEW_ACTION_CLOSE.equals(action)){
                    actionObject.append(VIEW_ACTION_CLOSE,true);
                }else if(VIEW_ACTION_SEARCH.equals(action)){
                    actionObject.append(VIEW_ACTION_SEARCH,true);
                }else if(VIEW_ACTION_BACK.equals(action)){
                    actionObject.append(VIEW_ACTION_BACK,true);
                    if(StringUtils.isNotEmpty(MapUtils.getString(a,VIEW_ACTION_VIEW))&&StringUtils.isNotEmpty(MapUtils.getString(a,VIEW_ACTION_TYPE))){
                        actionObject.append(VIEW_ACTION_VIEW,MapUtils.getString(a,VIEW_ACTION_VIEW));
                        actionObject.append(VIEW_ACTION_TYPE,MapUtils.getString(a,VIEW_ACTION_TYPE));
                        actionObject.append(VIEW_ACTION_ID,MapUtils.getString(a,VIEW_ACTION_ID));
                    }

                }else if(VIEW_ACTION_EXPORT.equals(action)){
                    actionObject.append(VIEW_ACTION_EXPORT,true);
                    actionObject.append(VIEW_ACTION_VIEW,MapUtils.getString(a,VIEW_ACTION_VIEW));
                }else if(VIEW_ACTION_POPUP.equals(action)){
                    actionObject.append(VIEW_ACTION_POPUP,true);
                    actionObject.append(VIEW_ACTION_VIEW,MapUtils.getString(a,VIEW_ACTION_VIEW));
                    actionObject.append(VIEW_ACTION_TYPE,MapUtils.getString(a,VIEW_ACTION_TYPE));
                    actionObject.append(VIEW_ACTION_REFRESH,MapUtils.getString(a,VIEW_ACTION_REFRESH));
//                    actionObject.append(VIEW_ACTION_TITLE,MapUtils.getString(a,VIEW_ACTION_TITLE));
                    if(StringUtils.isNotEmpty(MapUtils.getString(a,VIEW_ACTION_PARAMETER))){
                        actionObject.append(VIEW_ACTION_PARAMETER,JSON.parse(MapUtils.getString(a,VIEW_ACTION_PARAMETER)));
                    }
                }else if(VIEW_ACTION_VIEW.equals(action)){
                    actionObject.append(VIEW_ACTION_VIEW,MapUtils.getString(a,VIEW_ACTION_VIEW));
                    actionObject.append(VIEW_ACTION_TYPE,MapUtils.getString(a,VIEW_ACTION_TYPE));
                    if(StringUtils.isNotEmpty(MapUtils.getString(a,VIEW_ACTION_PARAMETER))){
                        actionObject.append(VIEW_ACTION_PARAMETER,JSON.parse(MapUtils.getString(a,VIEW_ACTION_PARAMETER)));
                    }
                }else if(VIEW_ACTION_URL.equals(action)){
                    actionObject.append(VIEW_ACTION_URL,MapUtils.getString(a,VIEW_ACTION_URL));
                }else if(VIEW_ACTION_JAVASCRIPT.equals(action)){
                    actionObject.append(VIEW_ACTION_JAVASCRIPT,MapUtils.getString(a,VIEW_ACTION_JAVASCRIPT));
                }
                //get permission
                if("true".equals(MapUtils.getString(a,"usePermission"))){
                    List<String> list=new ArrayList();
                    List<Map<String,Object>> result=jdbcService.findList(TBL_DYNA_ACTION_PERMISSION,new BasicBSONObject("actionId",a.get(DB_FIELD__ID)),null,0,0);
                    processPermission(list,result);
                    actionObject.append("permission",list);
                }
                actions.add(actionObject);
            }
            if (actions.size()>0)
                view.append(VIEW_FIELD_ACTIONS,actions);
        }


        //get the change
        List<BasicDBObject> changes=new ArrayList();
        List<Map<String,Object>> changeList=jdbcService.findList(TBL_DYNA_FORM_CHANGE,new BasicBSONObject("formId",id),sort,0,0);
        if(changeList!=null){
            for (Map<String,Object> c:changeList){
                BasicDBObject changeObject=new BasicDBObject();

                changeObject.append(VIEW_CHANGE_NAME,MapUtils.getString(c,VIEW_CHANGE_NAME));
                changeObject.append(VIEW_CHANGE_TARGET,MapUtils.getString(c,VIEW_CHANGE_TARGET));
                if("toggle".equals(MapUtils.getString(c,VIEW_CHANGE_TYPE))){
                    changeObject.append(VIEW_CHANGE_TYPE,MapUtils.getString(c,VIEW_CHANGE_TYPE));
                    changeObject.append(VIEW_CHANGE_VALUE,MapUtils.getString(c,VIEW_CHANGE_VALUE));
                }
                changes.add(changeObject);
            }
            if (changes.size()>0)
                view.append(VIEW_FIELD_CHANGE,changes);
        }

        //for search type
        if("search".equals(type)){
            List<BasicDBObject> results=new ArrayList();
            List<Map<String,Object>> resultList=jdbcService.findList(TBL_DYNA_FORM_RESULT,new BasicBSONObject("formId",id),sort,0,0);
            if(resultList!=null){
                for (Map<String,Object> r:resultList){
                    BasicDBObject result=new BasicDBObject();

                    result.append(VIEW_FIELD_RESULT_NAME,MapUtils.getString(r,VIEW_FIELD_RESULT_NAME));
                    result.append(VIEW_FIELD_RESULT_LABEL,MapUtils.getString(r,VIEW_FIELD_RESULT_LABEL));
                    if(StringUtils.isNotEmpty(MapUtils.getString(r,VIEW_FIELD_RESULT_ATTIBUTES))){
                        result.append(VIEW_FIELD_RESULT_ATTIBUTES, JSON.parse(MapUtils.getString(r,VIEW_FIELD_RESULT_ATTIBUTES)));
                    }
                    //get permission
                    if("true".equals(MapUtils.getString(r,"usePermission"))){
                        List<String> list=new ArrayList();
                        List<Map<String,Object>> permissions=jdbcService.findList(TBL_DYNA_RESULT_PERMISSION,new BasicBSONObject("resultId",r.get(DB_FIELD__ID)),null,0,0);
                        processPermission(list,permissions);
                        result.append(VIEW_FIELD_RESULT_PERMISSION,list);
                    }

                    //get converter
                    String converterId=MapUtils.getString(r,VIEW_FIELD_RESULT_CONVERTER);
                    BasicDBObject converterObject=getConverter(converterId);
                    if(converterObject!=null){
                        result.append(VIEW_FIELD_RESULT_CONVERTER,converterObject);
                    }


                    results.add(result);
                }
                if (results.size()>0)
                    view.append(VIEW_FIELD_RESULT,results);
            }

            //get the operate
            List<BasicDBObject> operates=new ArrayList();
            List<Map<String,Object>> operateList=jdbcService.findList(TBL_DYNA_FORM_OPERATE,new BasicBSONObject("formId",id),sort,0,0);
            if(operateList!=null){
                for (Map<String,Object> o: operateList){
                    BasicDBObject operateObject=new BasicDBObject();

                    operateObject.append(VIEW_FIELD_OPERATE_NAME,MapUtils.getString(o,VIEW_FIELD_OPERATE_NAME));
                    operateObject.append(VIEW_FIELD_OPERATE_ICON,MapUtils.getString(o,VIEW_FIELD_OPERATE_ICON));
                    if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_CONFIRM))){
                        operateObject.append(VIEW_FIELD_OPERATE_CONFIRM,MapUtils.getString(o,VIEW_FIELD_OPERATE_CONFIRM));
                    }
                    if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_DEPEND))){
                        operateObject.append(VIEW_FIELD_OPERATE_DEPEND,MapUtils.getString(o,VIEW_FIELD_OPERATE_DEPEND));
                    }
                    if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_VALUE))){
                        operateObject.append(VIEW_FIELD_OPERATE_VALUE,MapUtils.getString(o,VIEW_FIELD_OPERATE_VALUE));
                    }
                    if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_CHANGE))){
                        operateObject.append(VIEW_FIELD_OPERATE_CHANGE,MapUtils.getString(o,VIEW_FIELD_OPERATE_CHANGE));
                    }
                    String operate=MapUtils.getString(o,VIEW_FIELD_OPERATE_OPERATE);
                    if(VIEW_FIELD_OPERATE_OPERATE.equals(operate)){
                        operateObject.append(VIEW_FIELD_OPERATE_OPERATE,true);
                        String provider=MapUtils.getString(o,VIEW_FIELD_OPERATE_PROVIDER);
                        if(StringUtils.isNotEmpty(provider)){
                            operateObject.append(VIEW_FIELD_OPERATE_PROVIDER,provider);
                        }
                        if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_REFRESH))){
                            operateObject.append(VIEW_FIELD_OPERATE_REFRESH,MapUtils.getString(o,VIEW_FIELD_OPERATE_REFRESH));
                        }
                    }else if (VIEW_FIELD_OPERATE_POPUP.equals(operate)){
                        operateObject.append(VIEW_FIELD_OPERATE_POPUP,true);
                        operateObject.append(VIEW_FIELD_OPERATE_VIEW,MapUtils.getString(o,VIEW_FIELD_OPERATE_VIEW));
                        operateObject.append(VIEW_FIELD_OPERATE_TYPE,MapUtils.getString(o,VIEW_FIELD_OPERATE_TYPE));
                        if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_REFRESH))){
                            operateObject.append(VIEW_FIELD_OPERATE_REFRESH,MapUtils.getString(o,VIEW_FIELD_OPERATE_REFRESH));
                        }

                    }else if (VIEW_FIELD_OPERATE_VIEW.equals(operate)){
                        operateObject.append(VIEW_FIELD_OPERATE_VIEW,MapUtils.getString(o,VIEW_FIELD_OPERATE_VIEW));
                        operateObject.append(VIEW_FIELD_OPERATE_TYPE,MapUtils.getString(o,VIEW_FIELD_OPERATE_TYPE));
                        if(StringUtils.isNotEmpty(MapUtils.getString(o,VIEW_FIELD_OPERATE_REFRESH))){
                            operateObject.append(VIEW_FIELD_OPERATE_REFRESH,MapUtils.getString(o,VIEW_FIELD_OPERATE_REFRESH));
                        }
                    }

                    //get permission
                    if("true".equals(MapUtils.getString(o,"usePermission"))){
                        List<String> list=new ArrayList();
                        List<Map<String,Object>> result=jdbcService.findList(TBL_DYNA_OPERATE_PERMISSION,new BasicBSONObject("operateId",o.get(DB_FIELD__ID)),null,0,0);
                        processPermission(list,result);
                        operateObject.append("permission",list);
                    }

                    operates.add(operateObject);
                }
                if (operates.size()>0)
                    view.append(VIEW_FIELD_OPERATE,operates);
            }

        }
        //for create

        if("create".equals(type)){
            //get the subview
            List<BasicDBObject> subviews=new ArrayList();
            List<Map<String,Object>> subviewList=jdbcService.findList(TBL_DYNA_FORM_SUBVIEW,new BasicBSONObject("formId",id),sort,0,0);
            if(subviewList!=null){
                for (Map<String,Object> s:subviewList){
                    BasicDBObject subviewObject=new BasicDBObject();
                    subviewObject.append(VIEW_SUBVIEW_NAME,MapUtils.getString(s,VIEW_SUBVIEW_NAME));
                    if(StringUtils.isNotEmpty(MapUtils.getString(s,VIEW_SUBVIEW_PARAMETER))){
                        subviewObject.append(VIEW_SUBVIEW_PARAMETER,JSON.parse(MapUtils.getString(s,VIEW_SUBVIEW_PARAMETER)));
                    }

                    subviews.add(subviewObject);
                }
            }
            if(subviews.size()>0)
                view.append(VIEW_FIELD_SUBVIEW,subviews);


        }
        return com.alibaba.fastjson.JSON.toJSONString(view,true);
    }
    private BasicDBObject getConverter(String converterId) {
        if(StringUtils.isNotEmpty(converterId)){
            BasicDBObject converterObject=new BasicDBObject();
            Map<String,Object> converter= jdbcService.find(TBL_DYNA_CONVERTER,new BasicBSONObject("_id",converterId));

            if(converter!=null){
//                converterObject.append(VIEW_CONVERTER_NAME,MapUtils.getString(converter,VIEW_CONVERTER_NAME));
                if(StringUtils.isNotEmpty(MapUtils.getString(converter,VIEW_CONVERTER_PROVIDER))){
                    converterObject.append(VIEW_CONVERTER_PROVIDER,MapUtils.getString(converter,VIEW_CONVERTER_PROVIDER));
                }
                if(StringUtils.isNotEmpty(MapUtils.getString(converter,VIEW_CONVERTER_PARAMETER))){
                    converterObject.append(VIEW_CONVERTER_PARAMETER,JSON.parse(MapUtils.getString(converter,VIEW_CONVERTER_PARAMETER)));
                }
                return converterObject;
            }
        }
        return null;
    }

    private void processPermission(List<String> list, List<Map<String, Object>> result) {
        if(result!=null){
            for (Map<String,Object> r:result){
                String pid=MapUtils.getString(r,"permissionId");
                Map permission=jdbcService.find(DbConstant.TBL_PERMISSION, new BasicBSONObject("_id",pid));
                if(permission!=null){
                    list.add(MapUtils.getString(permission,"name"));
                }
            }
        }
    }
}
