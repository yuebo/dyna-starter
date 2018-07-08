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

package com.github.yuebo.dyna;

import java.util.Arrays;

/**
 * User: yuebo
 * Date: 12/2/14
 * Time: 9:45 AM
 */
public interface AppConstants extends DbConstant {

    String DB_FIELD__ID = "id";
    String PARAMETER_FIELD__TASKID = "_taskId";
    String PARAMETER_FIELD__ID = "id";
    String PARAMETER_FIELD__BACK = "_back";
    String DB_FIELD__DATA = "_data";
    String DB_FIELD_DATA = "data";
    String VIEW_TYPE_CREATE = "create";
    String VIEW_TYPE_MENU = "menu";
    String VIEW_TYPE_UPLOAD = "upload";
    String VIEW_TYPE_SEARCH = "search";
    String VIEW_TYPE_LIST = "list";
    String VIEW_TYPE_CHART = "chart";
    String $ = "$";
    String EVENT_LIST = "eventList";

    String SESSION_KEY_SEARCH_PARAM = "search_param";
    String SESSION_KEY_USER = "user";


    //    String VIEW_FIELD__ID="_id";
    //define the process activity in view json
    String VIEW_FIELD_PROCESS = "process";
    String VIEW_FIELD_INPUT_PARAMETER = "inputParameter";
    //the input will convert from inputParameter
    String VIEW_FIELD_INPUT = "input";
    //custom the out path for loadAction
    String VIEW_FIELD_VIEW = "view";
    //define the view fields for create and search view
    String VIEW_FIELD_FIELDS = "fields";
    //define the view fields for create and search view
    String VIEW_FIELD_PERMISSION = "permission";
    //define the create view in readOnly mode, which will set the field to readonly
    String VIEW_FIELD_READONLY = "readOnly";
    //define the default search condition or default save field values on the fields, the value can be el expression
    String VIEW_FIELD_DEFAULT_VALUE = "defaultValue";
    String VIEW_FIELD_EXPORT = "export";
    String VIEW_FIELD_DEFAULT_SORT = "defaultSort";
    //define the field type in the fields, see INPUT_TYPE_* for values
    String VIEW_FIELD_FIELDS_TYPE = "type";
    //define the field attributes
    String VIEW_FIELD_FIELDS_ATTRIBUTES = "attributes";
    String VIEW_FIELD_FIELDS_PERMISSION = "permission";
    //define the field name
    String VIEW_FIELD_FIELDS_ID = "id";
    //define the field name
    String VIEW_FIELD_FIELDS_NAME = "name";
    //define the field display label
    String VIEW_FIELD_FIELDS_LABEL = "label";
    //define the field converter
    String VIEW_FIELD_FIELDS_CONVERTER = "converter";
    //define the field converter parameter
    String VIEW_FIELD_FIELDS_PARAMETER = "parameter";
    //define the search view field if using the like, only accept $like
    String VIEW_FIELD_FIELDS_OPERATOR = "operator";
    String VIEW_FIELD_FIELDS_TIP = "tip";
    String VIEW_FIELD_FIELDS_VIEW = "view";
    String VIEW_FIELD_FIELDS_TITLE = "title";
    String VIEW_FIELD_FIELDS_CONTAINER = "container";
    String VIEW_FIELD_FIELDS_JOIN = "join";
    String VIEW_FIELD_FIELDS_DEFAULT = "default";
    //define the file processor for the file type
    String VIEW_FIELD_FIELDS_PROCESSOR = "processor";
    //define the fields if it can be update, if false, the value will be ignore when save
    String VIEW_FIELD_FIELDS_UPDATE = "update";
    //define the option or option provider
    String VIEW_FIELD_FIELDS_OPTION = "option";
    //define the search view result
    String VIEW_FIELD_RESULT = "result";
    //search result set converter
    String VIEW_FIELD_RESULT_CONVERTER = "converter";
    String VIEW_FIELD_RESULT_DATA = "data";
    //search result field column name
    String VIEW_FIELD_RESULT_NAME = "name";
    String VIEW_FIELD_RESULT_LABEL = "label";
    String VIEW_FIELD_RESULT_ATTIBUTES = "attributes";
    String VIEW_FIELD_RESULT_PERMISSION = "permission";
    //define if the message clear after refresh. in subview, it always false
    String VIEW_FIELD_CLEAR = "clear";
    //define the view type, ie. search or create
    String VIEW_FIELD_TYPE = "type";
    //define the view type, ie. search or create
    String VIEW_FIELD_TITLE = "title";
    //define the validators for the view
    String VIEW_FIELD_VALIDATORS = "validators";
    //define the button actions
    String VIEW_FIELD_ACTIONS = "actions";
    //define search result action
    String VIEW_FIELD_OPERATE = "operate";
    //define the create view type, value can be 'multipart/form-data' or 'application/x-www-form-urlencoded'
    String VIEW_FIELD_ENCTYPE = "enctype";
    //used to set the immediate redirect path
    String VIEW_FIELD_PATH = "path";
    String VIEW_FIELD_CHARTS = "charts";
    String VIEW_FIELD_CHARTS_NAME = "name";
    String VIEW_FIELD_SELF_ONLY = "selfOnly";
    String VIEW_FIELD_DATA_PERMISSION = "dataPermission";

