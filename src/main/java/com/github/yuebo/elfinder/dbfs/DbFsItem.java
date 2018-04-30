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

package com.github.yuebo.elfinder.dbfs;

import com.github.yuebo.elfinder.service.FsItem;
import com.github.yuebo.elfinder.service.FsVolume;

import java.io.File;

/**
 * Created by yuebo on 2018/2/12.
 */
public class DbFsItem implements FsItem {
    private DbFsVolume dbFsVolume;
    private File file;
    public DbFsItem(DbFsVolume dbFsVolume, File file) {
        this.dbFsVolume=dbFsVolume;
        this.file=file;

    }

    @Override
    public FsVolume getVolume() {
        return dbFsVolume;
    }

    @Override
    public String getObjectId() {
        return file.getPath().replace("\\","/");
    }
    public String getFileName(){
        return this.file.getName();
    }

    public long size() {
        return this.file.length();
    }
}
