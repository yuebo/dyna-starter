/*
 *
 *  *
 *  *  * Copyright 2002-2017 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 *
 */

package com.github.yuebo.dyna.provider.operate.course;

import com.github.yuebo.dyna.core.OperateContext;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.provider.operate.DefaultOperateProvider;
import com.mongodb.BasicDBObject;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DicussActiveOperateProvider extends DefaultOperateProvider {

    @Override
    protected boolean doOperate(ViewContext viewContext, OperateContext operateContext) {
        String id=request.getParameter("id");

        Map<String,Object> test= jdbcService.find("biz_discuss",new BasicDBObject("id",id).append("status","I"));

        if(test!=null){
            jdbcService.update("biz_discuss",new BasicDBObject("id",id),new BasicDBObject("status",getStatus()));
        }

        return super.doOperate(viewContext, operateContext);
    }

    protected Object getStatus() {
        return "A";
    }

}
