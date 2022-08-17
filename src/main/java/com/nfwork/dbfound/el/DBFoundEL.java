package com.nfwork.dbfound.el;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;

public class DBFoundEL {

	public static Object getData(String express, Object root, Map<String, Object> elCache) {

		String childExpress;
		String name ;
		Object currentObject;

		int last = express.lastIndexOf(".");

		if(last > -1){
			childExpress = express.substring(0,last).trim();
			name = express.substring(last+1).trim();

			currentObject = elCache.get(childExpress);
			if(currentObject == null){
				currentObject = getData(childExpress, root);
				elCache.put(childExpress, currentObject);
			}
		}else{
			currentObject = root;
			name = express.trim();
		}

		int index = findIndex(name);
		if (index > -1) {
			name = name.substring(0, name.indexOf("["));
		}
		Object value = getNextObject(currentObject,name);
		if( index > -1){
			value = getDataByIndex(index,value);
		}
		return value;
	}

	public static Object getData(String express, Object root) {

		Object currentObject = root;
		if (express == null) {
			return null;
		}
		String[] d = express.split("\\.");

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
				nextObject = getDataByIndex(index, nextObject);
			}

			// 判断是否终止
			if (i == d.length - 1) {
				return nextObject;
			} else {
				currentObject = nextObject;
			}
		}

		return null;
	}

	public static void setData(String express, Map<String, Object> root, Object value){
		if (express == null) {
			return ;
		}
		String[] expArray = express.split("\\.");

		Object currentObj = root;
		Object nextObj ;
		for (int i =0;i<expArray.length;i++){
			String exp = expArray[i].trim();

			int index = findIndex(exp);
			if (index != -1) {
				exp = exp.substring(0, exp.indexOf("["));
			}

			if( i== expArray.length -1){
				setNextObject(currentObj,exp,value);
				return;
			}

			nextObj = getNextObject(currentObj,exp);

			if (index != -1 && nextObj != null) {
				nextObj = getDataByIndex(index, nextObj);
			}

			if(nextObj == null){
				nextObj = new HashMap<String, Object>();
				setNextObject(currentObj,exp,nextObj);
			}

			currentObj = nextObj;
		}
	}

	public static Object getDataByIndex(int index,Object object){
		if (object instanceof List) {
			List l = (List) object;
			if (index < l.size()) {
				object = l.get(index);
			}
		} else if (object instanceof Set) {
			Set s = (Set) object;
			if (index < s.size()) {
				for (Object o : s) {
					if (index == 0) {
						object = o;
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
		}  else if (object instanceof int[]) {
			int[] objects = (int[]) object;
			if (index < objects.length) {
				object = objects[index];
			}
		} else if (object instanceof long[]) {
			long[] objects = (long[]) object;
			if (index < objects.length) {
				object = objects[index];
			}
		} else if (object instanceof double[]) {
			double[] objects = (double[]) object;
			if (index < objects.length) {
				object = objects[index];
			}
		} else if (object instanceof float[]) {
			float[] objects = (float[]) object;
			if (index < objects.length) {
				object = objects[index];
			}
		}
		return object;
	}

	public static Object getDataByProperty(String property,Object object){
		return getNextObject(object,property);
	}

	private static Object getNextObject(Object currentObj,String name){
		if(currentObj instanceof Map){
			Map currentMap = (Map) currentObj;
			return currentMap.get(name);
		} else{
			if("value".equals(name)){
				if(isSampleObject(currentObj)){
					return currentObj;
				}
				if(DataUtil.getDataLength(currentObj) != -1){
					return currentObj;
				}
			}else if("size".equals(name)){
				int size = DataUtil.getDataLength(currentObj);
				if(size != -1){
					return size;
				}
			}
			try {
				Reflector reflector = Reflector.forClass(currentObj.getClass());
				name = reflector.getFieldName(name);
				if (reflector.hasGetter(name)) {
					return reflector.getGetInvoker(name).invoke(currentObj, null);
				}
				if (name.contains("_")) {
					name = StringUtil.underscoreToCamelCase(name);
					if (reflector.hasGetter(name)) {
						return reflector.getGetInvoker(name).invoke(currentObj, null);
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}
	}

	private static void setNextObject(Object currentObj,String name,Object nextObj){
		if(currentObj instanceof Map){
			Map currentMap = (Map) currentObj;
			currentMap.put(name, nextObj);
		}else{
			try {
				Reflector reflector = Reflector.forClass(currentObj.getClass());
				name = reflector.getFieldName(name);
				if(!reflector.hasSetter(name)) {
					if(name.contains("_")){
						name = StringUtil.underscoreToCamelCase(name);
					}
				}
				if(reflector.hasGetter(name)) {
					Class<?> fieldType = reflector.getSetterType(name);
					if (nextObj!=null && Enum.class.isAssignableFrom(fieldType) && !(nextObj instanceof Enum)) {
						nextObj = EnumHandlerFactory.getEnumHandler(fieldType).locateEnum(nextObj.toString());
					}
					BeanUtils.setProperty(currentObj, name, nextObj);
				}
			} catch (Exception e) {
				throw new DBFoundRuntimeException("set context data failed, " + e.getMessage(), e);
			}
		}
	}

	private static  boolean isSampleObject(Object object){
		return object instanceof Number || object instanceof Date || object instanceof String
				|| object instanceof Enum || object instanceof Boolean;
	}

	private final static Pattern p = Pattern.compile("\\[[0123456789 ]+]");

	private static int findIndex(String value) {
		Matcher m = p.matcher(value);
		if (m.find()) {
			String text = m.group();
			text = text.substring(1, text.length() - 1);
			return Integer.parseInt(text.trim());
		}
		return -1;
	}
}

