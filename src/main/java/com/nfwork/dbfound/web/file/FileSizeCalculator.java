package com.nfwork.dbfound.web.file;

public class FileSizeCalculator {
	static String[] sizeLabel = { "B", "KB", "MB", "GB" };

	public static String getFileSize(double size) {
		return calFileSize(size);
	}

	private static String calFileSize(double size) {
		for (String s : sizeLabel) {
			if (size < 1024) {
				String value = "" + round(size);
				if (value.endsWith(".0")) {
					value = value.substring(0, value.length() - 2);
				}
				return value + s;
			}
			size = size / 1024;
		}
		return "大于1024G";
	}

	private static double round(double number) {
		return Math.round(number * Math.pow(10, 2)) / Math.pow(10, 2);
	}
}
