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

package com.github.yuebo.dyna.service;

import com.github.yuebo.dyna.config.QiniuApiConfig;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by yuebo on 2018/2/12.
 */
@Service
public class QiniuService {
    private Logger logger= LoggerFactory.getLogger(QiniuService.class);
    @Autowired
    QiniuApiConfig config;

    public Response upload(File tempFile,String path) throws QiniuException {
        StringMap putPolicy=new StringMap();
//        putPolicy.put("scope",config.getStorage()+":"+path);
        putPolicy.put("insertOnly",0);
//        if(type.equals("voice")){
//            putPolicy.put("scope",storage+":"+mediaId);
//            putPolicy.put("deadline",1390528576);
//            putPolicy.put("persistentNotifyUrl",String.format("%s/qiniu/notify",host));
//            putPolicy.put("persistentOps","avthumb/mp3/ab/192k|saveas/"+ UrlSafeBase64.encodeToString(storage + ":" + mediaId + ".mp3"));
//        }
        String token = auth().uploadToken(config.getStorage(),path,3600,putPolicy);
        Response r = uploadManager().put(tempFile, path, token);
        logger.info(r.getInfo());
        return r;
    }

    public Response delete(String path) throws QiniuException {
        return bucketManager().delete(config.getStorage(),path);
    }

    public Response batchDelete(String ... path) throws QiniuException {
        BucketManager.BatchOperations  batchOperations=new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(config.getStorage(),path);
        return bucketManager().batch(batchOperations);
    }

    public Auth auth(){
        return Auth.create(config.getKey(),config.getSecret());
    }
    public UploadManager uploadManager(){
        return new UploadManager(new com.qiniu.storage.Configuration(Zone.zone0()));
    }
    public BucketManager bucketManager(){
        return new BucketManager(auth(),new com.qiniu.storage.Configuration(Zone.zone0()));
    }
}
