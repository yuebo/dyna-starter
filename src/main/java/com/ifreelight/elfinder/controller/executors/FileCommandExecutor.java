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

package com.ifreelight.elfinder.controller.executors;

import com.ifreelight.elfinder.controller.executor.AbstractCommandExecutor;
import com.ifreelight.elfinder.controller.executor.CommandExecutor;
import com.ifreelight.elfinder.controller.executor.FsItemEx;
import com.ifreelight.elfinder.service.FsService;
import com.ifreelight.elfinder.util.MimeTypesUtils;
import org.apache.commons.io.IOUtils;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileCommandExecutor extends AbstractCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, HttpServletResponse response,
                        ServletContext servletContext) throws Exception
	{
		String target = request.getParameter("target");
		boolean download = "1".equals(request.getParameter("download"));
		FsItemEx fsi = super.findItem(fsService, target);
		if(fsi.isFolder()){
			response.sendError(404);
			return;
		}
		if(fsi.isDbFs()){
			response.sendRedirect(fsi.getURL());
			return;
		}
		String mime = fsi.getMimeType();

		response.setCharacterEncoding("utf-8");
		response.setContentType(mime);
		//String fileUrl = getFileUrl(fileTarget);
		//String fileUrlRelative = getFileUrl(fileTarget);
		String fileName = fsi.getName();
		//fileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
		if (download || MimeTypesUtils.isUnknownType(mime))
		{
			response.setHeader("Content-Disposition",
				"attachments; " + getAttachementFileName(fileName, request.getHeader("USER-AGENT")));
			//response.setHeader("Content-Location", fileUrlRelative);
			response.setHeader("Content-Transfer-Encoding", "binary");
		}

		OutputStream out = response.getOutputStream();
		InputStream is = null;
//		response.setContentLength((int) fsi.getSize());
		try
		{
			// serve file
			is = fsi.openInputStream();
			IOUtils.copy(is, out);
			out.flush();
			out.close();
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private String getAttachementFileName(String fileName, String userAgent) throws UnsupportedEncodingException
	{
		if (userAgent != null)
		{
			userAgent = userAgent.toLowerCase();

			if (userAgent.indexOf("msie") != -1)
			{
				return "filename=\"" + URLEncoder.encode(fileName, "UTF8") + "\"";
			}

			// Opera Only accept filename*
			if (userAgent.indexOf("opera") != -1)
			{
				return "filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF8");
			}
			// Safari using ISO8859-1
			if (userAgent.indexOf("safari") != -1)
			{
				return "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
			}
			// Chrome
			if (userAgent.indexOf("applewebkit") != -1)
			{
				return "filename=\"" + MimeUtility.encodeText(fileName, "UTF8", "B") + "\"";
			}
			// FireFox
			if (userAgent.indexOf("mozilla") != -1)
			{
				return "filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF8");
			}
		}

		return "filename=\"" + URLEncoder.encode(fileName, "UTF8") + "\"";
	}
}
