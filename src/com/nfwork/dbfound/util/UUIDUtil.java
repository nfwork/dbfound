package com.nfwork.dbfound.util;

import java.util.Random;

/**
 * 参数随机数key值 UUID
 * 
 * @param length
 * @return
 */
public class UUIDUtil {

	static StringBuffer buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	static Random r = new Random();
	static int range = buffer.length();

	/**
	 * 产生一个24的随机字符串 作为dbfound的uuid
	 * 
	 * @return
	 */
	public static String getUUID() {
		return getRandomString(24);
	}

	/**
	 * 参数数据字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}
}
