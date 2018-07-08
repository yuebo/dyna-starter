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

import com.github.yuebo.dyna.core.ProcessInformation;
import com.github.yuebo.dyna.core.ViewContext;
import com.github.yuebo.dyna.provider.processor.DefaultFormProcessor;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuebo on 17/11/2017.
 */
@Component
public class TaskInitFormProcessor extends DefaultFormProcessor {
    @Autowired
    FormService formService;
    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;
    @Autowired
    RuntimeService runtimeService;

    @Override
    public Map<String, Object> load(ViewContext viewContext, Map condition) {
        String taskId = (String) condition.get(DB_FIELD__ID);
        ProcessInformation processInformation = new ProcessInformation();
        processInformation.setTaskId(taskId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String InstanceId = "";
        String formKey = "";
        if (task == null) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            if (historicTaskInstance != null) {
                InstanceId = historicTaskInstance.getProcessInstanceId();
                processInformation.setCompleted(true);
                processInformation.setTaskHistoryId(historicTaskInstance.getId());
                processInformation.setProcessId(historicTaskInstance.getProcessDefinitionId());
                processInformation.setProcessInstanceId(InstanceId);
                processInformation.setStarted(true);
                formKey = historicTaskInstance.getFormKey();
            }

        } else {
            InstanceId = task.getProcessInstanceId();
            processInformation.setCompleted(false);
            processInformation.setTaskId(taskId);
            processInformation.setProcessInstanceId(InstanceId);
            processInformation.setProcessId(task.getProcessDefinitionId());
            processInformation.setStarted(true);
            formKey = task.getFormKey();
        }
        if (processInformation.getProcessInstanceId() != null) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInformation.getProcessInstanceId()).singleResult();
            if (processInstance == null) {
                HistoricProcessInstance historicTaskInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInformation.getProcessInstanceId()).singleResult();
                processInformation.setBusinessId(historicTaskInstance.getBusinessKey());
            } else {
                processInformation.setBusinessId(processInstance.getBusinessKey());
            }
        }

        HashMap map = new HashMap();
        viewContext.getViewMap().put("view", "redirect:" + formKey + "?_taskId=" + processInformation.getTaskId() + "&id=" + processInformation.getBusinessId());
        return map;
    }
}
