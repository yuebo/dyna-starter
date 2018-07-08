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

import com.github.yuebo.dyna.config.QiniuApiConfig;
import com.github.yuebo.elfinder.localfs.LocalFsVolume;
import com.github.yuebo.elfinder.service.FsItem;
import com.github.yuebo.elfinder.util.MimeTypesUtils;
import com.github.yuebo.dyna.service.JDBCService;
import com.github.yuebo.dyna.service.QiniuService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

import static com.github.yuebo.dyna.AppConstants.AUDIT_CREATED_BY;
import static com.github.yuebo.dyna.AppConstants.AUDIT_CREATED_TIME;
import static com.github.yuebo.dyna.AppConstants.DB_FIELD__ID;

/**
 * Created by yuebo on 2018/2/12.
 */
@Component
@ConditionalOnProperty(prefix = "elfinder",value = "provider",havingValue = "db",matchIfMissing = false)
public class DbFsVolume extends LocalFsVolume {
    @Autowired
    JDBCService jdbcService;
    @Autowired
    QiniuService qiniuService;
    @Autowired
    QiniuApiConfig qiniuApiConfig;
    private String DATA_DBFS="tbl_dbfs";
    @Autowired
    HttpServletRequest request;

    protected String getUserId() {
        Map map = (Map) request.getSession().getAttribute("user");
        return map == null ? null : String.valueOf(map.get(DB_FIELD__ID));
    }
    @Override
    public void createFile(FsItem fsi) throws IOException {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("path",fsi.getObjectId());
        if(!exists(fsi)){
            param.put("type","file");
            param.put("name",filename(fsi));
            param.put("parent",asFile(getParent(fsi)).get(DB_FIELD__ID));
            param.put(AUDIT_CREATED_TIME,new Date());
            param.put(AUDIT_CREATED_BY,getUserId());
            param.put("size",0);
            jdbcService.save(DATA_DBFS,param);
//            File file=File.createTempFile("empty","txt");
//            qiniuService.upload(file,MapUtils.getString(param,"_id"));
//            file.delete();
        }

    }

    @Override
    public void createFolder(FsItem fsi) throws IOException {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("path",fsi.getObjectId());
        if(!exists(fsi)){
            param.put("type","folder");
            param.put("name",filename(fsi));
            param.put("parent",asFile(getParent(fsi)).get(DB_FIELD__ID));
            param.put(AUDIT_CREATED_TIME,new Date());
            param.put(AUDIT_CREATED_BY,getUserId());
            param.put("size",0);
            jdbcService.save(DATA_DBFS,param);
        }

    }

    @Override
    public void deleteFile(FsItem fsi) throws IOException {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("path",fsi.getObjectId());
        Map<String,Object> file=asFile(fsi);
        jdbcService.delete(DATA_DBFS,param);
        //deleteRemote
        qiniuService.delete(MapUtils.getString(file,DB_FIELD__ID));
    }

    @Override
    public void deleteFolder(FsItem fsi) throws IOException {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("path",fsi.getObjectId());
        List<Map<String,Object>> result=jdbcService.queryForList("select * from "+DATA_DBFS+" where path like concat(:path,'/%')",param,null,0,0 );

        if(result!=null){
            ArrayList<String> ids=new ArrayList();
            for(Map<String,Object> r:result){
                Map<String,Object> p=new HashMap<String,Object>();
                p.put("path", MapUtils.getString(r,"path"));
                if("file".equals(MapUtils.getString(r,"type"))){
                    ids.add(MapUtils.getString(r,DB_FIELD__ID));
                }
                jdbcService.delete(DATA_DBFS,p);
            }
            if(ids.size()>0){
                qiniuService.batchDelete(ids.toArray(new String[]{}));
            }
        }
        jdbcService.delete(DATA_DBFS,param);

    }

    @Override
    public boolean exists(FsItem newFile) {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("path",newFile.getObjectId());
        return jdbcService.find(DATA_DBFS,param)!=null;
    }

    @Override
    public FsItem fromPath(String relativePath) {

        return new DbFsItem(this,new File(relativePath));
    }

    @Override
    public String getDimensions(FsItem fsi) {
        return null;
    }

    @Override
    public long getLastModified(FsItem fsi) {
        Map<String,Object> fs=asFile(fsi);
        return fs==null?0:((Date)fs.get(AUDIT_CREATED_TIME)).getTime();
    }

    @Override
    public String getMimeType(FsItem fsi) {
        Map<String,Object> file = asFile(fsi);
        if ("folder".equals(MapUtils.getString(file,"type")))
            return "directory";

        String ext = FilenameUtils.getExtension(MapUtils.getString(file,"name"));
        if (ext != null && !ext.isEmpty())
        {
            String mimeType = MimeTypesUtils.getMimeType(ext);
            return mimeType == null ? MimeTypesUtils.UNKNOWN_MIME_TYPE : mimeType;
        }

        return MimeTypesUtils.UNKNOWN_MIME_TYPE;
    }

    @Override
    public String getName() {
        return "DBFS";
    }

    @Override
    public String getName(FsItem fsi) {
        return filename(fsi);
    }

    @Override
    public FsItem getParent(FsItem fsi) {
        String path=fsi.getObjectId();
        return new DbFsItem(this,new File(path).getParentFile());
    }

    @Override
    public String getPath(FsItem fsi) throws IOException {
        return fsi.getObjectId();
    }

