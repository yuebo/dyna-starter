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

package com.github.yuebo.elfinder.impl;

import com.github.yuebo.elfinder.service.FsItem;
import com.github.yuebo.elfinder.service.FsSecurityChecker;
import com.github.yuebo.elfinder.service.FsService;

public class FsSecurityCheckForAll implements FsSecurityChecker
{
	boolean _locked = false;

	boolean _readable = true;

	boolean _writable = true;

	public boolean isLocked()
	{
		return _locked;
	}

	@Override
	public boolean isLocked(FsService fsService, FsItem fsi)
	{
		return _locked;
	}

	public boolean isReadable()
	{
		return _readable;
	}

	@Override
	public boolean isReadable(FsService fsService, FsItem fsi)
	{
		return _readable;
	}

	public boolean isWritable()
	{
		return _writable;
	}

	@Override
	public boolean isWritable(FsService fsService, FsItem fsi)
	{
		return _writable;
	}

	public void setLocked(boolean locked)
	{
		_locked = locked;
	}

	public void setReadable(boolean readable)
	{
		_readable = readable;
	}

	public void setWritable(boolean writable)
	{
		_writable = writable;
	}

}
