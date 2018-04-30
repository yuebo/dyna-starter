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

package com.github.yuebo.dyna.event;

import com.github.yuebo.dyna.utils.FormViewUtils;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.github.yuebo.dyna.core.FileProcessor;
import com.github.yuebo.dyna.core.PermissionProvider;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.service.JDBCService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: yuebo
 * Date: 12/8/14
 * Time: 3:00 PM
 */
@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileUploadEventListener implements ApplicationListener<FileUploadEvent> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected JDBCService jdbcService;
    @Autowired
    protected SpringUtils springUtils;
    @Autowired
    protected FormViewUtils formViewUtils;

    @Autowired
    PermissionProvider permissionProvider;

    @Override
    public void onApplicationEvent(FileUploadEvent event) {
        String userName=MapUtils.getString(event.getUser(),"name");
        logger.info("upload event is received: {} by {}", event.getUpload(), userName);
        Map<String, Object> uploadView = formViewUtils.getFormUpload(event.getUpload());

        ViewContext viewContext=new ViewContext(uploadView);

        boolean hasPermission=permissionProvider.hasPermission(viewContext.getPermission());
        if(!hasPermission){
            logger.info("no access for the user {} at view {}",userName, event.getUpload());
        }else {
            FileProcessor fileProcessor = springUtils.getFileProcessor(viewContext.getProcessor());
            if (fileProcessor.accept(event)) {
                logger.info("file is accepted by this file processor {}", fileProcessor.getClass().getName());

                fileProcessor.processFile(event);
            }else {
                logger.info("file is rejected by this file processor {}", fileProcessor.getClass().getName());
            }

        }
        logger.info("upload event end");




    }

}
