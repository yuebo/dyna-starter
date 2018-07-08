package com.github.yuebo.dyna.core;

import com.github.yuebo.dyna.event.FileUploadEvent;
import com.github.yuebo.dyna.utils.ConvertUtils;
import com.github.yuebo.dyna.AppConstants;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by yuebo on 29/11/2017.
 */
public class ViewContext implements AppConstants {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Map<String, Object> viewMap;
    private Map<String, List<Map<String, Object>>> messagesContext = new HashMap();
    Map<String, Map<String, Object>> refContext = new HashMap();
    private FileUploadEvent fileUploadEvent;


    public List<Map<String, Object>> getFields() {
        return (List) MapUtils.getObject(viewMap, VIEW_FIELD_FIELDS);
    }

    public List<Map<String, Object>> getFieldValidators() {
        List<Map<String,Object>> validators= (List) MapUtils.getObject(viewMap, VIEW_FIELD_VALIDATORS,Collections.emptyList());
        List<Map<String, Object>> result=new ArrayList();
        for (Map<String,Object> validator:validators){
            String type=MapUtils.getString(validator,"type","field");
            if("field".equals(type)){
                result.add(validator);
            }
        }
        return result;
    }

    public List<Map<String, Object>> getResultValidators() {
        List<Map<String,Object>> validators= (List) MapUtils.getObject(viewMap, VIEW_FIELD_VALIDATORS,Collections.emptyList());
        List<Map<String, Object>> result=new ArrayList();
        for (Map<String,Object> validator:validators){
            String type=MapUtils.getString(validator,"type","field");
            if("result".equals(type)){
                result.add(validator);
            }
        }
        return result;
    }

    public Map<String, Object> getProcess() {
        return (Map) viewMap.get(VIEW_FIELD_PROCESS);
    }

    public Map<String, Object> getInput() {
        return (Map) MapUtils.getObject(viewMap, VIEW_FIELD_INPUT);
    }

    public List<String> getInputParameter() {
        return (List) MapUtils.getObject(viewMap, VIEW_FIELD_INPUT_PARAMETER);
    }

    public ViewContext(Map<String, Object> viewMap) {
        this.viewMap = viewMap;
    }

    public String getId() {
        return MapUtils.getString(viewMap, VIEW_FIELD__ID);
    }

    public void setId(String id) {
        viewMap.put(VIEW_FIELD__ID, id);
    }

    public String getName() {
        return MapUtils.getString(viewMap, VIEW_FIELD_NAME);
    }

    public void setName(String name) {
        viewMap.put(VIEW_FIELD_NAME, name);
    }

    public String getType() {
        return MapUtils.getString(viewMap, VIEW_FIELD_TYPE);
    }

    public void setType(String type) {
        viewMap.put(VIEW_FIELD_TYPE, type);
    }

    public Boolean getReadOnly() {
        return MapUtils.getBoolean(viewMap, VIEW_FIELD_READONLY, false);
    }

    public void setReadOnly(Boolean readOnly) {
        viewMap.put(VIEW_FIELD_READONLY, readOnly);
    }

    public String getData() {
        return MapUtils.getString(viewMap, VIEW_FIELD_DATA);
    }

    public void setData(String data) {
        viewMap.put(VIEW_FIELD_DATA, data);
    }

    public void setTaskId(String taskId) {
        viewMap.put(VIEW_FIELD__TASKID, taskId);
    }

    public String getTaskId() {
        return MapUtils.getString(viewMap, VIEW_FIELD__TASKID);
    }

    public void setProcessor(String processor) {
        viewMap.put(VIEW_FIELD_PROCESSOR, processor);
    }

    public String getProcessor() {
        return MapUtils.getString(viewMap, VIEW_FIELD_PROCESSOR);
    }

    public boolean isViewValid() {
        return !(viewMap == null || viewMap.isEmpty());
    }

    public Map<String, Object> getViewMap() {
        return viewMap;
    }


    public Map<String, List<Map<String, Object>>> getMessagesContext() {
        return messagesContext;
    }

    public Map<String, Map<String, Object>> getRefContext() {
        return refContext;
    }

    public Boolean hasToken() {
        return MapUtils.getBoolean(viewMap, VIEW_FIELD_TOKEN, false);
    }

