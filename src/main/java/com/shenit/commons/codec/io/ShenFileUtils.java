package com.shenit.commons.codec.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File utils
 * @author jiangnan
 *
 */
public final class ShenFileUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ShenFileUtils.class);
	/**
	 * Get or create file if needed
	 * @param paths Paths to join a file
	 * @return
	 */
	public static File createFileIfNeeded(String... paths){
		File file = FileUtils.getFile(paths);
		if(!file.exists()) {
			if(file.getParentFile() != null) file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				LOG.warn("[createFileIfNeeded] Create new file -> {} failed due to exception.",file,e);
				return null;
			}
		}
		return file;
	}
}
