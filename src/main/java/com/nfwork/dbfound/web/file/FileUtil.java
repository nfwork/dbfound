package com.nfwork.dbfound.web.file;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;

import java.io.File;

public class FileUtil{

	private static final String baseFolder = System.getProperty("java.io.tmpdir");

	private static final Object lockObj = new Object();

	public static String getDownLoadFolder(String value) {
		if (value == null) {
			return null;
		} else if (!value.startsWith("/") && !value.contains(":")) {
			return baseFolder + "/" + value;
		} else {
			return value;
		}
	}

	public static File getUploadFolder(String foldName) {
		if (foldName == null) {
			foldName = baseFolder;
		}else {
			foldName = baseFolder + "/" + foldName;
		}
		File fold = new File(foldName);
		if (!fold.exists()) {
			synchronized (lockObj) {
				if (!fold.exists()) {
					boolean success = fold.mkdirs();
					if(!success){
						throw new DBFoundRuntimeException("create fold failed , fold: "+ foldName);
					}
				}
			}
		}
		return fold;
	}

}
