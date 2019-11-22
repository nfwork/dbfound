package com.nfwork.dbfound.web.file;

public class FileSizeCalculator {
	static String[] sizeLabel = { "B", "KB", "MB", "GB" };

	public static String getFileSize(double size) {
		String fileSize = calFileSize(size);
		return fileSize;
	}

	private static String calFileSize(double size) {
		for (int index = 0; index < sizeLabel.length; index++) {
			if (size < 1024) {
				String value = "" + round(size, 2);
				if (value.endsWith(".0")) {
					value = value.substring(0, value.length() - 2);
				}
				return value + sizeLabel[index];
			}
			size = size / 1024;
		}
		return "大于1024G";
	}

	private static double round(double number, int count) {
		return Math.round(number * Math.pow(10, count)) / Math.pow(10, count);
	}
}
