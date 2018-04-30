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

package com.ifreelight.dyna.utils;

import com.ifreelight.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 24/11/2017.
 */
@Component
@CacheConfig
public class JsonUtils {
    private Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    @Autowired
    ClasspathViewLoader classpathViewLoader;
    @Autowired
    JDBCService jdbcService;

//    @Cacheable(key = "#sub+'.'+#name", value = "json")
    public String getJsonStringFromClassPath(String name, String sub) {
        Resource resource = getFromResourcesLoader(name);
        if (resource != null) {
            try (InputStream inputStream = resource.getInputStream()) {
                List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
                StringBuffer json = new StringBuffer();
                for (String s : lines) {
                    json.append(s);
                    json.append("\n");
                }
                return json.toString();

            } catch (IOException e) {
                logger.error("view not exist: {}", name);
            }
        }

        return null;
    }

    public Resource getFromResourcesLoader(String name) {
        StringBuffer buffer = new StringBuffer(name).append(".json");
        for (Resource resource : classpathViewLoader.getMatchingResources()) {
            if (resource.getFilename().equals(buffer.toString())) {
                return resource;
            }
        }
        return null;
    }


    public String getJsonStringFromDB(String name){
        Map data=jdbcService.find("tbl_view_deployment",new BasicDBObject("name",name));
        if(data==null)
            return null;
        return MapUtils.getString(data,"data");
    }
}
