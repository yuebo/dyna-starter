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

package com.github.yuebo.elfinder.localfs;


import com.github.yuebo.elfinder.service.FsItem;
import com.github.yuebo.elfinder.service.FsVolume;
import com.github.yuebo.elfinder.util.MimeTypesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class LocalFsVolume implements FsVolume
{
	@Override
	public String toString()
	{
		return "LocalFsVolume [" + _rootDir + "]";
	}

	String _name;

	File _rootDir;

	private File asFile(FsItem fsi)
	{
		return ((LocalFsItem) fsi).getFile();
	}

	@Override
	public void createFile(FsItem fsi) throws IOException
	{
		asFile(fsi).createNewFile();
	}

	@Override
	public void createFolder(FsItem fsi) throws IOException
	{
		asFile(fsi).mkdirs();
	}

	@Override
	public void deleteFile(FsItem fsi) throws IOException
	{
		File file = asFile(fsi);
		if (!file.isDirectory())
		{
			file.delete();
		}
	}

	@Override
	public void deleteFolder(FsItem fsi) throws IOException
	{
		File file = asFile(fsi);
		if (file.isDirectory())
		{
			FileUtils.deleteDirectory(file);
		}
	}

	@Override
	public boolean exists(FsItem newFile)
	{
		return asFile(newFile).exists();
	}

	private LocalFsItem fromFile(File file)
	{
		return new LocalFsItem(this, file);
	}

	@Override
	public FsItem fromPath(String relativePath)
	{
		return fromFile(new File(_rootDir, relativePath));
	}

	@Override
	public String getDimensions(FsItem fsi)
	{
		return null;
	}

	@Override
	public long getLastModified(FsItem fsi)
	{
		return asFile(fsi).lastModified();
	}

	@Override
	public String getMimeType(FsItem fsi)
	{
		File file = asFile(fsi);
		if (file.isDirectory())
			return "directory";

		String ext = FilenameUtils.getExtension(file.getName());
		if (ext != null && !ext.isEmpty())
		{
			String mimeType = MimeTypesUtils.getMimeType(ext);
			return mimeType == null ? MimeTypesUtils.UNKNOWN_MIME_TYPE : mimeType;
		}

		return MimeTypesUtils.UNKNOWN_MIME_TYPE;
	}

	public String getName()
	{
		return _name;
	}

	@Override
	public String getName(FsItem fsi)
	{
		return asFile(fsi).getName();
	}

	@Override
	public FsItem getParent(FsItem fsi)
	{
		return fromFile(asFile(fsi).getParentFile());
	}

	@Override
	public String getPath(FsItem fsi) throws IOException
	{
		String fullPath = asFile(fsi).getCanonicalPath();
		String rootPath = _rootDir.getCanonicalPath();
		String relativePath = fullPath.substring(rootPath.length());
		return relativePath.replace('\\', '/');
	}

	@Override
	public FsItem getRoot()
	{
		return fromFile(_rootDir);
	}

	public File getRootDir()
	{
		return _rootDir;
	}

	@Override
	public long getSize(FsItem fsi) throws IOException
	{
		if (isFolder(fsi))
		{
			// This recursively walks down the tree
			Path folder = asFile(fsi).toPath();
			FileSizeFileVisitor visitor = new FileSizeFileVisitor();
			Files.walkFileTree(folder, visitor);
			return visitor.getTotalSize();
		}
		else
		{
			return asFile(fsi).length();
		}
	}

	@Override
	public String getThumbnailFileName(FsItem fsi)
	{
		return null;
	}

	@Override
	public boolean hasChildFolder(FsItem fsi)
	{
		return asFile(fsi).isDirectory() && asFile(fsi).listFiles(new FileFilter()
		{

			@Override
			public boolean accept(File arg0)
			{
				return arg0.isDirectory();
			}
		}).length > 0;
	}

	@Override
	public boolean isFolder(FsItem fsi)
	{
		return asFile(fsi).isDirectory();
	}

	@Override
	public boolean isRoot(FsItem fsi)
	{
		return _rootDir == asFile(fsi);
	}

	@Override
	public FsItem[] listChildren(FsItem fsi)
	{
		List<FsItem> list = new ArrayList<FsItem>();
		File[] cs = asFile(fsi).listFiles();
		if (cs == null)
		{
			return new FsItem[0];
		}

		for (File c : cs)
		{
			list.add(fromFile(c));
		}

		return list.toArray(new FsItem[0]);
	}

	@Override
	public InputStream openInputStream(FsItem fsi) throws IOException
	{
		return new FileInputStream(asFile(fsi));
	}

	@Override
	public OutputStream openOutputStream(FsItem fsi) throws IOException
	{
		return new FileOutputStream(asFile(fsi));
	}

	@Override
	public void rename(FsItem src, FsItem dst) throws IOException
	{
		asFile(src).renameTo(asFile(dst));
	}

	@Override
	public String getURL(FsItem f)
	{
		// We are just happy to not supply a custom URL.
		return null;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public void setRootDir(File rootDir)
	{
		if (!rootDir.exists())
		{
			rootDir.mkdirs();
		}

		_rootDir = rootDir;
	}

	/**
	 * Used to calculate total file size when walking the tree.
	 */
	private static class FileSizeFileVisitor extends SimpleFileVisitor<Path> {

		private long totalSize;

		@Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
            totalSize += file.toFile().length();
            return FileVisitResult.CONTINUE;
        }

		public long getTotalSize()
		{
			return totalSize;
		}

	}
}
