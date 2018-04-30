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

package com.github.yuebo.dyna.provider.processor.sys;

import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.provider.processor.DefaultFormProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by yuebo on 28/11/2017.
 */
@Component
public class UploadFileFormProcessor extends DefaultFormProcessor {
    @Override
    public String process(ViewContext viewContext, Map saveEntity) {
        messageUtils.addMessage(viewContext.getMessagesContext(), "upload", viewContext);
        formUtils.copyValuesToViewMap(viewContext, saveEntity);
        if (viewContext.getRedirect() == null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("redirect:").append(viewContext.getName());
            return buffer.toString();
        } else {
            return "redirect:" + String.valueOf(viewContext.getRedirect());
        }
    }
}
