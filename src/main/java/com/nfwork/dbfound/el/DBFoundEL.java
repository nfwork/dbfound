package com.nfwork.dbfound.el;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.MethodUtils;

import com.nfwork.dbfound.util.LogUtil;

public class DBFoundEL {

	public static Object getData(String express, Map<String, Object> root) {
		if (express == null) {
			return null;
		}
		String d[] = express.split("\\.");
		Object currentObject = root;
		Object object = getData(d, currentObject);
		return object;
	}

	@SuppressWarnings("unchecked")
	private static Object getData(String d[], Object currentObject) {
		try {
			for (int i = 0; i < d.length; i++) {
				if (currentObject == null) {
					return null;
				}
				String currentExpree = d[i].trim();
				int index = findIndex(currentExpree);
				if (index != -1) {
					currentExpree = currentExpree.substring(0, currentExpree.indexOf("["));
				}
				// 计算当前对象
				Object object = null;
				if(isSampleObject(currentObject)){
					if("value".equals(currentExpree)){
						return currentObject;
					}
				}else if (currentObject instanceof Map) {
					Map m = (Map) currentObject;
					object = m.get(currentExpree);
				} else {
					String methodEndName = currentExpree.substring(0, 1)
							.toUpperCase()
							+ currentExpree.substring(1);
					try {
						String methodName = "get" + methodEndName;
						object = MethodUtils.invokeMethod(currentObject,
								methodName, null);
					} catch (NoSuchMethodException e) {
						try {
							String methodName = "is" + methodEndName;
							object = MethodUtils.invokeMethod(currentObject,
									methodName, null);
						} catch (NoSuchMethodException e2) {
							return null;
						}
					}
				}

				if (index != -1) {
					if (object instanceof List) {
						List l = (List) object;
						object = l.get(index);
					} else if (object instanceof Set) {
						Set s = (Set) object;

						Iterator iterator = s.iterator();
						while (iterator.hasNext()) {
							if (index == 0) {
								object = iterator.next();
								break;
							}
							index--;
						}
					} else if (object instanceof Object[]) {
						Object[] objects = (Object[]) object;
						object = objects[index];
					}
				}

				// 判断是否终止
				if (i == d.length - 1) {
					if (object == null) {
						return null;
					}
					return object;
				} else {
					currentObject = object;
				}
			}
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
		return null;
	}

	private static  boolean isSampleObject(Object object){
		return object instanceof Integer || object instanceof Long || object instanceof Double || object instanceof Date
				|| object instanceof Float || object instanceof String || object instanceof Enum;
	}

	private static int findIndex(String value) {
		Pattern p = Pattern.compile("\\[[0123456789 ]+\\]");
		Matcher m = p.matcher(value);
		while (m.find()) {
			String text = m.group();
			text = text.substring(1, text.length() - 1);
			int result = Integer.parseInt(text.trim());
			return result;
		}
		return -1;
	}
}
