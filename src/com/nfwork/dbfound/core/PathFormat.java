package com.nfwork.dbfound.core;

public class PathFormat {

	public static String format(String path) {
		if (path == null) {
			return null;
		} else {
			return path.replace("\\", "/");
		}
	}
}
