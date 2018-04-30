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

/**
 * Define a convert operation
 * <p>convert provider is used for covert the user input from a value to another when create view submitted or restore a value when load the create view page or search result</p>
 * User: yuebo
 * Date: 12/4/14
 * Time: 9:32 AM
 */
public interface ConvertProvider {
    /**
     *
     * @param v
     *       the value before convert
     * @param convertContext
     *       the convert context includes the parameters
     * @return
     *      the new value after convert
     */
    Object convert(Object v, ConvertContext convertContext);
    /**
     *
     * @param o
     *       the value before restore
     * @param convertContext
     *       the convert context includes the parameters
     * @return
     *      the new value after restore
     */
    Object restore(Object o, ConvertContext convertContext);
}
