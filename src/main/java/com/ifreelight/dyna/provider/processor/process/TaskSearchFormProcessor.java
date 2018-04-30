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
import com.ifreelight.dyna.utils.UserUtils;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.OrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 18/11/2017.
 */
@Component
public class TaskSearchFormProcessor extends DefaultSearchFormProcessor {
    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UserUtils userUtils;

    @Override
    public SearchResult processSearch(ViewContext viewContext, Map filter, OrderedMap sort, int limit, int skip) {
        SearchResult searchResult = new SearchResult();

        TaskQuery taskQuery = taskService.createTaskQuery();
        String name = (String) filter.get("name");
        if (name == null)
            name = "";

        List<Map<String, Object>> result = new ArrayList();
        Map user = userUtils.currentUser();
        if (user == null) {
            return searchResult;
        }
        long size = taskQuery.taskNameLikeIgnoreCase("%" + name + "%").taskCandidateOrAssigned((String) user.get("name")).count();
        List<Task> taskList = taskQuery.taskCandidateOrAssigned((String) user.get("name")).orderByTaskCreateTime().desc().listPage(skip, limit);
        for (Task task : taskList) {
            HashMap map = new HashMap();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            map.put("name", task.getName());
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processName", processDefinition.getName());
            map.put("assignee", task.getAssignee());
            map.put("dueDate", task.getDueDate());
            map.put(DB_FIELD__ID, task.getId());
            map.put("formKey", task.getFormKey());
            result.add(map);
        }

        searchResult.setResult(result);
        searchResult.setTotal(size);
        return searchResult;
    }
}
