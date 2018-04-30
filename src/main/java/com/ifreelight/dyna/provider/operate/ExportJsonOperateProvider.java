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

package com.ifreelight.dyna.provider.operate;

import com.ifreelight.dyna.AppConstants;
import com.ifreelight.dyna.FormConstants;
import com.ifreelight.dyna.core.OperateContext;
import com.ifreelight.dyna.core.ViewContext;
import com.ifreelight.dyna.utils.ExportJsonUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
@Component
public class ExportJsonOperateProvider extends DefaultOperateProvider implements AppConstants,FormConstants {
    @Autowired
    ExportJsonUtils  exportJsonUtils;
    private static Logger logger=Logger.getLogger(ExportJsonOperateProvider.class);
    @Override
    protected boolean doOperate(ViewContext viewContext, OperateContext operateContext) {
        //get the id from parameter,
        String id=request.getParameter("_id");
        if (id==null){
            throw new RuntimeException("error id found");
        }

        String json=exportJsonUtils.exportJson(id);
        Map view=(Map)JSON.parse(json);

        BasicDBObject filter=new BasicDBObject("name",MapUtils.getString(view,VIEW_FIELD_NAME));
        jdbcService.ensureLength("tbl_view_deployment","data",10000);
        jdbcService.ensureLength("tbl_view_deployment","name",500);
        Map data=jdbcService.find("tbl_view_deployment",filter);
        if(data==null){
            data=new HashMap();
            data.put("name",MapUtils.getString(view,VIEW_FIELD_NAME));
            data.put("data",json);
            data.put("type",MapUtils.getString(view,VIEW_FIELD_TYPE));
            jdbcService.save("tbl_view_deployment", data);
        }else {
            data.put("data",json);
            data.put("type",MapUtils.getString(view,VIEW_FIELD_TYPE));
            jdbcService.update("tbl_view_deployment", filter,data);
        }

        return false;
    }


}
