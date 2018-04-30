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

import com.github.yuebo.elfinder.service.FsSecurityChecker;

import java.util.regex.Pattern;

public class FsSecurityCheckFilterMapping
{
	FsSecurityChecker _checker;

	String _pattern;

	public FsSecurityChecker getChecker()
	{
		return _checker;
	}

	public String getPattern()
	{
		return _pattern;
	}

	public boolean matches(String hash)
	{
		return Pattern.compile(_pattern).matcher(hash).matches();
	}

	public void setChecker(FsSecurityChecker checker)
	{
		_checker = checker;
	}

	public void setPattern(String pattern)
	{
		_pattern = pattern;
	}
}
