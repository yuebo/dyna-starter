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

import com.github.yuebo.dyna.DbConstant;
import com.github.yuebo.dyna.core.SearchResult;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.provider.processor.DefaultSearchFormProcessor;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.OrderedMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 17/11/2017.
 */
@Component
public class RoleSearchSearchFormProcessor extends DefaultSearchFormProcessor {
    @Override
    public SearchResult processSearch(ViewContext viewContext, Map filter, OrderedMap sort, int limit, int skip) {
        SearchResult result = new SearchResult();
//        System.out.println(filter);
        Map temp = new HashMap();
        temp.putAll(filter);
        List<Map<String, Object>> searchResult = jdbcService.queryForListWithFilter("select * from (select r.id,r.rolename,group_concat(c.name)as permission from "+DbConstant.TBL_ROLE+" r left join "+ DbConstant.TBL_ROLE_PERMISSION+" p on r.id=p.roleId left join "+ DbConstant.TBL_PERMISSION+" c on p.permission=c.id group by r.id) temp", temp, sort, limit, skip);

        int total = jdbcService.count(viewContext.getData(), new BasicDBObject(filter));
        result.setResult(searchResult);
        result.setTotal(total);
        return result;
    }
}
