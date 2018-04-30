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

package com.ifreelight.dyna.core;

import java.util.Map;

/**
 * This is an interface for create view to load, process the data.
 * <p>see the {@link com.ifreelight.dyna.provider.processor.DefaultFormProcessor} for the default processing</p>
 * User: yuebo
 * Date: 12/2/14
 * Time: 9:35 AM
 */
public interface FormProcessor {

    /**
     * Define the preCheck for loading view, and can return a custom path if necessary
     * @param context
     *          The current view context
     * @return
     *          The path to redirect if not empty
     * */
    default public String preCheck(ViewContext context) {
        return context.getPath();
    }
    /**
     * Define the reload action for reload the page while the validation failed
     * @param viewContext
     *          The current view context
     * @param saveEntity
     *          The data user submitted
     * */
    void reload(ViewContext viewContext, Map saveEntity);
    /**
     * Define the load action for entering the page
     * @param viewContext
     *          The current view context
     * @param condition
     *          The search condition for user submitted
     * @return
     *          The data to show on the page
     * */
    Map<String, Object> load(ViewContext viewContext, Map condition);
    /**
     * Define the hook before start the process
     * @param viewContext
     *          The current view context
     * @param elContext
     *          The Spring EL parameter context, used for process engine to find the value
     * @param saveEntity
     *          The data user submitted
     * @return
     *          true is process to start the process
     * */
    boolean beforeStartProcess(ViewContext viewContext, Map<String, Object> elContext, Map saveEntity);
    /**
     * Define the hook before start the process
     * @param viewContext
     *          The current view context
     * @param elContext
     *          The Spring EL parameter context, used for process engine to find the value
     * @param saveEntity
     *          The data user submitted
     * @return
     *          true is process to start the process
     * */
    boolean beforeCompleteProcess(ViewContext viewContext, Map<String, Object> elContext, Map saveEntity);
    /**
     * Define submit data process logic for the create form
     * @param viewContext
     *          The current view context
     * @param saveEntity
     *          The data user submitted
     * @return
     *          the redirect path after submit data
     * */
    String process(ViewContext viewContext, Map saveEntity);
}
