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

package com.github.yuebo.dyna.provider.processor.sys;

import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.provider.processor.DefaultFormProcessor;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/7.
 */
@Component
public class PreviewFormProcessor extends DefaultFormProcessor {

    @Override
    public Map<String, Object> load(ViewContext viewContext, Map condition) {
        String id = (String) condition.get(DB_FIELD__ID);
        HashMap map = new HashMap();

        Map view=jdbcService.find("tbl_view_deployment",new BasicDBObject("_id",id));

        if(view==null){
            viewContext.getViewMap().put("view", VIEW_OUTPUT_ERROR);
            return Collections.EMPTY_MAP;
        }
        Map viewMap= (Map)JSON.parse(MapUtils.getString(view,"data"));

        viewContext.getViewMap().put("view", "redirect:/spring/data/" + MapUtils.getString(viewMap,"type")+"/"+MapUtils.getString(viewMap,"name"));
        return map;
    }
}
