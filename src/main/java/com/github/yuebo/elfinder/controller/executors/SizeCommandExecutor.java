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
import com.github.yuebo.elfinder.service.FsService;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * This calculates the total size of all the supplied targets and returns the size in bytes.
 */
public class SizeCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
    @Override
    protected void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json) throws Exception
    {
        String[] targets = request.getParameterValues("targets[]");
        long size = 0;
        for (String target: targets)
        {
            FsItemEx item = findItem(fsService, target);
            size += item.getSize();
        }
        json.put("size", size);
    }
}
