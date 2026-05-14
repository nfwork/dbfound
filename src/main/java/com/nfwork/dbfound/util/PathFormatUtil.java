package com.nfwork.dbfound.util;

public class PathFormatUtil {

	public static String format(String path) {
		if (path == null) {
			return null;
		} else {
			return path.replace("\\", "/");
		}
	}
}
