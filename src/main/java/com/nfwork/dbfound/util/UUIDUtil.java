package com.nfwork.dbfound.util;

import java.util.Random;

/**
 * 参数随机数key值 UUID
 * @author John
 *
 */
public class UUIDUtil {

	static StringBuilder builder = new StringBuilder("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	static Random r = new Random();
	static int range = builder.length();

	/**
	 * 产生一个24的随机字符串 作为dbfound的uuid
	 *
	 */
	public static String getUUID() {
		return getRandomString(24);
	}

	/**
	 * 参数数据字符串
	 *
	 */
	public static String getRandomString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(builder.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}
}
