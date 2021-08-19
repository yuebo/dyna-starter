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

package com.github.yuebo.dyna.provider;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: yuebo
 * Date: 12/4/14
 * Time: 3:49 PM
 */
@Component
public class MultipartStoreHandler {
    @Value("${upload.defaultPath:/tmp}")
    private String defaultPath;

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss_");

    public String store(MultipartFile file) throws Exception {
        if(!new File(defaultPath).exists()){
            new File(defaultPath).mkdirs();
        }
        File f = new File(defaultPath, simpleDateFormat.format(new Date()) + file.getOriginalFilename());
        FileOutputStream outputStream = new FileOutputStream(f);
        try {
            IOUtils.copy(file.getInputStream(), outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return f.getAbsolutePath();
    }

    public String getDefaultPath() {
        return defaultPath;
    }
}
