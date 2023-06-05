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
import com.github.yuebo.dyna.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yuebo on 5/18/2015.
 */
@Component
@Slf4j
public class SpringUtils implements ApplicationContextAware, AppConstants {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ConvertProvider getConvertProvider(Map converter) {
        ConvertProvider convertProvider = null;
        if (converter == null) {
            convertProvider = (ConvertProvider) applicationContext.getBean("defaultConvertProvider");
        } else {
            convertProvider = (ConvertProvider) applicationContext.getBean((String) converter.get("provider"));
        }
        return convertProvider;
    }

    public OptionProvider getOptionProvider(Map provider) {
        OptionProvider optionProvider = null;
        if (provider == null||MapUtils.getString(provider,"provider")==null) {
            optionProvider = (OptionProvider) applicationContext.getBean("defaultOptionProvider");
        } else {
            optionProvider = (OptionProvider) applicationContext.getBean(MapUtils.getString(provider,"provider"));
        }
        return optionProvider;
    }

    public FormProcessor getFormProcessor(ViewContext viewContext) {
        FormProcessor formProcessor = null;
        if (viewContext.getProcessor() != null) {
            formProcessor = (FormProcessor) applicationContext.getBean(viewContext.getProcessor());
        }
        if (formProcessor == null) {
            formProcessor = (FormProcessor) applicationContext.getBean("defaultFormProcessor");
        }
        return formProcessor;
    }

    public SearchFormProcessor getSearchFormProcessor(Map map) {
        SearchFormProcessor formProcessor = null;
        String processor = (String) map.get("processor");
        if (StringUtils.isNotEmpty(processor)) {
            formProcessor = (SearchFormProcessor) applicationContext.getBean(processor);
        } else {
            formProcessor = (SearchFormProcessor) applicationContext.getBean("defaultSearchFormProcessor");
        }
        return formProcessor;
    }

    public ValidatorProvider getValidatorProvider(String name) {
        return (ValidatorProvider) applicationContext.getBean(name);
    }

    public ResultIterator getResultiterator(String name) {
        return (ResultIterator) applicationContext.getBean(name);
    }

    public void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    public ArrayList<Map<String, Object>> getActualResultList(
            ViewContext viewContext) {
        ArrayList<Map<String, Object>> actualResultList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> resultlist = viewContext.getResult();
        if (resultlist != null) {
            for (Map<String, Object> r : resultlist) {
                if (r.get("iterator") == null) {
                    actualResultList.add(r);
                } else {
                    ResultIterator iterator = getResultiterator((String) r.get("iterator"));
                    iterator.init((Map) r.get("parameter"));
                    while (iterator.hasNext()) {
                        Map val = iterator.next();
                        actualResultList.add(val);
                    }

                }

            }
        }

        return actualResultList;
    }

    public List<Map> getResultList(ViewContext viewContext, List<Map<String, Object>> list) {
        List<String> resultFields = new ArrayList<String>();
        List<Map> resultList = new ArrayList<Map>();
        for (Map f : viewContext.getResult()) {
            resultFields.add((String) f.get("name"));
        }

        for (Map m : list) {
            Map m2 = new HashMap();
            for (Object s : m.keySet()) {
                if (resultFields.contains(s)) {
                    m2.put(s, m.get(s));
                }
            }
            m2.put(DB_FIELD__ID, m.get(DB_FIELD__ID));
            resultList.add(m2);
        }
        return resultList;
    }


    public static Object execute(EvaluationContext context, String exp) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(exp);
            return expression.getValue(context);
        } catch (Exception e) {
            log.error("error parse exp" + exp);
            return null;
        }


    }

    public ExpressionProvider getExpressionProvider(Map map) {
        ExpressionProvider expressionProvider = null;
        if (map.get("provider") != null) {
            expressionProvider = (ExpressionProvider) applicationContext.getBean((String) map.get("provider"));
        }
        if (expressionProvider == null) {
            expressionProvider = (ExpressionProvider) applicationContext.getBean("defaultExpressionProvider");
        }
        return expressionProvider;
    }


    public FileProcessor getFileProcessor(String map) {
        FileProcessor fileHandler = null;
        if (StringUtils.isNotEmpty(map)) {
            fileHandler = (FileProcessor) applicationContext.getBean(map);
        } else {
            fileHandler = (FileProcessor) applicationContext.getBean("defaultFileProcessor");
        }
        return fileHandler;
    }
    public List<String> listBean(Class c){
        return Arrays.asList(applicationContext.getBeanNamesForType(c));
    }
    public String getBeanClassName(String bean){
        return getClassNameWithoutSpringProxy(applicationContext.getBean(bean).getClass());
    }

    public OperateProvider getOperateProvider(String provider) {
        OperateProvider operate = null;
        if (StringUtils.isNotEmpty(provider)) {
            operate = (OperateProvider) applicationContext.getBean(provider);
        } else {
            operate = (OperateProvider) applicationContext.getBean("defaultOperateProvider");
        }
        return operate;
    }

    public String getClassNameWithoutSpringProxy(Class c){
        if(c.getName().contains("$$EnhancerBySpringCGLIB$$")){
            return c.getSuperclass().getName();
        }
        return c.getName();
    }

    public List<UIComponent> getComponents() {
        String [] beans=applicationContext.getBeanNamesForType(UIComponent.class);

        List<UIComponent> result=new ArrayList();
        for (String name:beans){
            result.add((UIComponent)applicationContext.getBean(name));
        }
        return result;
    }
}
