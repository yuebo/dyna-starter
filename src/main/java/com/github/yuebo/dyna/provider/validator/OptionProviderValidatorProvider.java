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

package com.github.yuebo.dyna.provider.validator;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.FileValidatorProvider;
import com.github.yuebo.dyna.core.OptionProvider;
import com.github.yuebo.dyna.core.ValidateContext;
import com.github.yuebo.dyna.utils.FormViewUtils;
import com.github.yuebo.dyna.utils.SpringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/11.
 */
@Component("option")
public class OptionProviderValidatorProvider implements FileValidatorProvider,AppConstants {
    @Autowired
    FormViewUtils formViewUtils;
    @Autowired
    SpringUtils springUtils;
    @Override
    public boolean validate(ValidateContext validateContext, Map fieldValueMap) {

        Object[] param=getParameterValues(validateContext.getField(),fieldValueMap);


        Map<String,Object> field=formViewUtils.getField(validateContext.getViewContext(),validateContext.getField());

        if(isOptionValue(MapUtils.getString(field,VIEW_FIELD_FIELDS_TYPE))){

            Map<String,Object> option=MapUtils.getMap(field,VIEW_FIELD_FIELDS_OPTION);

            if(MapUtils.getString(option,"provider")!=null){
                OptionProvider provider=springUtils.getOptionProvider(option);

                List<Map<String,String>> options=provider.option(validateContext.getViewContext(),field,MapUtils.getMap(option,"parameter", Collections.emptyMap()));
                List<String> values=new ArrayList();
                for(Map<String,String> o:options){
                    values.addAll(o.keySet());
                }

                for (Object p:param){
                    //can allow empty in case the field is not required
                    if(StringUtils.isNotEmpty((String)p)&&!values.contains(p)){
                        return false;
                    }
                }

            }
            else {
                Map<String,String> value=MapUtils.getMap(option,"values");
                if(value!=null){
                    List<String> values=new ArrayList();
                    values.addAll(value.keySet());
                    for (Object p:param){
                        //can allow empty in case the field is not required
                        if(StringUtils.isNotEmpty((String)p)&&!values.contains(p)){
                            return false;
                        }
                    }
                }else {
                    return false;
                }



            }




        }

        return true;
    }
}
