package com.github.yuebo.dyna.provider.option;

import com.github.yuebo.dyna.core.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/5.
 */
@Component
public class SpringBeanOptionProvider extends DefaultOptionProvider{
    @Override
    protected List<Map<String, Object>> findOptions(ViewContext viewContext, String table, Map query, ListOrderedMap sort, int i, int i1) {
        List<Map<String, Object>> result=new ArrayList();
        List<String> names=new ArrayList();
        if("formProvider".equals(MapUtils.getString(query,"class"))){
            String processor=MapUtils.getString(query,"processor");
            if("create".equals(processor)){
                names=springUtils.listBean(FormProcessor.class);
            }else if("search".equals(processor)) {
                names=springUtils.listBean(SearchFormProcessor.class);
            }else {
                return Collections.emptyList();
            }
            for(String name:names){
                Map map=new LinkedCaseInsensitiveMap();
                map.put("processor",name);
                map.put("processorName",springUtils.getBeanClassName(name));
                result.add(map);
            }

        }else {
            String providerClass=MapUtils.getString(query,"class");
            String provider=MapUtils.getString(query,"provider");
            Class c=null;
            if("option".equals(providerClass)){
                c= OptionProvider.class;
            }
            if("converter".equals(providerClass)){
                c= ConvertProvider.class;
            }
            if("validator".equals(providerClass)){
                c= ValidatorProvider.class;
            }
            if("operate".equals(providerClass)){
                c= OperateProvider.class;
            }
            if("file".equals(providerClass)){
                c= FileProcessor.class;
            }
            names=springUtils.listBean(c);
            for(String name:names){
                Map map=new LinkedCaseInsensitiveMap();
                map.put("provider",name);
                map.put("providerName",springUtils.getBeanClassName(name));
                result.add(map);
            }

        }


        return result;
    }
}
