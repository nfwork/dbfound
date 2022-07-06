package com.nfwork.dbfound.el;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import org.apache.commons.beanutils.BeanUtils;

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
				Object nextObject = getNextObject(currentObject, currentExpree);

				if (index != -1 && nextObject != null) {
					nextObject = getByIndex(index, nextObject);
				}

				// 判断是否终止
				if (i == d.length - 1) {
					return nextObject;
				} else {
					currentObject = nextObject;
				}
			}
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
		return null;
	}

	public static void setData(String express, Map<String, Object> root, Object value){
		if (express == null) {
			return ;
		}
		if(!ELEngine.isAbsolutePath(express)){
			throw new DBFoundRuntimeException("dbfound el set data failed, express must start with param,request,outParam,session,header,cookie");
		}

		String[] expArray = express.split("\\.");
		Object currentObj = root;
		Object nextObj = null;
		for (int i =0;i<expArray.length;i++){
			String exp = expArray[i].trim();

			if( i== expArray.length -1){
				setNextObject(currentObj,exp,value);
				return;
			}

			int index = findIndex(exp);
			if (index != -1) {
				exp = exp.substring(0, exp.indexOf("["));
			}

			nextObj = getNextObject(currentObj,exp);

			if (index != -1 && nextObj != null) {
				nextObj = getByIndex(index, nextObj);
			}

			if(nextObj == null){
				nextObj = new HashMap<String, Object>();
				setNextObject(currentObj,exp,nextObj);
			}

			currentObj = nextObj;
		}
	}

	private static Object getByIndex(int index,Object object){
		if (object instanceof List) {
			List l = (List) object;
			if (index < l.size()) {
				object = l.get(index);
			}
		} else if (object instanceof Set) {
			Set s = (Set) object;
			if (index < s.size()) {
				Iterator iterator = s.iterator();
				while (iterator.hasNext()) {
					if (index == 0) {
						object = iterator.next();
						break;
					}
					index--;
				}
			}
		} else if (object instanceof Object[]) {
			Object[] objects = (Object[]) object;
			if (index < objects.length) {
				object = objects[index];
			}
		}

		return object;
	}

	private static Object getNextObject(Object currentObj,String name){
		if(currentObj instanceof Map){
			Map currentMap = (Map) currentObj;
			return currentMap.get(name);
		} else if(isSampleObject(currentObj)){
			if("value".equals(name)){
				return currentObj;
			}else{
				return null;
			}
		}else{
			try {
				return BeanUtils.getProperty(currentObj, name);
			} catch (Exception e) {
				throw new DBFoundRuntimeException("dbfound el get data failed, " + e.getMessage(), e);
			}
		}
	}

	private static void setNextObject(Object currentObj,String name,Object nextObj){
		if(currentObj instanceof Map){
			Map currentMap = (Map) currentObj;
			currentMap.put(name, nextObj);
		}else{
			try {
				BeanUtils.setProperty(currentObj, name, nextObj);
			} catch (Exception e) {
				throw new DBFoundRuntimeException("set context data failed, " + e.getMessage(), e);
			}
		}
	}

	private static  boolean isSampleObject(Object object){
		return object instanceof Integer || object instanceof Long || object instanceof Double || object instanceof Float
				|| object instanceof Date || object instanceof String || object instanceof Enum || object instanceof Boolean;
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
