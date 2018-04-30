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

package com.github.yuebo.elfinder.service;

import com.github.yuebo.elfinder.controller.executor.FsItemEx;

import java.io.IOException;

public interface FsService
{
	FsItem fromHash(String hash) throws IOException;

	String getHash(FsItem item) throws IOException;

	FsSecurityChecker getSecurityChecker();

	String getVolumeId(FsVolume volume);

	FsVolume[] getVolumes();

	FsServiceConfig getServiceConfig();

	/**
	 * find files by name pattern, this is often implemented upon a metadata
	 * store, or lucene-like search engines
	 * 
	 * @param filter
	 * @return
	 */

	// TODO: bad designs: FsItemEx should not used here
	//top level interfaces should only know FsItem instead of FsItemEx
	FsItemEx[] find(FsItemFilter filter);
}
