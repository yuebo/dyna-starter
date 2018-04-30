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

import com.ifreelight.dyna.core.NullNumber;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/4/14
 * Time: 9:52 AM
 */
@Component("numberConverter")
public class NumberConvertProvider extends DefaultConvertProvider {
    private Logger logger = Logger.getLogger(getClass());
    private String NUMBER_FORMAT="0.00";

    @Override
    protected Object convertItem(Object item, Map parameter) {
        assert item instanceof String;
        Number val = null;
        if (StringUtils.isEmpty((String) item)) {
            return new NullNumber();
        }

        try {
            //val=Float.parseFloat((String)item);
            String format = MapUtils.getString(parameter,"format");
            if (StringUtils.isEmpty(format))
                format = NUMBER_FORMAT;

            DecimalFormat decimalFormat=new DecimalFormat(format);
            val = decimalFormat.parse((String) item);
        } catch (Exception e) {
            logger.error("date convert error", e);
        }

        return val;
    }

    @Override
    protected Object restoreItem(Object item, Map parameter) {
        assert item instanceof Number;
        if (item == null||item instanceof NullNumber) {
            return null;
        }
        //String val=item.toString();
        String format = MapUtils.getString(parameter,"format");
        if (StringUtils.isEmpty(format))
            format = NUMBER_FORMAT;
        DecimalFormat decimalFormat=new DecimalFormat(format);
        String val = decimalFormat.format((Number) item);
        return val;
    }

}
