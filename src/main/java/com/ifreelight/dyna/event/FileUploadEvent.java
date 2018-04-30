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

package com.ifreelight.dyna.event;

import org.bson.types.ObjectId;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/8/14
 * Time: 2:57 PM
 */
public class FileUploadEvent extends ApplicationEvent {
    private Map<String, Object> user;
    private String id;
    private String upload;
    private String fileName;
    private String baseName;
    private Map<String, Object> parameter = new HashMap<String, Object>();

    public FileUploadEvent(Object source, String upload, String baseName, String fileName, Map<String, Object> user) {
        super(source);
        this.upload = upload;
        this.fileName = fileName;
        this.baseName = baseName;
        this.user = user;
        this.id = new ObjectId().toHexString();
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }
}
