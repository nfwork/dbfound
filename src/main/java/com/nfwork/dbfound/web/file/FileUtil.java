package com.nfwork.dbfound.web.file;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;

import java.io.File;

public class FileUtil{

	private static String baseFolder;

	private static final Object lockObj = new Object();

	public static String getDownLoadFolder(String value) {
		if (baseFolder == null) {
			synchronized (lockObj) {
				baseFolder = System.getProperty("java.io.tmpdir");
			}
		}
		if (value == null) {
			return null;
		} else if (!value.startsWith("/") && !value.contains(":")) {
			return baseFolder + "/" + value;
		} else {
			return value;
		}
	}
	
	public static void init(String fold) {
		baseFolder = fold;
	}

	public static File getUploadFolder(String foldName) {
		if (baseFolder == null) {
			synchronized (lockObj) {
				baseFolder = System.getProperty("java.io.tmpdir");
			}
		}
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
