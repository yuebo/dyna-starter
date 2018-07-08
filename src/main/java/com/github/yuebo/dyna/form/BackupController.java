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
package com.github.yuebo.dyna.form;

import com.github.yuebo.dyna.security.PermissionCheck;
import com.github.yuebo.dyna.service.JDBCService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 2018/7/8
 * Time: 21:40
 */
@Controller
@RequestMapping("/spring/data")
@PermissionCheck(name = "systemBackupView")
public class BackupController {
    @Autowired
    private JDBCService jdbcService;
    @GetMapping("/backup/systemBackupView")
    public String backup(HttpServletResponse res) throws IOException {
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=backup.zip");

        List<Map<String,Object>> viewList=jdbcService.findList("tbl_view_deployment",new HashMap(),null,0,0);
        try(ZipArchiveOutputStream zipArchiveOutputStream=new ZipArchiveOutputStream(res.getOutputStream())){

            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);

            if(!viewList.isEmpty()) {
                for (Map<String, Object> view : viewList) {
                    zipArchiveOutputStream.putArchiveEntry(new ZipArchiveEntry(MapUtils.getString(view, "type", "null")
                            .concat("/").concat(MapUtils.getString(view, "name").concat(".json"))));

                    zipArchiveOutputStream.write(MapUtils.getString(view, "data").getBytes());
                    zipArchiveOutputStream.closeArchiveEntry();
                }
            }
        }
        return null;
    }
}
