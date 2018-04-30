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

package com.ifreelight.elfinder.util;

import com.ifreelight.elfinder.controller.executor.FsItemEx;
import com.ifreelight.elfinder.service.FsItemFilter;

import java.util.ArrayList;
import java.util.List;

public abstract class FsItemFilterUtils
{
	public static FsItemFilter FILTER_ALL = new FsItemFilter()
	{
		@Override
		public boolean accepts(FsItemEx item)
		{
			return true;
		}
	};

	public static FsItemFilter FILTER_FOLDER = new FsItemFilter()
	{
		@Override
		public boolean accepts(FsItemEx item)
		{
			return item.isFolder();
		}
	};

	public static FsItemFilter createFileNameKeywordFilter(final String keyword)
	{
		return new FsItemFilter()
		{
			@Override
			public boolean accepts(FsItemEx item)
			{
				return item.getName().contains(keyword);
			}
		};
	}

	public static FsItemEx[] filterFiles(FsItemEx[] sourceFiles,
                                         FsItemFilter filter)
	{
		List<FsItemEx> filtered = new ArrayList<FsItemEx>();
		for (FsItemEx file : sourceFiles)
		{
			if (filter.accepts(file))
				filtered.add(file);
		}

		return filtered.toArray(new FsItemEx[0]);
	}

	/**
	 * returns a FsItemFilter according to given mimeFilters
	 * 
	 * @param mimeFilters
	 * @return
	 */
	public static FsItemFilter createMimeFilter(final String[] mimeFilters)
	{
		if (mimeFilters == null || mimeFilters.length == 0)
			return FILTER_ALL;

		return new FsItemFilter()
		{
			@Override
			public boolean accepts(FsItemEx item)
			{
				String mimeType = item.getMimeType().toUpperCase();

				for (String mf : mimeFilters)
				{
					mf = mf.toUpperCase();
					if (mimeType.startsWith(mf + "/") || mimeType.equals(mf))
						return true;
				}
				return false;
			}
		};
	}

}
