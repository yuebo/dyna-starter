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

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.security.PermissionCheck;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by yuebo on 28/11/2017.
 */
@Controller
@RequestMapping("/spring/data")
@PermissionCheck(name = "elfinderView")
public class UploadDownloadController implements AppConstants {
    private static Logger logger= LoggerFactory.getLogger(UploadDownloadController.class);
    @Autowired
    private JDBCService jdbcService;

    @RequestMapping(value = "/download/{viewname}", method = {RequestMethod.GET})
    public void download(HttpServletRequest request, @PathVariable("viewname") String viewname, Model model, HttpServletResponse response) throws IOException {
        logger.info("start to download {}",viewname);
        String id = request.getParameter("_id");

        Map result = jdbcService.find(TBL_JOB, new BasicDBObject(DB_FIELD__ID, id));
        if (request == null) {
            response.sendError(404);
            return;
        }
        String fileName = (String) result.get("fileName");
        File fileToDownload = new File(fileName);
        if (!fileToDownload.exists()) {
            response.sendError(404);
            return;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileToDownload.getName());
        try (FileInputStream fileInputStream = new FileInputStream(fileToDownload)) {
            IOUtils.copy(fileInputStream, response.getOutputStream());
        }


    }

}