    @Override
    public FsItem getRoot() {
        return new DbFsItem(this,new File("/"));
    }

    @Override
    public long getSize(FsItem fsi) throws IOException {
        Map<String,Object> file=asFile(fsi);
        return file==null?0:MapUtils.getLong(file,"size");
    }

    @Override
    public String getThumbnailFileName(FsItem fsi) {
        return null;
    }

    @Override
    public boolean hasChildFolder(FsItem fsi) {
        return false;
    }

    @Override
    public boolean isFolder(FsItem fsi) {
        return isRoot(fsi)||MapUtils.getString(asFile(fsi),"type").equals("folder");
    }

    @Override
    public boolean isRoot(FsItem fsi) {
        return "/".equals(fsi.getObjectId());
    }

    @Override
    public FsItem[] listChildren(FsItem fsi) {
        String objectId=fsi.getObjectId();
        BasicDBObject query=new BasicDBObject();
        if(objectId!=null){
            query.append("parent",asFile(fsi).get(DB_FIELD__ID));
        }else {
            query.append("parent","");
        }
        List<Map<String,Object>>files= jdbcService.findList(DATA_DBFS,query,null,0,0);
        if(files!=null){
            List<DbFsItem> items=new ArrayList();
            for(Map<String,Object> file:files){
                String path=MapUtils.getString(file,"path");
                items.add(new DbFsItem(this,new File(path)));
            }
            return items.toArray(new DbFsItem[]{});
        }else {
            return new DbFsItem[]{};
        }


    }

    @Override
    public InputStream openInputStream(FsItem fsi) throws IOException {
        if(isFolder(fsi)){
            return null;
        }
        java.net.URLConnection urlConnection= new URL(qiniuApiConfig.getBaseUrl()+MapUtils.getString(asFile(fsi),DB_FIELD__ID)+"?"+System.currentTimeMillis()).openConnection();
        urlConnection.setDefaultUseCaches(false);
        urlConnection.setUseCaches(false);
        return urlConnection.getInputStream();
    }

    @Override
    public OutputStream openOutputStream(FsItem fsi) throws IOException {
        File temp=File.createTempFile("dbfs","tmp");
        return new TempFileOutputStream(temp, new Callable() {
            @Override
            public Object call() throws Exception {
                Map<String,Object> file=asFile(fsi);
                String id=MapUtils.getString(file,DB_FIELD__ID);
                qiniuService.upload(temp,id);
                jdbcService.update(DATA_DBFS,new BasicDBObject(DB_FIELD__ID,id),new BasicDBObject("size",temp.length()));
                return 0;
            }
        });
    }

    @Override
    public void rename(FsItem src, FsItem dst) throws IOException {
        Map<String,Object>  srcFile=asFile(src);
        jdbcService.update(DATA_DBFS,new BasicDBObject(DB_FIELD__ID,srcFile.get(DB_FIELD__ID)),new BasicDBObject("path",dst.getObjectId()).append("name",filename(dst)));
        if(isFolder(dst)) {
            BasicDBObject query=new BasicDBObject();
            if(src.getObjectId()!=null){
                query.append("parent",srcFile.get(DB_FIELD__ID));
            }else {
                query.append("parent","");
            }
            //update parent
            List<Map<String,Object>>files= jdbcService.findList(DATA_DBFS,query,null,0,0);
            if(files!=null){
                for(Map<String,Object> file:files){
                    jdbcService.update(DATA_DBFS,new BasicDBObject(DB_FIELD__ID,file.get(DB_FIELD__ID)),new BasicDBObject("path", StringUtils.replaceOnce(MapUtils.getString(file,"path"),src.getObjectId(),dst.getObjectId())));
                }
            }
            //update child path
            List<Map<String,Object>> subFiles= jdbcService.queryForList("select * from "+DATA_DBFS +" where PATH like concat(:path,'/%')",new BasicDBObject("path",src.getObjectId()),null,0,0);
            if(subFiles!=null){
                for(Map<String,Object> file:subFiles){
                    jdbcService.update(DATA_DBFS,new BasicDBObject(DB_FIELD__ID,file.get(DB_FIELD__ID)),new BasicDBObject("path", StringUtils.replaceOnce(MapUtils.getString(file,"path"),src.getObjectId(),dst.getObjectId())));
                }
            }

        }

    }

    @Override
    public String getURL(FsItem f) {
        if(isFolder(f)){
           return null;
        }
        return qiniuApiConfig.getBaseUrl()+MapUtils.getString(asFile(f),DB_FIELD__ID)+"?view";
    }

    private DbFsItem fromFile(File file)
    {
        return new DbFsItem(this, file);
    }

    @Override
    public String toString() {
        return "DB FS located [" + qiniuApiConfig.getBaseUrl() + "]";

    }

    private Map<String,Object> asFile(FsItem fsi)
    {
        if(fsi instanceof DbFsItem){
            return jdbcService.find(DATA_DBFS,new BasicDBObject("path",fsi.getObjectId()));
        }
        return null;
    }

    private String filename(FsItem fsItem){
        if(fsItem instanceof DbFsItem){
            return ((DbFsItem) fsItem).getFileName();
        }
        return null;
    }
    private long size(FsItem fsItem){
        if(fsItem instanceof DbFsItem){
            return ((DbFsItem) fsItem).size();
        }
        return 0;
    }

}
