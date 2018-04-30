package com.github.yuebo.dyna.provider.option;

import com.github.yuebo.dyna.core.ViewContext;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
@Component
public class MultiDataOptionProvider extends DefaultOptionProvider {
    @Override
    protected List<Map<String, Object>> findOptions(ViewContext viewContext, String table, Map query, ListOrderedMap sort, int i, int i1) {
        List<Map<String, Object>> result=new ArrayList();
        for (String subtable:table.split(",")){
            List<Map<String, Object>> temp=super.findOptions(viewContext,subtable,query,sort,i,i1);
            if(temp!=null)
                result.addAll(temp);
        }

        return result;
    }
}
