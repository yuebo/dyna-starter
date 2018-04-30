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

package com.ifreelight.dyna.provider.converter;

import com.ifreelight.dyna.core.ConvertContext;
import com.ifreelight.dyna.core.ConvertProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/4/14
 * Time: 9:32 AM
 */
@Component
public class DefaultConvertProvider implements ConvertProvider {
    @Override
    public Object convert(Object v, ConvertContext convertContext) {
        if (v != null && v instanceof List) {
            ArrayList result = new ArrayList();
            for (Object item : (List) v) {
                result.add(convertItem(item, convertContext.getParameter()));
            }
            return result;
        }
        return convertItem(v, convertContext.getParameter());
    }


    protected Object convertItem(Object item, Map parameter) {
        return item;
    }

    protected Object restoreItem(Object item, Map parameter) {
        return item;
    }

    @Override
    public Object restore(Object o, ConvertContext convertContext) {
        if (o == null) {
            return null;
        }
        if (o instanceof List) {
            ArrayList result = new ArrayList();
            for (Object item : (List) o) {
                result.add(restoreItem(item, convertContext.getParameter()));
            }
            return result;
        }
        return restoreItem(o, convertContext.getParameter());
    }
}
