/*
 *
 *  *
 *  *  * Copyright 2002-2017 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 *
 */

package com.github.yuebo.dyna.provider.chart;

import com.github.yuebo.dyna.utils.UserUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/18.
 */
@Component
public class UserTaskChartDatasourceProvider extends DefaultChartDatasourceProvider {

    @Autowired
    TaskService taskService;
    @Autowired
    HistoryService historyService;
    @Autowired
    UserUtils userUtils;


    @Override
    protected List<Map<String, Object>> getDataList(String data) {

        HistoricTaskInstanceQuery historicTaskInstanceQuery=historyService.createHistoricTaskInstanceQuery();
        List<String> roles = userUtils.currentRoles();
        String username = (String) userUtils.currentUser().get("name");
        long finished = historicTaskInstanceQuery.finished().taskDeleteReason("completed").taskCandidateGroupIn(roles).taskCandidateUser(username).count();

        TaskQuery taskQuery=taskService.createTaskQuery();

        long active=taskQuery.taskCandidateGroupIn(roles).taskCandidateUser(username).count();

        List<Map<String,Object>> result=new ArrayList();

        Map<String,Object> item=new HashMap();
        item.put("user",username);
        item.put("status","未完成");
        item.put("cnt",active);
        result.add(item);
        Map<String,Object> item2=new HashMap();
        item2.put("user",username);
        item2.put("status","完成");
        item2.put("cnt",finished);
        result.add(item2);
        return result;
    }
}
