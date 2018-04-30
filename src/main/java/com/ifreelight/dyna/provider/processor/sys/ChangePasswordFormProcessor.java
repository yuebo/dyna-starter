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

package com.ifreelight.dyna.provider.processor.sys;

import com.ifreelight.dyna.core.ViewContext;
import com.ifreelight.dyna.provider.processor.DefaultFormProcessor;
import com.ifreelight.dyna.utils.Md5Utils;
import com.ifreelight.dyna.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yuebo on 30/11/2017.
 */
@Component
public class ChangePasswordFormProcessor extends DefaultFormProcessor {
    @Autowired
    UserUtils userUtils;
    @Autowired
    Md5Utils md5Utils;
    @Override
    public Map<String, Object> load(ViewContext viewContext, Map condition) {
        String id=(String)userUtils.currentUser().get(DB_FIELD__ID);
        condition.put(DB_FIELD__ID,id);
        viewContext.setId(id);
        return super.load(viewContext, condition);
    }

    @Override
    public String process(ViewContext viewContext, Map saveEntity) {
        String id=(String)userUtils.currentUser().get(DB_FIELD__ID);
        viewContext.setId(id);
        saveEntity.put("password",md5Utils.md5((String)saveEntity.get("password")));
        return super.process(viewContext, saveEntity);
    }
}
