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

package com.github.yuebo.dyna.provider.converter;

import com.github.yuebo.dyna.core.NullDate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/4/14
 * Time: 9:52 AM
 */
@Component("dateConverter")
public class DateConvertProvider extends DefaultConvertProvider {
    private Logger logger = Logger.getLogger(getClass());
    private final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected Object convertItem(Object item, Map parameter) {
        assert item instanceof String;
        Date val = null;
        if (StringUtils.isEmpty((String) item)) {
            return new NullDate();
        }

        try {
            String format = (String) parameter.get("format");
            if (StringUtils.isEmpty(format))
                format = DATE_FORMAT;
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setLenient(false);
            val = dateFormat.parse((String) item);
        } catch (ParseException e) {
            logger.error("date convert error", e);
        }

        return val;
    }

    @Override
    protected Object restoreItem(Object item, Map parameter) {
        assert item instanceof Date;
        if (item instanceof NullDate) {
            return "";
        }
        String format = (String) parameter.get("format");
        if (StringUtils.isEmpty(format))
            format = DATE_FORMAT;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        String val = dateFormat.format((Date) item);
        return val;
    }
}
