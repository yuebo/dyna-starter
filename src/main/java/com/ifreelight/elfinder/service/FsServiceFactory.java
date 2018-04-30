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

package com.ifreelight.elfinder.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * a factory which creates FsServices
 * 
 * @author bluejoe
 *
 */
public interface FsServiceFactory
{
	/**
	 * sometimes a FsService should be constructed dynamically according to current web request.
	 * e.g users may own separated file spaces in a net disk service platform,
	 * in this case, getFileService() get user principal from current request and offers him/her different file view.
	 * 
	 * @param request
	 * @param servletContext
	 * @return
	 */
	FsService getFileService(HttpServletRequest request, ServletContext servletContext);

}
