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

import org.apache.commons.collections.OrderedMap;

import java.util.Map;

/**
 * User: yuebo
 * Date: 1/6/15
 * Time: 4:53 PM
 */
public interface SearchFormProcessor {

    public SearchResult processSearch(ViewContext viewContext, Map filter, OrderedMap sort, int limit, int skip);
}
