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

package com.github.yuebo.dyna.form;

import com.github.yuebo.dyna.core.ValidatorProvider;
import com.github.yuebo.dyna.event.FileUploadEvent;
import com.github.yuebo.dyna.utils.*;
import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.core.*;
import com.github.yuebo.dyna.security.PermissionCheck;
import com.github.yuebo.dyna.service.JDBCService;
import com.mongodb.BasicDBObject;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by yuebo on 5/18/2015.
 */
@Controller
@RequestMapping("/spring/data")
@PermissionCheck
public class FormViewController implements AppConstants {
    private static Logger logger = LoggerFactory.getLogger(FormViewController.class);
    @Autowired
    private JDBCService jdbcService;
    @Autowired
    private FormViewUtils formViewUtils;
    @Autowired
    private SpringUtils springUtils;

    @Autowired
    private FormUtils formUtils;

    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    HistoryService historyService;
    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    IdentityService identityService;

    @Autowired
    PermissionProvider permissionProvider;

    @Autowired
    private MessageUtils messageUtils;

    @RequestMapping(value = "/create/{viewname}", method = {RequestMethod.GET})
    public String show(HttpServletRequest request, HttpServletResponse response, @PathVariable("viewname") String viewname, Model model) throws IOException {
        logger.debug("start show create view:{}", viewname);
        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);

        if (!VIEW_TYPE_CREATE.equals(viewContext.getType())) {
            return VIEW_OUTPUT_ERROR;
        }

        String taskId = request.getParameter(PARAMETER_FIELD__TASKID);
        String _id = request.getParameter(DB_FIELD__ID);
        Map<String, Object> processMap = viewContext.getProcess();
        ProcessInformation processInformation = new ProcessInformation();
        //try to get the process
        if (processMap != null) {
            processInformation = getProcessInformation(taskId);
            if (StringUtils.isNotEmpty(taskId) && StringUtils.isEmpty(_id) && processInformation.isStarted()) {
                _id = processInformation.getBusinessId();
            }
        }

        if (StringUtils.isNotEmpty(taskId)) {
            viewContext.setTaskId(taskId);
        }


        FormProcessor formProcessor = springUtils.getFormProcessor(viewContext);


        Map<String, Object> inputParameter = viewContext.getInput();

        String path = formProcessor.preCheck(viewContext);

        //validate
        if (path != null) {
            model.addAttribute(MODEL_ATTRIBUTE_VIEW, viewContext.getViewMap());
            model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
            model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
            logger.debug("end show create view:{}, path: {}", viewname, path);
            return path;
        }
        if (!viewContext.isViewValid()) {
            logger.debug("end show create view:{} with error", viewname);
            return VIEW_OUTPUT_ERROR;
        }
        if (processMap != null && StringUtils.isEmpty(taskId) && StringUtils.isNotEmpty(_id)) {
            logger.debug("end show create view:{} with error", viewname);
            return VIEW_OUTPUT_ERROR;
        }


        if (_id != null) {
            HashMap<String, Object> valueParam = new HashMap();
            valueParam.put(DB_FIELD__ID, _id);
            valueParam.put(DB_FIELD__DATA, viewContext.getData());
            Map map2 = formProcessor.load(viewContext, valueParam);

            if (map2 != null) {
                //add for check createdBy
                if(viewContext.isSelfOnly()&&(viewContext.getDataPermission()==null||!permissionProvider.hasPermission(viewContext.getDataPermission()))){
                    if(!MapUtils.getString(map2,AUDIT_CREATED_BY).equals(getUserId(request))){
                        logger.debug("invalid data access");
                        return VIEW_OUTPUT_ERROR;
                    }
                }
                if (viewContext.getViewMap().get(VIEW_FIELD_VIEW) != null) {
                    model.addAttribute(MODEL_ATTRIBUTE_VIEW, viewContext.getViewMap());
                    model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
                    model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
                    return (String) viewContext.getViewMap().get(VIEW_FIELD_VIEW);
                }
            }


            if (map2 != null) {
                formUtils.copyValuesToViewMap(viewContext, map2);
            }

        } else {
            HashMap<String, Object> map2 = new HashMap();

            if (inputParameter != null) {
                map2.putAll(inputParameter);
            }
            formUtils.copyValuesToViewMap(viewContext, map2);
        }

        //disable the form if the task is completed
        if (processInformation.isCompleted()) {
            viewContext.setReadOnly(true);
            List<Map<String, Object>> fields = (List) map.get(VIEW_FIELD_FIELDS);
            for (Map<String, Object> field : fields) {
                ConvertUtils.setProperty(field, "attributes.disabled", "disabled");
            }
        }


        model.addAttribute(MODEL_ATTRIBUTE_VIEW, viewContext.getViewMap());
        model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        String view = (String) map.get(VIEW_FIELD_VIEW);

        logger.debug("end show create view:{}", viewname);

