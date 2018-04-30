package com.ifreelight.dyna.core;

import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
public class ConvertContext {
    private Map<String,Object> converterMap;
    public ConvertContext(Map<String,Object> converterMap){
        this.converterMap=converterMap;
    }
    public Map<String,Object> getParameter(){
        Map result= MapUtils.getMap(this.converterMap,"parameter");
        if(result==null){
            return MapUtils.EMPTY_MAP;
        }
        return result;
    }
}
