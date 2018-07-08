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

package com.github.yuebo.dyna.backup;

import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.service.JDBCService;
import com.github.yuebo.dyna.utils.ClasspathViewLoader;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 1/7/15
 * Time: 10:38 AM
 */
public class SystemInit {
    private static Logger logger= LoggerFactory.getLogger(SystemInit.class);
    private JDBCService jdbcService;
    private List<String> configFiles;
    private List<String> dataFiles;
    private boolean initData;
    private boolean initView;
    private ClasspathViewLoader viewLoader;

    public void setViewLoader(ClasspathViewLoader viewLoader) {
        this.viewLoader = viewLoader;
    }

    public void setInitData(boolean initData) {
        this.initData = initData;
    }

    public void setInitView(boolean initView) {
        this.initView = initView;
    }

    private ApplicationContext applicationContext;



    public void setConfigFiles(List<String> configFiles) {
        this.configFiles = configFiles;
    }

    public void setDataFiles(List<String> dataFiles) {
        this.dataFiles = dataFiles;
    }

    public void setJdbcService(JDBCService jdbcService) {
        this.jdbcService = jdbcService;
    }

    public  void init() throws IOException {

        if(initData){
            EvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setVariable("db", this);
            for (String file : dataFiles) {
                ClassPathResource classPathResource = new ClassPathResource("json/" + file);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(classPathResource.getInputStream(), outputStream);
                Map<String, List<Map>> list = (Map<String, List<Map>>) JSON.parse(new String(outputStream.toByteArray(), "utf-8"));

                for (String c : list.keySet()) {
                    String[] val = c.split("#");

                    List<Map> data = list.get(c);
                    HashMap temp = new HashMap();
                    for (Map d : data) {
                        temp.clear();
                        for (Object key : d.keySet()) {
                            if (d.get(key) instanceof String) {
                                String value = (String) d.get(key);
                                if (value.startsWith("#")) {
                                    Object result = SpringUtils.execute(evaluationContext, value);
                                    temp.put(key, result);
                                }

                            }
                        }
                        d.putAll(temp);
                        BasicDBObject condition = new BasicDBObject();
                        for (int j = 1; j < val.length; j++) {
                            condition.put(val[j], d.get(val[j]));
                        }
                        Map result = jdbcService.find(val[0], condition);
                        if (result == null) {
                            jdbcService.save(val[0], d);
                        } else {
                            //no update if found
                        }
                    }
                }


            }
        }

        if(initView){
            jdbcService.ensureLength("tbl_view_deployment","data",8000);
            jdbcService.ensureLength("tbl_view_deployment","name",500);
            Resource[] resources= viewLoader.getMatchingResources();

            for (Resource resource:resources){
                if (resource != null) {
                    try (InputStream inputStream = resource.getInputStream()) {
                        List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
                        StringBuffer json = new StringBuffer();
                        for (String s : lines) {
                            json.append(s);
                            json.append("\n");
                        }

                        ViewContext viewContext=new ViewContext((Map<String, Object>) JSON.parse(json.toString()));
                        Map<String,Object> result=jdbcService.find("tbl_view_deployment",new BasicDBObject("name",viewContext.getName()));
                        if(StringUtils.isEmpty(viewContext.getType())){
                            logger.error("view type cannot find {}",resource.getFilename());
                        }
                        if(result==null){
                            BasicDBObject view=new BasicDBObject();
                            view.append("name",viewContext.getName());
                            view.append("type",viewContext.getType());
                            view.append("data", com.alibaba.fastjson.JSON.toJSONString(viewContext.getViewMap(),true));
                            jdbcService.save("tbl_view_deployment",view);
                        }else {
                            logger.debug("view {} exist in db, ignore the file in classpath", viewContext.getName());
                        }

                    } catch (IOException e) {
                        logger.error("load view error: {}", resource.getFilename());
                    }
                }
            }






        }


    }

    public Object id(String table, String name, String value) {
        Map param = new HashMap();
        param.put("_data", table);
        param.put(name, value);
        return jdbcService.findData(param).get("id");
    }
    public Date now(){
        return new Date();
    }

}