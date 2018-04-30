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

package com.ifreelight.elfinder.controller.executor;

import java.util.Map;

public class DefaultCommandExecutorFactory implements CommandExecutorFactory
{
	String _classNamePattern;

	private Map<String, CommandExecutor> _map;

	private CommandExecutor _fallbackCommand;

	@Override
	public CommandExecutor get(String commandName)
	{
		if (_map.containsKey(commandName))
			return _map.get(commandName);

		try
		{
			String className = String.format(_classNamePattern,
				commandName.substring(0, 1).toUpperCase() + commandName.substring(1));
			return (CommandExecutor) Class.forName(className).newInstance();
		}
		catch (Exception e)
		{
			//not found
			return _fallbackCommand;
		}
	}

	public String getClassNamePattern()
	{
		return _classNamePattern;
	}

	public Map<String, CommandExecutor> getMap()
	{
		return _map;
	}

	public CommandExecutor getFallbackCommand() {
		return _fallbackCommand;
	}

	public void setClassNamePattern(String classNamePattern)
	{
		_classNamePattern = classNamePattern;
	}

	public void setMap(Map<String, CommandExecutor> map)
	{
		_map = map;
	}

	public void setFallbackCommand(CommandExecutor fallbackCommand) {
		this._fallbackCommand = fallbackCommand;
	}
}
