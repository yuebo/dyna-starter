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

package com.github.yuebo.elfinder.controller.executors;

import com.github.yuebo.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.github.yuebo.elfinder.controller.executor.CommandExecutor;
import com.github.yuebo.elfinder.controller.executor.FsItemEx;
import com.github.yuebo.elfinder.service.FsItemFilter;
import com.github.yuebo.elfinder.service.FsService;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadCommandExecutor extends AbstractJsonCommandExecutor
		implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		assert request instanceof MultipartHttpServletRequest;
		MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest) request;
		List<MultipartFile> listFiles = multipartHttpServletRequest.getFiles("upload[]");
		List<FsItemEx> added = new ArrayList<FsItemEx>();

		String target = request.getParameter("target");
		FsItemEx dir = super.findItem(fsService, target);

		FsItemFilter filter = getRequestedFilter(request);
		for (MultipartFile fis : listFiles)
		{
			String fileName = fis.getOriginalFilename();
			FsItemEx newFile = new FsItemEx(dir, fileName);
			newFile.createFile();
			InputStream is = fis.getInputStream();
			OutputStream os = newFile.openOutputStream();

			IOUtils.copy(is, os);
			os.close();
			is.close();

			if (filter.accepts(newFile))
				added.add(newFile);
		}

		json.put("added", files2JsonArray(request, added));
	}
}