        if (view == null)
            return VIEW_OUTPUT_SHOW;
        else
            return view;
    }

    @RequestMapping(value = "/create/{viewname}", method = {RequestMethod.POST})
    @Transactional
    public String create(HttpServletResponse response, @PathVariable("viewname") String viewname, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws IOException {
        logger.debug("start create view:{}", viewname);

        //Get the Id and task Id
        String taskId = request.getParameter(PARAMETER_FIELD__TASKID);
        String id = request.getParameter(DB_FIELD__ID);
        ProcessInformation processInformation = new ProcessInformation();
        //get View configuration
        Map<String, Object> map = formViewUtils.getFormView(viewname);

        ViewContext viewContext = new ViewContext(map);

        if (!VIEW_TYPE_CREATE.equals(viewContext.getType())) {
            return VIEW_OUTPUT_ERROR;
        }
        //load precess information
        Map<String, Object> processMap = viewContext.getProcess();
        if (processMap != null) {
            processInformation = getProcessInformation(taskId);
            if (StringUtils.isNotEmpty(taskId) && StringUtils.isEmpty(id) && processInformation.isStarted()) {
                id = processInformation.getBusinessId();
            }
            logger.debug("process instance loaded:{}", processInformation.getProcessInstanceId());

        }
        //set id for the map
        viewContext.setId(id);

        //set the task Id in view
        if (StringUtils.isNotEmpty(taskId)) {
            viewContext.setTaskId(taskId);
        }

        List<Map<String, Object>> list = viewContext.getFields();

        //populate the save viewContext, only the field in the view can save to the db
        Map saveEntity = (Map) map.get(VIEW_FIELD_DEFAULT_VALUE);
        if (saveEntity == null) {
            saveEntity = new HashMap();
        } else {
            saveEntity = parseExpress(saveEntity, new HashMap());
        }

        saveEntity.put(DB_FIELD__DATA, map.get(DB_FIELD_DATA));

        FormProcessor formProcessor = springUtils.getFormProcessor(viewContext);
        //perform validation
        boolean isValid = this.doValidation(viewContext, processInformation, saveEntity, request);
        if (!isValid) {
            //fix for error
            for (Map<String, Object> v : list) {

                UIComponent uiComponent=formViewUtils.getComponentByType(MapUtils.getString(v,VIEW_FIELD_FIELDS_TYPE));
                if (!uiComponent.isValidationRequired()) {
                    continue;
                }
                String name = String.valueOf(v.get(VIEW_FIELD_FIELDS_NAME));
                String type = String.valueOf(v.get(VIEW_FIELD_FIELDS_TYPE));
                Map converter = (Map) v.get(VIEW_FIELD_FIELDS_CONVERTER);
                //if the name start with $, then update the ref entity
                if (!name.startsWith($)) {

                    formUtils.populateForRestore(name, type, converter, request, saveEntity, false);
                } else {
                    formUtils.populateForRestore(name, type, converter, request, saveEntity, false);
                }
            }
            formUtils.copyValuesToViewMap(viewContext, saveEntity);
            if (id != null) {
                viewContext.setId(id);
            }
            model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
            model.addAttribute(MODEL_ATTRIBUTE_MESSAGES, viewContext.getMessagesContext());

            model.addAttribute(MODEL_ATTRIBUTE_VIEW, map);
            model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
            formProcessor.reload(viewContext, saveEntity);
            return VIEW_OUTPUT_SHOW;
        }


        List<FileUploadEvent> eventList = formUtils.getEventList(request);
        logger.debug("publish upload event: ", eventList.size());
        for (FileUploadEvent event : eventList) {
            event.setParameter(saveEntity);
            springUtils.publishEvent(event);
        }
        //remove the update=false fields in saveEntity Map
        if (StringUtils.isNotEmpty(id)) {
            for (Map<String, Object> field : list) {
                if (Boolean.FALSE.equals(field.get(VIEW_FIELD_FIELDS_UPDATE))) {
                    ConvertUtils.removeProperty(saveEntity, (String) field.get(VIEW_FIELD_FIELDS_NAME));
                }
            }
        }
        logger.debug("call form processor: {}", formProcessor.getClass().getName());

        String returnPath = formProcessor.process(viewContext, saveEntity);
        //check process, if there is process configuration, then init or complete the process
        if (processMap != null) {
            this.doProcess(viewContext, saveEntity, request);
        }

        returnPath = processPopup(returnPath, request);

        logger.debug("end create view {} with path {}", viewname, returnPath);

        redirectAttributes.addFlashAttribute(MODEL_ATTRIBUTE_MESSAGES, viewContext.getMessagesContext());
        model.addAttribute(MODEL_ATTRIBUTE_MESSAGES, viewContext.getMessagesContext());
        model.addAttribute(MODEL_ATTRIBUTE_VIEW, map);
        model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        return returnPath;
    }

    @RequestMapping(value = "/search/{viewname}", method = {RequestMethod.GET})
    public String search(HttpServletRequest request, @PathVariable("viewname") String viewname, Model model) {
        logger.debug("start search view {}", viewname);
        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);

        if (!VIEW_TYPE_SEARCH.equals(viewContext.getType()) || viewContext.getInputParameter() != null) {
            logger.debug("end search view {} with error", viewname);
            return VIEW_OUTPUT_ERROR;
        }
        String _back = request.getParameter(PARAMETER_FIELD__BACK);
        Map search = (Map) request.getSession().getAttribute(SESSION_KEY_SEARCH_PARAM);
        //if the is back action, then restore the search result and parameter from session
        if (StringUtils.isNotEmpty(_back)) {

            if (search == null) {

            } else {
                Map params = (Map) search.get(viewname);

                //parameter viewContext
                if (params != null) {
                    copyParamMapToViewMap(map, params);
                    model.addAttribute(MODEL_ATTRIBUTE_PARAMS, params);
                }
            }

        }
        if (search != null) {
            search.remove(viewname);
        }

        ArrayList resultList = springUtils.getActualResultList(viewContext);


        logger.debug("end search view {}", viewname);
        map.put(VIEW_FIELD_RESULT, resultList);
        model.addAttribute(MODEL_ATTRIBUTE_VIEW, map);
        model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);

        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        return VIEW_OUTPUT_SHOW;
    }


    @RequestMapping(value = "/data/{viewname}", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    Map<String, Object> search(HttpServletResponse response, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "order", required = false) String order, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer skip, @PathVariable("viewname") String viewname, HttpServletRequest request, Model model) throws IOException {
        logger.debug("start do search {}", viewname);

        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);
        if (!VIEW_TYPE_SEARCH.equals(viewContext.getType())) {
            logger.debug("end do search {} with error", viewname);
            response.sendError(404);
            return null;
        }

        Map<String, Object> searchCondition = (Map<String, Object>) map.get(VIEW_FIELD_DEFAULT_VALUE);
        searchCondition = prepareSearchCondition(searchCondition);
        Map<String, Object> inputParameter = (Map) map.get(VIEW_FIELD_INPUT);
        if (inputParameter != null) {
            searchCondition.putAll(inputParameter);
        }
        //add for check createdBy
        if(viewContext.isSelfOnly()&&(viewContext.getDataPermission()==null||!permissionProvider.hasPermission(viewContext.getDataPermission()))){
            searchCondition.put(AUDIT_CREATED_BY,getUserId(request));
        }

        boolean isValid = this.doValidation(viewContext, null, searchCondition, request);

        HashMap<String, Object> result = new HashMap();
        if (!isValid) {
            result.put(SEARCH_RESULT_VALIDATE, false);
            result.put(SEARCH_RESULT_ERROR, viewContext.getMessagesContext().get(SEARCH_RESULT_ERROR));
        } else {
            ListOrderedMap sortOrder = new ListOrderedMap();
            if (sort != null) {
                sortOrder.put(sort, "asc".equals(order) ? "1" : "-1");
            } else {
//                sortOrder.putAll(viewContext.getDefaultSort());
            }
            SearchFormProcessor searchFormProcessor = springUtils.getSearchFormProcessor(map);
            SearchResult searchResult = searchFormProcessor.processSearch(viewContext, new BasicDBObject(searchCondition), sortOrder, limit, skip);

            //handle result iterator
            processSearchResult(searchResult, viewContext);

            //set the sessoin

            Map search = (Map) request.getSession().getAttribute(SESSION_KEY_SEARCH_PARAM);
            if (search == null) {
                search = new HashMap();
                request.getSession().setAttribute(SESSION_KEY_SEARCH_PARAM, search);
            }
            HashMap values = new HashMap();
            values.putAll(request.getParameterMap());
            search.put(viewname, values);


            List<Map> resultList2 = springUtils.getResultList(viewContext, searchResult.getResult());

            logger.debug("end do search {}", viewname);


            result.put(SEARCH_RESULT_VALIDATE, true);
            result.put(SEARCH_RESULT_CLEAR, !Boolean.FALSE.equals(map.get(VIEW_FIELD_CLEAR)));
            result.put(SEARCH_RESULT_ROWS, resultList2);
            result.put(SEARCH_RESULT_TOTAL, searchResult.getTotal());

        }
        return result;
    }

    protected void processSearchResult(SearchResult searchResult, ViewContext viewContext) {
        ArrayList<Map<String, Object>> actualResultList = springUtils.getActualResultList(viewContext);
        ArrayList<Map<String, Object>> temp = new ArrayList();
        if (searchResult.getResult() != null) {
            for (Map<String, Object> v : searchResult.getResult()) {
                //cache will case error if we share the object
                Map<String, Object> values = new LinkedCaseInsensitiveMap();
                values.putAll(v);

                Object id = v.get(DB_FIELD__ID);
                if (id != null && id instanceof ObjectId)
                    v.put(DB_FIELD__ID, ((ObjectId) id).toHexString());
                else
                    v.put(DB_FIELD__ID, id == null ? null : id.toString());
                if(viewContext.getResult()!=null){
                    for (Map<String, Object> f : viewContext.getResult()) {
                        Map converter = (Map) f.get(VIEW_FIELD_RESULT_CONVERTER);
                        ConvertProvider provider = springUtils.getConvertProvider(converter);
                        String name = String.valueOf(f.get(VIEW_FIELD_RESULT_NAME));
                        if (!name.startsWith($)) {
                            ConvertUtils.setProperty(values, name, provider.restore(ConvertUtils.getProperty(v, name), new ConvertContext(converter)));
                        }
                    }
                    temp.add(values);
                }

            }
        } else {
            searchResult.setResult(Collections.emptyList());
        }

        viewContext.setResult(actualResultList);
        searchResult.setResult(temp);
    }


    @RequestMapping(value = "/template/{viewname}", method = {RequestMethod.GET})
    public String template(HttpServletResponse response, @PathVariable("viewname") String viewname, HttpServletRequest request, Model model) {
        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);
        if (!VIEW_TYPE_SEARCH.equals(viewContext.getType())) {
            return VIEW_OUTPUT_ERROR;
        }
        map.put(VIEW_FIELD_RESULT, springUtils.getActualResultList(viewContext));
        model.addAttribute(MODEL_ATTRIBUTE_VIEW, map);
        model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        return VIEW_OUTPUT_TABLE;
    }


    @RequestMapping(value = "/task/{viewname}", method = {RequestMethod.GET})
    @PermissionCheck(name = "chartView")
    public String processTaskChart(HttpServletResponse response, @PathVariable("viewname") String viewname, HttpServletRequest request, Model model) {
        String taskId = request.getParameter(DB_FIELD__ID);

        ProcessInformation processInformation = new ProcessInformation();
        processInformation = getProcessInformation(taskId);


        List<String> activitiIds = new ArrayList();
        List<HistoricActivityInstance> finishedActivity = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInformation.getProcessInstanceId()).finished().list();

        for (HistoricActivityInstance instance : finishedActivity) {
            activitiIds.add(instance.getActivityId());
        }
        if (!processInformation.isCompleted()) {
            activitiIds.addAll(runtimeService.getActiveActivityIds(processInformation.getProcessInstanceId()));
        }

        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInformation.getProcessId());
        List<String> highlightFlows = new ArrayList();
        processHighLightFlow(definition.getActivities(), highlightFlows, activitiIds);


        ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream inputStream = processDiagramGenerator.generateDiagram(repositoryService.getBpmnModel(processInformation.getProcessId()), "PNG", activitiIds, highlightFlows, processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), processEngineConfiguration.getAnnotationFontName(), processEngineConfiguration.getClassLoader(), 1.0);


        exportChartImage(response, inputStream);

        return null;
    }

    private void exportChartImage(HttpServletResponse response, InputStream inputStream) {
        try {
            response.reset();
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline;filename=task.png");

            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @RequestMapping(value = "/process/{viewname}", method = {RequestMethod.GET})
    @PermissionCheck(name = "chartView")
    public String processChart(HttpServletResponse response, @PathVariable("viewname") String viewname, HttpServletRequest request, Model model) {
        String processId = request.getParameter(DB_FIELD__ID);
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processId);
        ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream inputStream = processDiagramGenerator.generateDiagram(repositoryService.getBpmnModel(definition.getId()), "PNG", Collections.EMPTY_LIST, Collections.EMPTY_LIST, processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), processEngineConfiguration.getAnnotationFontName(), processEngineConfiguration.getClassLoader(), 1.0);
        exportChartImage(response, inputStream);
        return null;
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        logger.debug("user {} logout", getUserName(request));
        request.getSession().invalidate();
        return "redirect:create/logoutView";
    }


    @RequestMapping(value = "/change/{viewname}/{field}", method = {RequestMethod.GET, RequestMethod.POST})
    @PermissionCheck
    public String change(HttpServletRequest request, HttpServletResponse response, @PathVariable("viewname") String viewname, @PathVariable("field") String field, Model model) {
        logger.debug("start change the value event called on {}.{}", viewname, field);

        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);
        List<Map> list = (List<Map>) map.get(VIEW_FIELD_FIELDS);

        for (Map m : list) {
            if (request.getParameterMap().containsKey(m.get(VIEW_FIELD_FIELDS_NAME))) {
                ConvertUtils.setProperty(m, "attributes.value", request.getParameter((String) m.get(VIEW_FIELD_FIELDS_NAME)));
            }

            if (field.equals(m.get(VIEW_FIELD_FIELDS_NAME))) {
                model.addAttribute("field", m);
                continue;
            }
        }
        logger.debug("end change the value event called on {}.{}", viewname, field);

        model.addAttribute(MODEL_ATTRIBUTE_VIEW, map);
        model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        return VIEW_OUTPUT_CHANGE;

    }

    @RequestMapping(value = "/autocomplete/{viewname}/{field}", method = {RequestMethod.GET, RequestMethod.POST})
    @PermissionCheck
    public @ResponseBody
    List<Map<String, String>> autocomplete(HttpServletRequest request, HttpServletResponse response, @PathVariable("viewname") String viewname, @PathVariable("field") String field, Model model) {
        logger.debug("start autocomplete event the value event called on {}.{}", viewname, field);

        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);

        copyParamMapToViewMap(map, request.getParameterMap());

        Map<String, Object> fieldMap = formViewUtils.getField(viewContext, field);
        List<Map<String, String>> result = new ArrayList();
        Map<String, Object> option = MapUtils.getMap(fieldMap, VIEW_FIELD_FIELDS_OPTION);
        if (MapUtils.getString(option, "provider") == null) {
            Map<String, String> values = MapUtils.getMap(option, "values", Collections.emptyMap());
            processAutoComplete(viewContext, fieldMap, values, result);

        } else {
            OptionProvider optionProvider = springUtils.getOptionProvider(option);
            List<Map<String, String>> options = optionProvider.option(viewContext, fieldMap, MapUtils.getMap(MapUtils.getMap(fieldMap, VIEW_FIELD_FIELDS_OPTION), "parameter", Collections.EMPTY_MAP));
            if (options != null) {
                for (Map<String, String> values : options) {
                    processAutoComplete(viewContext, fieldMap, values, result);
                }
            }

        }
        logger.debug("end autocomplete event the value event called on {}.{}", viewname, field);
        return result;


    }

    private void processAutoComplete(ViewContext viewContext, Map<String, Object> fieldMap, Map<String, String> values, List<Map<String, String>> result) {
        for (String key : values.keySet()) {
            Map<String, String> keyValue = new HashMap();
            keyValue.put("value", key);
            keyValue.put("label", values.get(key));
            String value = (String) formViewUtils.getValue(viewContext, MapUtils.getString(fieldMap, "name"));
            if (key.contains(value)) {
                result.add(keyValue);
            }
        }
    }

    private ProcessInformation getProcessInformation(String taskId) {
        ProcessInformation processInformation = new ProcessInformation();
        if (taskId == null) {
            return processInformation;
        }
        processInformation.setTaskId(taskId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String InstanceId = "";
        if (task == null) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            if (historicTaskInstance != null) {
                InstanceId = historicTaskInstance.getProcessInstanceId();
                processInformation.setCompleted(true);
                processInformation.setTaskHistoryId(historicTaskInstance.getId());
                processInformation.setProcessId(historicTaskInstance.getProcessDefinitionId());
                processInformation.setProcessInstanceId(InstanceId);
                processInformation.setStarted(true);
            }

        } else {
            InstanceId = task.getProcessInstanceId();
            processInformation.setCompleted(false);
            processInformation.setTaskId(taskId);
            processInformation.setProcessInstanceId(InstanceId);
            processInformation.setProcessId(task.getProcessDefinitionId());
            processInformation.setStarted(true);
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

        return processInformation;
    }

    protected boolean doValidation(ViewContext viewContext, ProcessInformation processInformation, Map saveEntity, HttpServletRequest request) {
        logger.debug("start validation");
        if (viewContext.getFields() != null) {
            for (Map<String, Object> v : viewContext.getFields()) {
                UIComponent uiComponent=formViewUtils.getComponentByType(MapUtils.getString(v,VIEW_FIELD_FIELDS_TYPE));
                if (!uiComponent.isValidationRequired()) {
                    continue;
                } else {
                    String name = String.valueOf(v.get(VIEW_FIELD_FIELDS_NAME));
                    String type = String.valueOf(v.get(VIEW_FIELD_FIELDS_TYPE));
                    Map converter = (Map) v.get(VIEW_FIELD_FIELDS_CONVERTER);
                    Map parameter = (Map) v.get(VIEW_FIELD_FIELDS_PARAMETER);
                    String operator = (String) v.get(VIEW_FIELD_FIELDS_OPERATOR);

                    String processor = (String) v.get(VIEW_FIELD_FIELDS_PROCESSOR);
                    if (VIEW_FIELD_TYPE_SEARCH.equalsIgnoreCase(viewContext.getType())) {
                        formUtils.populateForSearch(v, name, type, converter, parameter, operator, request, saveEntity);

                    } else {
                        formUtils.populateForUpdate(name, type, converter, processor, request, saveEntity, false);
                    }
                }
            }
        }

        List<Map<String, Object>> validatorList = viewContext.getFieldValidators();
        if (validatorList == null)
            validatorList = new ArrayList();
        //add a simple validation on the field
        if (viewContext.getFields()!=null){
            for (Map<String,Object> field : viewContext.getFields()){
                Map<String,Object> attributes=MapUtils.getMap(field,VIEW_FIELD_FIELDS_ATTRIBUTES,new HashMap());
                Boolean required=MapUtils.getBoolean(attributes,"required",false);
                Integer maxlength=MapUtils.getInteger(attributes,"maxlength");
                Integer minlength=MapUtils.getInteger(attributes,"minlength");
                String name=MapUtils.getString(field,VIEW_FIELD_FIELDS_NAME);
                if(required&&!containValidator(validatorList,name,"required")){
                    Map<String, Object> validator = new HashMap<>();
                    validator.put(VIEW_FIELD_VALIDATORS_FIELD, name);
                    validator.put(VIEW_FIELD_VALIDATORS_MSG, "required");
                    validator.put(VIEW_FIELD_VALIDATORS_PROVIDER, "required");
                    validator.put(VIEW_FIELD_VALIDATORS_EXTERNAL, false);
                    validatorList.add(validator);
                }
                if((maxlength!=null&&maxlength>=0||minlength!=null&&minlength>=0)&&!containValidator(validatorList,name,"length")){
                    Map<String, Object> validator = new HashMap<>();
                    validator.put(VIEW_FIELD_VALIDATORS_FIELD, name);
                    validator.put(VIEW_FIELD_VALIDATORS_MSG, "length");
                    validator.put(VIEW_FIELD_VALIDATORS_PROVIDER, "length");
                    validator.put(VIEW_FIELD_VALIDATORS_PARAMETER,new BasicDBObject("max",maxlength).append("min",minlength));
                    validator.put(VIEW_FIELD_VALIDATORS_EXTERNAL, false);
                    validatorList.add(validator);
                }
            }
        }


        if (Boolean.TRUE.equals(viewContext.hasToken())) {
            logger.debug("add token validation");

            Map<String, Object> validator = new HashMap<>();
            validator.put(VIEW_FIELD_VALIDATORS_FIELD, "_session.token");
            validator.put(VIEW_FIELD_VALIDATORS_MSG, "token");
            validator.put(VIEW_FIELD_VALIDATORS_PROVIDER, "tokenValidator");
            validator.put(VIEW_FIELD_VALIDATORS_EXTERNAL, true);
            validatorList.add(validator);
        }
        //add a validation for task is complete
        if (processInformation != null && processInformation.isCompleted()) {
            logger.debug("add task validation");
            Map<String, Object> validator = new HashMap<>();
            validator.put(VIEW_FIELD_VALIDATORS_FIELD, "$task");
            validator.put(VIEW_FIELD_VALIDATORS_MSG, "task");
            validator.put(VIEW_FIELD_VALIDATORS_PROVIDER, "taskValidator");
            validator.put(VIEW_FIELD_VALIDATORS_EXTERNAL, true);
            validatorList.add(validator);
        }
        if (viewContext.getFields() != null && validatorList != null) {

            for (Map validator : validatorList) {
                ValidateContext validateContext = new ValidateContext(viewContext, validator);

                ValidatorProvider validatorProvider = springUtils.getValidatorProvider(validateContext.getProvider());
                Map<String, Object> formField = formViewUtils.getField(viewContext, validateContext.getField());

                logger.debug("start validation on {} with provider {}", validateContext.getField(), validatorProvider.getClass().getName());
                if (validateContext.isExternal() && validatorProvider != null || !validateContext.isExternal() && validatorProvider != null && formField != null) {
                    //no need validation if update = false
//                    if(formField.get(VIEW_FIELD_FIELDS_UPDATE)!=null&&Boolean.FALSE.equals(formField.get(VIEW_FIELD_FIELDS_UPDATE))){
//                        continue;
//                    }
                    HashMap parameter = new HashMap();
                    parameter.putAll(request.getParameterMap());
                    if (request instanceof MultipartHttpServletRequest) {
                        parameter.putAll(((MultipartHttpServletRequest) request).getFileMap());
                    }

                    if (!validatorProvider.validate(validateContext, parameter)) {
                        Map<String, Object> fieldValue = formViewUtils.getField(viewContext, validateContext.getField());
                        if (fieldValue != null) {
                            messageUtils.addErrorMessage(viewContext.getMessagesContext(), validateContext.getField(), validateContext.getMessage(), viewContext, fieldValue.get(VIEW_FIELD_FIELDS_LABEL));
                        } else {
                            messageUtils.addErrorMessage(viewContext.getMessagesContext(), validateContext.getField(), validateContext.getMessage(), viewContext);
                        }

                    }
                }

            }

            if (viewContext.getMessagesContext().get("error") != null && viewContext.getMessagesContext().get("error").size() > 0) {
                logger.debug("end validation with value {}", false);
                return false;
            }
        }
        logger.debug("end validation with value {}", true);
        return true;
    }

    private boolean containValidator(List<Map<String, Object>> validatorList, String name, String required) {
        for(Map<String,Object> validator:validatorList){
            String field=MapUtils.getString(validator,VIEW_FIELD_VALIDATORS_FIELD);
            String provider=MapUtils.getString(validator,VIEW_FIELD_VALIDATORS_PROVIDER);
            if(StringUtils.equals(field,name)&&StringUtils.equals(required,provider)){
                return true;
            }
        }
        return false;
    }


    private void doProcess(ViewContext viewContext, Map<String, Object> saveEntity, HttpServletRequest request) {
        logger.debug("call activiti start");
        Map<String, Object> processMap = viewContext.getProcess();
        String taskId = request.getParameter(PARAMETER_FIELD__TASKID);
        String id = request.getParameter(PARAMETER_FIELD__ID);
        FormProcessor formProcessor = springUtils.getFormProcessor(viewContext);
        if (StringUtils.isEmpty(id)) {
            HashMap<String, Object> context = new HashMap();
            context.put("form", saveEntity);
            if (formProcessor.beforeStartProcess(viewContext, context, saveEntity) && processMap != null) {
                String name = (String) processMap.get("name");
                logger.debug("start process {}:", name);
                ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionName(name).latestVersion().singleResult();
                if (definition != null) {
                    Map<String, Object> output = (Map) processMap.get("output");
                    Map<String, Object> parameter = parseExpress(output, context);
                    parameter.put("requester", getUserName(request));
                    try {
                        identityService.setAuthenticatedUserId(getUserName(request));
                        ProcessInstance processInstance = runtimeService.startProcessInstanceById(definition.getId(), (String) saveEntity.get(DB_FIELD__ID), parameter);
                        saveEntity.put("processId", processInstance.getId());
                        BasicDBObject test = new BasicDBObject(DB_FIELD__ID, saveEntity.get(DB_FIELD__ID));
                        jdbcService.updateData(test, saveEntity);
                    } finally {
                        identityService.setAuthenticatedUserId(null);
                    }


                }
            }
        } else {
            logger.debug("complete task {}:", taskId);
            HashMap<String, Object> context = new HashMap();
            context.put("form", saveEntity);
            if (formProcessor.beforeCompleteProcess(viewContext, context, saveEntity) && processMap != null) {
                Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (task != null) {
                    Map<String, Object> output = (Map) processMap.get("output");
                    Map<String, Object> parameter = parseExpress(output, context);
                    Map<String, Object> complete = (Map) processMap.get("complete");
                    if (complete != null) {
                        ExpressionProvider provider = springUtils.getExpressionProvider(complete);
                        Map value = provider.getValue(complete, context);
                        if (Boolean.TRUE.equals(value.get("value"))) {
                            try {
                                identityService.setAuthenticatedUserId(getUserName(request));
                                taskService.complete(taskId, parameter);
                            } finally {
                                identityService.setAuthenticatedUserId(null);
                            }
                        }

                    }
                }
            }
        }
        logger.debug("call activiti end");
    }

    private String processPopup(String returnPath, HttpServletRequest request) {
        //keep the popup
        if (request.getParameter("_popup") != null) {
            if (returnPath.indexOf("?") >= 0) {
                return returnPath + "&_popup=true";
            } else {
                return returnPath + "_popup=true";
            }
        }
        return returnPath;
    }

    private void copyParamMapToViewMap(Map<String, Object> view, Map<String, String[]> value) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) ConvertUtils.getProperty(view, "fields");
        if(list!=null){
            for (Map map : list) {
                String name = String.valueOf(map.get("name"));
                String type = String.valueOf(map.get("type"));
                UIComponent uiComponent=formViewUtils.getComponentByType(type);
                HashMap<String, Object> attr = (HashMap<String, Object>) map.get("attributes");
                if (attr == null) {
                    attr = new HashMap<String, Object>();
                    map.put("attributes", attr);
                }
                String[] values = (String[]) value.get(name);
                if (values != null && values.length > 0) {
                    if (uiComponent.isBinaryType()) {
                        attr.put("value", values[0]);
                    } else if (!uiComponent.isMultiValue()) {
                        attr.put("value", values[0]);
                    } else if (uiComponent.isMultiValue()) {
                        attr.put("value", Arrays.asList(values));
                    }
                }


            }
        }

    }

    private void processHighLightFlow(List<ActivityImpl> activitis, List<String> highLightFlows, List<String> historyInstanceList) {
        for (ActivityImpl activity : activitis) {
            if ("subProcess".equals(activity.getProperty("type"))) {
                processHighLightFlow(activity.getActivities(), highLightFlows, historyInstanceList);
            }
            if (historyInstanceList.contains(activity.getId())) {
                List<PvmTransition> pvmTransitions = activity.getOutgoingTransitions();
                for (PvmTransition pvmTransition : pvmTransitions) {
                    String dest = pvmTransition.getDestination().getId();
                    if (historyInstanceList.contains(dest)) {
                        highLightFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
    }


    protected String getUserName(HttpServletRequest request) {
        Map map = (Map) request.getSession().getAttribute("user");
        return map == null ? null : String.valueOf(map.get("name"));
    }
    protected String getUserId(HttpServletRequest request) {
        Map map = (Map) request.getSession().getAttribute("user");
        return map == null ? null : String.valueOf(map.get(DB_FIELD__ID));
    }

    private Map<String, Object> parseExpress(Map<String, Object> output, Map<String, Object> context) {
        Map<String, Object> parameter = new HashMap();
        if (output != null) {
            for (String key : output.keySet()) {
                Object temp = (Object) output.get(key);
                if (temp instanceof Map) {
                    ExpressionProvider provider = springUtils.getExpressionProvider((Map) temp);
                    parameter.put(key, provider.getValue((Map) temp, context).get("value"));
                } else {
                    parameter.put(key, temp);
                }
            }
        }
        return parameter;
    }

    @RequestMapping(value = "/export/{viewname}", method = {RequestMethod.GET, RequestMethod.POST})
    public String export(HttpServletResponse response, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "order", required = false) String order, @PathVariable("viewname") String viewname, HttpServletRequest request, Model model) throws IOException {
        logger.debug("start do export {}", viewname);

        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);
        if (!VIEW_TYPE_SEARCH.equals(viewContext.getType())) {
            logger.debug("end do export {} with error", viewname);
            response.sendError(404);
            return null;
        }
//        if(viewContext.getExport()==null){
//            logger.debug("no export template found",viewname);
//            response.sendError(404);
//            return null;
//        }

        Map<String, Object> searchCondition = (Map<String, Object>) map.get(VIEW_FIELD_DEFAULT_VALUE);

        searchCondition = prepareSearchCondition(searchCondition);

        Map<String, Object> inputParameter = (Map) map.get(VIEW_FIELD_INPUT);
        if (inputParameter != null) {
            searchCondition.putAll(inputParameter);
        }

        //add for check createdBy
        if(viewContext.isSelfOnly()&&(viewContext.getDataPermission()==null||!permissionProvider.hasPermission(viewContext.getDataPermission()))){
            searchCondition.put(AUDIT_CREATED_BY,getUserId(request));
        }

        boolean isValid = doValidation(viewContext, null, searchCondition, request);

//        HashMap<String, Object> result = new HashMap();
        Map<String, Object> saveEntity = new HashMap();
        if (!isValid) {
            //fix for error
            for (Map<String, Object> v : viewContext.getFields()) {

                UIComponent uiComponent=formViewUtils.getComponentByType(MapUtils.getString(v,VIEW_FIELD_FIELDS_TYPE));
                if (!uiComponent.isValidationRequired()) {
                    continue;
                }
                String name = String.valueOf(v.get(VIEW_FIELD_FIELDS_NAME));
                String type = String.valueOf(v.get(VIEW_FIELD_FIELDS_TYPE));
                Map converter = (Map) v.get(VIEW_FIELD_FIELDS_CONVERTER);
                //if the name start with $, then update the ref entity
                formUtils.populateForRestore(name, type, converter, request, saveEntity, false);

            }
            formUtils.copyValuesToViewMap(viewContext, saveEntity);
            model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
            model.addAttribute(MODEL_ATTRIBUTE_MESSAGES, viewContext.getMessagesContext());

            model.addAttribute(MODEL_ATTRIBUTE_VIEW, map);
            model.addAttribute(MODEL_ATTRIBUTE_VIEW_CONTEXT, viewContext);
            return VIEW_OUTPUT_SHOW;


        } else {
            ListOrderedMap sortOrder = new ListOrderedMap();
            if (sort != null) {
                sortOrder.put(sort, "asc".equals(order) ? "1" : "-1");
            } else {
//                sortOrder.putAll(viewContext.getDefaultSort());
            }
            SearchFormProcessor searchFormProcessor = springUtils.getSearchFormProcessor(map);
            SearchResult searchResult = searchFormProcessor.processSearch(viewContext, new BasicDBObject(searchCondition), sortOrder, 0, 0);
            ArrayList<Map<String, Object>> actualResultList = springUtils.getActualResultList(viewContext);


            //handle result iterator
            processSearchResult(searchResult, viewContext);


            List<Map> resultList2 = springUtils.getResultList(viewContext, searchResult.getResult());

            logger.debug("end do export {}", viewname);

            Context context = new Context();
            Resource resource = new ClassPathResource(MapUtils.getString(viewContext.getExport(), "template", "export/default.xlsx"));
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline;filename=" + viewname + ".xlsx");
            context.putVar("data", searchResult.getResult());
            context.putVar("total", searchResult.getTotal());
            context.putVar("user", getUserName(request));
            context.putVar("headers", actualResultList);
            JxlsHelper.getInstance().processTemplate(resource.getInputStream(), response.getOutputStream(), context);
            return null;
        }
    }

    private Map<String, Object> prepareSearchCondition(Map<String, Object> searchCondition) {
        if (searchCondition == null)
            searchCondition = new HashMap();
        else {
            for (String key : searchCondition.keySet()) {
                Object values = searchCondition.get(key);
                if (values instanceof List) {
                    searchCondition.put(key, new BasicDBObject($in, values));
                }
            }
        }
        return searchCondition;
    }


    @RequestMapping(value = "/chart/{viewname}", method = {RequestMethod.GET,RequestMethod.POST})
    public String chart(HttpServletRequest request, HttpServletResponse response, @PathVariable("viewname") String viewname, Model model) throws IOException {
        logger.debug("start show create view:{}", viewname);
        Map<String, Object> map = formViewUtils.getFormView(viewname);
        ViewContext viewContext = new ViewContext(map);

        if (!VIEW_TYPE_CHART.equals(viewContext.getType())) {
            return VIEW_OUTPUT_ERROR;
        }

        copyParamMapToViewMap(map,request.getParameterMap());

        model.addAttribute("view", map);
        model.addAttribute("viewContext", viewContext);
        model.addAttribute(MODEL_ATTRIBUTE_REQUEST, request);
        return VIEW_OUTPUT_SHOW;

    }
    @PostMapping(value = "/search/update/{viewname}")
    @ResponseBody
    public void editTbaleUpdate(@PathVariable("viewname")String viewname,HttpServletRequest rerequest,HttpServletResponse response) throws IOException {
        ViewContext viewContext=new ViewContext(formViewUtils.getFormView(viewname));
        List<Map<String,Object>> validators = viewContext.getResultValidators();
        String id=rerequest.getParameter("pk");
        String name=rerequest.getParameter("name");
        String valueParam=rerequest.getParameter("value");
        Object value=rerequest.getParameter("value");


        HashMap fieldValueMap=new HashMap();
        fieldValueMap.put(name,value);
        if (!validators.isEmpty()){
            List<Map<String,Object>> result=springUtils.getActualResultList(viewContext);
            for (Map<String,Object> r:result) {
                String n = MapUtils.getString(r, VIEW_FIELD_RESULT_NAME);
                Map converter = (Map) r.get(VIEW_FIELD_RESULT_CONVERTER);
                String data = r.get(VIEW_FIELD_RESULT_DATA)==null?MapUtils.getString(r,VIEW_FIELD_RESULT_DATA):viewContext.getData();
                ConvertProvider provider = springUtils.getConvertProvider(converter);
                if (!name.startsWith($)) {
                    value = provider.convert(valueParam, new ConvertContext(converter));
                }
                if (StringUtils.equals(name, n)) {
                    boolean validated=true;
                    for(Map<String,Object> v:validators){
                        String vName=MapUtils.getString(v,VIEW_FIELD_VALIDATORS_FIELD);
                        if(StringUtils.equals(n,vName)){
                            ValidateContext validateContext = new ValidateContext(viewContext, v);
                            ValidatorProvider validatorProvider = springUtils.getValidatorProvider(validateContext.getProvider());
                            if(!validatorProvider.validate(validateContext,fieldValueMap)){
                                validated=false;
                                messageUtils.addErrorMessage(viewContext.getMessagesContext(), validateContext.getField(), validateContext.getMessage(), viewContext,MapUtils.getString(r,VIEW_FIELD_RESULT_LABEL));

                            }
                        }
                    }
                    if (validated){
                        jdbcService.update(viewContext.getData(),new BasicDBObject(DB_FIELD__ID,id),new BasicDBObject(name,value));
                    }else {
                        response.setHeader("Content-Type","text/plain");
                        response.sendError(400,(String)viewContext.getMessagesContext().get("error").get(0).get("msg"));
                    }
                    return;
                }
            }
        }else {
            jdbcService.update(viewContext.getData(),new BasicDBObject(DB_FIELD__ID,id),new BasicDBObject(name,value));
        }
    }
}