    String VIEW_FIELD_ENCTYPE_MULTIPART = "multipart/form-data";

    //define if the view validate token
    String VIEW_FIELD_TOKEN = "token";
    String VIEW_FIELD_VALIDATORS_FIELD = "field";
    String VIEW_FIELD_VALIDATORS_TYPE = "type";
    String VIEW_FIELD_VALIDATORS_MATCH = "match";
    String VIEW_FIELD_VALIDATORS_MSG = "msg";
    String VIEW_FIELD_VALIDATORS_PROVIDER = "provider";
    String VIEW_FIELD_VALIDATORS_EXTERNAL = "external";
    String VIEW_FIELD_VALIDATORS_PARAMETER = "parameter";

    String VIEW_FIELD_OPERATE_NAME = "name";
    String VIEW_FIELD_OPERATE_ICON = "icon";
    String VIEW_FIELD_OPERATE_CONFIRM = "confirm";
    String VIEW_FIELD_OPERATE_OPERATE = "operate";
    String VIEW_FIELD_OPERATE_POPUP = "popup";
    String VIEW_FIELD_OPERATE_VIEW = "view";
    String VIEW_FIELD_OPERATE_TYPE = "type";
    String VIEW_FIELD_OPERATE_REFRESH = "refresh";
    String VIEW_FIELD_OPERATE_CHANGE = "change";
    String VIEW_FIELD_OPERATE_PROVIDER = "provider";
    String VIEW_FIELD_OPERATE_PARAMETER = "parameter";
    String VIEW_FIELD_OPERATE_DEPEND = "depend";
    String VIEW_FIELD_OPERATE_VALUE = "value";


    String VIEW_FIELD_TYPE_SEARCH = "search";

    String VIEW_FIELD_NAME = "name";
    String VIEW_FIELD_DATA = "data";
    String VIEW_FIELD__TASKID = "_taskId";
    String VIEW_FIELD__ID = "id";
    String VIEW_FIELD_MENU = "menu";
    String VIEW_FIELD_PROCESSOR = "processor";
    String VIEW_FIELD_MESSAGES = "messages";
    String VIEW_FIELD_REDIRECT = "redirect";
    String VIEW_FIELD_SEARCHVIEW = "searchview";
    String VIEW_FIELD_QUERY = "query";

    String VIEW_CONVERTER_NAME="name";
    String VIEW_CONVERTER_PROVIDER="provider";
    String VIEW_CONVERTER_PARAMETER="parameter";

    String VIEW_OPTION_VALUES="values";
    String VIEW_OPTION_NAME="name";
    String VIEW_OPTION_PROVIDER="provider";
    String VIEW_OPTION_PARAMETER="parameter";

    String VIEW_ACTION_SUBMIT="submit";
    String VIEW_ACTION_SEARCH="search";
    String VIEW_ACTION_BACK="back";
    String VIEW_ACTION_CLOSE="close";
    String VIEW_ACTION_NAME="name";
    String VIEW_ACTION_LABEL="label";
    String VIEW_ACTION_TITLE="title";
    String VIEW_ACTION_STYLE="style";
    String VIEW_ACTION_UPDATE="update";
    String VIEW_ACTION_ID="id";
    String VIEW_ACTION_VIEW="view";
    String VIEW_ACTION_TYPE="type";
    String VIEW_ACTION_PARAMETER="parameter";
    String VIEW_ACTION_URL="url";
    String VIEW_ACTION_JAVASCRIPT="javascript";
    String VIEW_ACTION_POPUP="popup";
    String VIEW_ACTION_EXPORT="export";
    String VIEW_ACTION_REFRESH="refresh";