    public Map<String, Object> getMessages() {
        return (Map) MapUtils.getObject(viewMap, VIEW_FIELD_MESSAGES);
    }

    public String getRedirect() {
        return MapUtils.getString(viewMap, VIEW_FIELD_REDIRECT);
    }

    public String getQuery() {
        return MapUtils.getString(viewMap, VIEW_FIELD_QUERY);
    }

    public List<String> getKeys() {
        return (List) MapUtils.getObject(viewMap, "keys");
    }

    public List<Map<String, Object>> getResult() {
        return (List) MapUtils.getObject(viewMap, VIEW_FIELD_RESULT,Collections.emptyList());
    }

    public void setResult(List<Map<String, Object>> result) {
        viewMap.put(VIEW_FIELD_RESULT, result);
    }


    public void setFieldValue(String field,Object value){
        for(Map<String,Object> f:this.getFields()){
            if(field.equals(f.get(VIEW_FIELD_FIELDS_NAME))){
                ConvertUtils.setProperty(f,"attributes.value",value);
            }
        }
    }

    public boolean hasFieldType(String type){
        boolean has=false;
        if(this.getFields()!=null){
            for(Map<String,Object> f:this.getFields()){
                if(type.equals(f.get(VIEW_FIELD_FIELDS_TYPE))){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasId(){
        return StringUtils.isNotEmpty(getId());
    }


    public String getPath(){
        return MapUtils.getString(viewMap,VIEW_FIELD_PATH);
    }

    public List<Map<String, Object>> getOperate() {
        return (List) MapUtils.getObject(viewMap, VIEW_FIELD_OPERATE);
    }
    public Map<String, Object> getOperate(String name) {
        List<Map<String, Object>> operates=getOperate();
        if(operates!=null){
            for (Map<String,Object> op:operates){
                if(name.equals(MapUtils.getString(op,VIEW_FIELD_OPERATE_NAME))){
                    return op;
                }
            }
        }
        return null;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public Map<String,Object> getDefaultValue(){
        return MapUtils.getMap(viewMap,VIEW_FIELD_DEFAULT_VALUE);
    }

    public List<String> getPermission() {
        return (List)MapUtils.getObject(viewMap,VIEW_FIELD_PERMISSION);
    }

    public Map<String,Object> getExport() {
        return MapUtils.getMap(viewMap,"export");
    }


    public String getIdFieldName(){
       List<Map<String,Object>> list= getResult();
       for(Map<String,Object> result:list){
           if(MapUtils.getBoolean(result,DB_FIELD__ID,false)){
               return MapUtils.getString(result,VIEW_FIELD_RESULT_NAME);
           }
       }
       return DB_FIELD__ID;
    }


    public Map<String,Object> getChart(String chartName){
        List<Map<String,Object>> charts=(List)MapUtils.getObject(viewMap,VIEW_FIELD_CHARTS);
        if(charts!=null){
            for (Map<String,Object> chart:charts){
                if(chartName.equals(MapUtils.getString(chart,VIEW_FIELD_CHARTS_NAME))){
                    return chart;
                }
            }
        }
        return null;
    }

    public ChartContext toChartContext(String chartName){
        ChartContext chartContext=new ChartContext(getChart(chartName));
        chartContext.setViewContext(this);
        return chartContext;
    }

    public boolean isSelfOnly(){
        return MapUtils.getBoolean(viewMap,VIEW_FIELD_SELF_ONLY,false);
    }

    public List<String> getDataPermission() {
        return (List)MapUtils.getObject(viewMap,VIEW_FIELD_DATA_PERMISSION);
    }

    public List<String> getComponentList(){
        List<Map<String,Object>> fields=getFields();
        List<String> paths=new ArrayList();
        if(fields!=null&&!fields.isEmpty()){
            for(Map<String,Object> field:fields){
                String type=MapUtils.getString(field,VIEW_FIELD_FIELDS_TYPE);
                if(!paths.contains(type)){
                    paths.add(type);
                }
            }
        }
        return paths;
    }

    public boolean getCustomView(){
        return StringUtils.equals("custom",MapUtils.getString(this.viewMap,VIEW_FIELD_TYPE));
    }
}
