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
import com.github.yuebo.dyna.utils.MessageUtils;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.FileProcessor;
import com.github.yuebo.dyna.service.JDBCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuebo on 27/11/2017.
 */
@Component
public class DefaultFileProcessor implements FileProcessor, AppConstants {
    protected static final String ERROR_TYPE_RECORD = "Record";
    protected static final String ERROR_TYPE_TABLE = "Table";
    @Autowired
    protected JDBCService jdbcService;
    @Autowired
    protected FormViewUtils formViewUtils;
    @Autowired
    protected MessageUtils messageUtils;

    @Autowired
    protected SpringUtils springUtils;
    protected static Logger logger = LoggerFactory.getLogger(DefaultFileProcessor.class);

    @Override
    public boolean accept(FileUploadEvent event) {
        return true;
    }

    @Override
    @Async
    public void processFile(FileUploadEvent event) {
        String jobId = prepareJob(event);
        updateJob(event, "Completed", jobId, 0);
    }


    protected String prepareJob(FileUploadEvent event) {
        Map<String, Object> job = new HashMap<>();
        job.put("uploadTime", new Date());
        job.put("fileName", event.getFileName());
        job.put("baseName", event.getBaseName());
        job.put("upload", event.getUpload());
        job.put(DB_FIELD__ID, event.getId());
        initCreateInfo(job, event.getUser());
        jdbcService.save(TBL_JOB, job);
        return (String) job.get(DB_FIELD__ID);

    }

    protected void logError(FileUploadEvent event, String jobId, String type, String msg, Integer row) {
        Map<String, Object> job = new HashMap<>();
        job.put("jobId", jobId);
        job.put("type", type);
        job.put("msg", msg);
        if (row != null)
            job.put("row", row.intValue());
        initCreateInfo(job, event.getUser());
        jdbcService.save(TBL_JOB_ERROR, job);

    }


    protected void updateJob(FileUploadEvent event, String status, String jobId, long errorCount) {
        HashMap condition = new HashMap();
        condition.put(DB_FIELD__ID, jobId);
        Map job = new HashMap();
        job.put("status", status);
        job.put("completeDate", new Date());
        job.put("errorCnt", errorCount);
        initUpdateInfo(job, event.getUser());
        Map con = new HashMap(condition);
        jdbcService.update(TBL_JOB, con, job);
    }

    protected void initCreateInfo(Map saveEntity, Map user) {
        saveEntity.put(AUDIT_CREATED_BY, getUserId(user));
        saveEntity.put(AUDIT_CREATED_TIME, new Date());
    }

    protected void initUpdateInfo(Map saveEntity, Map user) {
        saveEntity.put(AUDIT_UPDATED_BY,getUserId(user));
        saveEntity.put(AUDIT_UPDATED_TIME, new Date());
    }

    protected String getUserId(Map user) {
        return String.valueOf(user.get(DB_FIELD__ID));
    }
}