    String VIEW_SUBVIEW_NAME="name";
    String VIEW_SUBVIEW_PARAMETER="parameter";
    String VIEW_FIELD_SUBVIEW = "subview";

    String VIEW_FIELD_CHANGE = "change";

    String VIEW_CHANGE_NAME = "name";
    String VIEW_CHANGE_TYPE = "type";
    String VIEW_CHANGE_TARGET = "target";
    String VIEW_CHANGE_VALUE = "value";





    //if the search is valid, return false if the validation is failed
    String SEARCH_RESULT_VALIDATE = "validate";
    //the search result error message list
    String SEARCH_RESULT_ERROR = "error";
    //define if the message clear after refresh. in subview, it always false
    String SEARCH_RESULT_CLEAR = "clear";
    //define the result
    String SEARCH_RESULT_ROWS = "rows";
    //define the result count
    String SEARCH_RESULT_TOTAL = "total";


    String VIEW_OUTPUT_ERROR = "error";
    String VIEW_OUTPUT_SHOW = "show";
    String VIEW_OUTPUT_TABLE = "table";
    String VIEW_OUTPUT_CHANGE = "change";


    String MODEL_ATTRIBUTE_VIEW = "view";
    String MODEL_ATTRIBUTE_VIEW_CONTEXT = "viewContext";
    String MODEL_ATTRIBUTE_REQUEST = "request";
    String MODEL_ATTRIBUTE_MESSAGES = "messages";
    String MODEL_ATTRIBUTE_PARAMS = "params";


//    String INPUT_TYPE_TEXT = "text";
//    String INPUT_TYPE_RADIO = "radio";
//    String INPUT_TYPE_TEXTAREA = "textarea";
//    String INPUT_TYPE_SELECT = "select";
//    String INPUT_TYPE_MULTI_SELECT = "muti-select";
//    String INPUT_TYPE_CHECK_BOX = "checkbox";
//    String INPUT_TYPE_FILE = "file";
//    String INPUT_TYPE_LABEL = "label";
//    String INPUT_TYPE_DYNA_LABEL = "dyna-label";
//    String INPUT_TYPE_RICHTEXT = "richtext";
//    String INPUT_TYPE_ELFINDER = "elfinder";
//    String INPUT_TYPE_PASSWORD = "password";
//    String INPUT_TYPE_NUMBER = "number";
//    String INPUT_TYPE_DATE = "date";
//    String INPUT_TYPE_HIDDEN = "hidden";
//    String INPUT_TYPE_EMAIL = "email";
//    String INPUT_TYPE_PICKER = "picker";
//    String INPUT_TYPE_AUTOCOMPLETE = "autocomplete";
//    String INPUT_TYPE_CODE = "code";



//    default boolean isLabelType(String type) {
//        String[] labels = new String[]{INPUT_TYPE_LABEL, INPUT_TYPE_DYNA_LABEL};
//        return Arrays.asList(labels).contains(type);
//    }
//
//    default boolean isSingleValue(String type) {
//        String[] controls = new String[]{INPUT_TYPE_TEXT, INPUT_TYPE_RADIO, INPUT_TYPE_TEXTAREA, INPUT_TYPE_SELECT, INPUT_TYPE_FILE, INPUT_TYPE_RICHTEXT, INPUT_TYPE_ELFINDER, INPUT_TYPE_PASSWORD, INPUT_TYPE_NUMBER, INPUT_TYPE_EMAIL, INPUT_TYPE_DATE, INPUT_TYPE_HIDDEN,INPUT_TYPE_AUTOCOMPLETE,INPUT_TYPE_PICKER,INPUT_TYPE_CODE};
//        return Arrays.asList(controls).contains(type);
//    }
//
//    default boolean isMultiValue(String type) {
//        String[] controls = new String[]{INPUT_TYPE_MULTI_SELECT, INPUT_TYPE_CHECK_BOX};
//        return Arrays.asList(controls).contains(type);
//    }
//    default boolean isOptionValue(String type) {
//        String[] controls = new String[]{INPUT_TYPE_SELECT, INPUT_TYPE_MULTI_SELECT,INPUT_TYPE_RADIO,INPUT_TYPE_AUTOCOMPLETE,INPUT_TYPE_CHECK_BOX,INPUT_TYPE_DYNA_LABEL};
//        return Arrays.asList(controls).contains(type);
//    }



}
