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

package com.ifreelight.dyna;

/**
 * Created by yuebo on 30/11/2017.
 */
public interface DbConstant {
    String $in = "$in";
    String $gte = "$gte";
    String $lte = "$lte";
    String $gt = "$gt";
    String $lt = "$lt";
    String $eq = "$eq";
    String $like = "$like";
    String $ne = "$ne";
    String $null = "$null";


    String TBL_USER="tbl_user";
    String TBL_ROLE="tbl_role";
    String TBL_USER_ROLE="tbl_user_role";
    String TBL_JOB="tbl_job";
    String TBL_JOB_ERROR="tbl_job_error";
    String TBL_MESSAGE="tbl_message";
    String TBL_PERMISSION="tbl_permission";
    String TBL_USER_PERMISSION="tbl_user_permission";
    String TBL_ROLE_PERMISSION="tbl_role_permission";
    String TBL_TASK_LOG="tbl_task_log";
    String TBL_INTERFACE_PREFIX="tbl_int_";

}
