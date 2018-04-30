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

package com.github.yuebo.dyna.provider.processor.process;

import com.github.yuebo.dyna.core.SearchResult;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.utils.UserUtils;
import com.github.yuebo.dyna.provider.processor.DefaultSearchFormProcessor;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
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
public class TaskSearchCompleteFormProcessor extends DefaultSearchFormProcessor {
    @Autowired
    HistoryService historyService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UserUtils userUtils;

    @Override
    public SearchResult processSearch(ViewContext viewContext, Map filter, OrderedMap sort, int limit, int skip) {
        SearchResult searchResult = new SearchResult();

        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery();
        String name = (String) filter.get("name");
        if (name == null)
            name = "";

        List<Map<String, Object>> result = new ArrayList();
        Map user = userUtils.currentUser();
        if (user == null) {
            return searchResult;
        }
        List<String> roles = userUtils.currentRoles();
        String username = (String) user.get("name");
        long size = taskQuery.taskNameLikeIgnoreCase("%" + name + "%").finished().taskDeleteReason("completed").taskCandidateGroupIn(roles).taskCandidateUser(username).count();
        List<HistoricTaskInstance> taskList = taskQuery.orderByTaskCreateTime().desc().listPage(skip, limit);
        for (HistoricTaskInstance task : taskList) {
            HashMap map = new HashMap();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            Map review = findReviewRecords(task.getId());
            if (review != null) {
                map.put("approve", review.get("approve"));
                map.put("remarks", review.get("remarks"));
            }
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("name", task.getName());
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


    private Map<String, Object> findReviewRecords(String taskId) {
        HashMap map = new HashMap();
        map.put(PARAMETER_FIELD__TASKID, taskId);
        return jdbcService.find(TBL_TASK_LOG, map);
    }
}
