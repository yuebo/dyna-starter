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

package com.ifreelight.dyna.provider.processor.process;

import com.ifreelight.dyna.core.SearchResult;
import com.ifreelight.dyna.core.ViewContext;
import com.ifreelight.dyna.provider.processor.DefaultSearchFormProcessor;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.collections.OrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 17/11/2017.
 */
@Component
public class ProcessSearchFormProcessor extends DefaultSearchFormProcessor {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    FormService formService;

    @Override
    public SearchResult processSearch(ViewContext viewContext, Map filter, OrderedMap sort, int limit, int skip) {

        SearchResult searchResult = new SearchResult();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        String name = (String) filter.get("name");
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionNameLike("%" + (name == null ? "" : name) + "%").latestVersion().listPage(skip, limit);
        long count = processDefinitionQuery.count();
        List<Map<String, Object>> result = new ArrayList();
        for (ProcessDefinition processDefinition : definitionList) {
            HashMap map = new HashMap();
            map.put("name", processDefinition.getName());
            map.put("category", processDefinition.getCategory());
            map.put("version", processDefinition.getVersion());
            map.put(DB_FIELD__ID, processDefinition.getId());
            String key = formService.getStartFormKey(processDefinition.getId());
            map.put("start", key);

            result.add(map);
        }
        searchResult.setResult(result);
        searchResult.setTotal(count);
        return searchResult;
    }
}
