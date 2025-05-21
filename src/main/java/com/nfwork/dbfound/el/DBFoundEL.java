package com.nfwork.dbfound.el;

import java.lang.reflect.Array;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;

public class DBFoundEL extends PropertyTransfer{

	public static Object getData(String express, Object root, Map<String, Object> elCache) {

		String childExpress = null;
		String name ;
		Object currentObject;

		int last = express.lastIndexOf(".");

		if(last > -1){
			childExpress = express.substring(0,last).trim();
			name = express.substring(last+1).trim();

			currentObject = elCache.get(childExpress);
			if(currentObject == null){
				currentObject = getData(childExpress, root, elCache);
				if(!isSampleObject(currentObject)) {
					elCache.put(childExpress, currentObject);
				}
			}
		}else{
			currentObject = root;
			name = express.trim();
		}

		if (currentObject == null) {
			return null;
		}

		Object value;
		List<Integer> indexList = findIndex(name);
		if (indexList != null) {
			name = name.substring(0, name.indexOf("["));
			String cacheName = childExpress == null ? name : childExpress+ "." + name;
			value = elCache.get(cacheName);

			if(value == null){
				value = getDataByProperty(currentObject, name);
				if (value == null) {
					return null;
				}

				if(value instanceof Collection){
					if(!(value instanceof ArrayList)){
						value = ((Collection<?>)value).toArray();
					}
					elCache.put(cacheName,value);
				}
			}

			for(int index : indexList) {
				value = getDataByIndex(index, value);
				if (value == null) {
					return null;
				}
			}
		} else {
			value = getDataByProperty(currentObject, name);
		}

		return value;
	}

	public static Object getData(String express, Object root) {

		Object currentObject = root;
		if (express == null) {
			return null;
		}

		String[] d;
		if(express.contains(".")) {
			d = express.split("\\.");
		}else{
			d = new String[]{express};
		}

		for (int i = 0; i < d.length; i++) {
			if (currentObject == null) {
				return null;
			}
			String currentExpress = d[i].trim();
			List<Integer> indexList = findIndex(currentExpress);
			if (indexList != null) {
				currentExpress = currentExpress.substring(0, currentExpress.indexOf("["));
			}
			// 计算当前对象
			Object nextObject = getDataByProperty(currentObject, currentExpress);

			if (nextObject == null) {
				return null;
			}

			if (indexList != null) {
				for(int index : indexList) {
					nextObject = getDataByIndex(index, nextObject);
					if (nextObject == null) {
						return null;
					}
				}
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

			int index = -1;
			List<Integer> indexList = findIndex(exp);
			if(indexList != null){
				if(indexList.size()==1){
					index = indexList.get(0);
				}else{
					throw new DBFoundRuntimeException("dbfoundEl setData failed, express: " +express +" is not supported");
				}
			}
			if (index != -1) {
				exp = exp.substring(0, exp.indexOf("["));
			}

			if (i == expArray.length - 1) {
				if (index == -1) {
					setDataByProperty(currentObj, exp, value);
				} else {
					nextObj = getDataByProperty(currentObj, exp);
					if (nextObj == null) {
						nextObj = new ArrayList<>();
						setDataByProperty(currentObj, exp, nextObj);
					}
					if (nextObj instanceof ArrayList) {
						List<Object> list = (List) nextObj;
						if (list.size() > index) {
							list.set(index, value);
						} else {
							for (int j = list.size(); j < index; j++) {
								list.add(null);
							}
							list.add(value);
						}
					} else if (DataUtil.isArray(nextObj)) {
						Array.set(nextObj, index, value);
					} else {
						throw new DBFoundRuntimeException("dbfoundEl setData failed, can not set array data into " + nextObj.getClass());
					}
				}
				return;
			}

			nextObj = getDataByProperty(currentObj, exp);

			if (index > -1) {
				if(nextObj == null){
					nextObj = new ArrayList<>();
					setDataByProperty(currentObj, exp, nextObj);
				}
				if (nextObj instanceof ArrayList) {
					List<Object> list = (List) nextObj;
					if (list.size() > index) {
						currentObj = list.get(index);
						if (currentObj == null) {
							currentObj = new HashMap<>();
							list.set(index, currentObj);
						}
					} else {
						for (int j = list.size(); j < index; j++) {
							list.add(null);
						}
						currentObj = new HashMap<>();
						list.add(currentObj);
					}
				} else if (DataUtil.isArray(nextObj)) {
					currentObj = Array.get(nextObj, index);
				} else {
					throw new RuntimeException("can not set array data into " + nextObj.getClass());
				}
			} else {
				if(nextObj == null) {
					nextObj = new HashMap<String, Object>();
					setDataByProperty(currentObj, exp, nextObj);
				}
				currentObj = nextObj;
			}
		}
	}

	public static Object getDataByIndex(int index,Object object){
		if (object instanceof ArrayList) {
			List<?> l = (List<?>) object;
			if (index < l.size()) {
				return l.get(index);
			}
		} else if (DataUtil.isArray(object)) {
			if(index < Array.getLength(object)) {
				return Array.get(object, index);
			}
		} else if (object instanceof Collection) {
			LogUtil.warn("dbfound el in handling " + object.getClass() + " is relatively poor, recommend change to ArrayList or Array");
			Collection<?> s = (Collection<?>) object;
			if (index < s.size()) {
				for (Object o : s) {
					if (index == 0) {
						return o;
					}
					index--;
				}
			}
		}
		return null;
	}

	public static Object getDataByProperty(Object currentObj, String property){

		if(currentObj instanceof Map){
			Map<?,?> currentMap = (Map<?,?>) currentObj;
			Object result = currentMap.get(property);
			if(result == null){
				if(property.contains("_") && !currentMap.containsKey(property)){
					property = underscoreToCamelCase(property);
					result = currentMap.get(property);
				}
			}
			return result;
		} else{
			if("value".equals(property)){
				if(isSampleObject(currentObj)){
					return currentObj;
				}
				if(DataUtil.getDataLength(currentObj) != -1){
					return currentObj;
				}
			}else if("size".equals(property)){
				int size = DataUtil.getDataLength(currentObj);
				if(size != -1){
					return size;
				}
			}else if("length".equals(property)){
				if(currentObj instanceof String){
					return ((String) currentObj).length();
				}
			}
			try {
				Reflector reflector = Reflector.forClass(currentObj.getClass());
				property = reflector.getFieldName(property);
				if (reflector.hasGetter(property)) {
					return reflector.getGetInvoker(property).invoke(currentObj, null);
				}
				if (property.contains("_")) {
					property = underscoreToCamelCase(property);
					if (reflector.hasGetter(property)) {
						return reflector.getGetInvoker(property).invoke(currentObj, null);
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static void setDataByProperty(Object object, String property, Object value){
		if(object instanceof Map){
			Map currentMap = (Map) object;
			currentMap.put(property, value);
		}else{
			setBeanProperty(object,property,value);
		}
	}

	public static void setBeanProperty(Object object, String property, Object value){
		Reflector reflector = Reflector.forClass(object.getClass());
		property = reflector.getFieldName(property);
		if(reflector.hasSetter(property)) {
			reflector.setProperty(object, property, value);
		}else{
			if(property.contains("_")){
				property = underscoreToCamelCase(property);
				if(reflector.hasSetter(property)) {
					reflector.setProperty(object, property, value);
				}
			}
		}
	}

	private static  boolean isSampleObject(Object object){
		return object instanceof Number || object instanceof Date || object instanceof Temporal
				|| object instanceof String || object instanceof Enum || object instanceof Boolean;
	}

	private final static Pattern p = Pattern.compile("\\[[0-9 ]+]");

	private static List<Integer> findIndex(String value) {
		if(!value.contains("[")){
			return null;
		}
		List<Integer> list = null;
		Matcher m = p.matcher(value);
		while (m.find()) {
			String text = m.group();
			text = text.substring(1, text.length() - 1);
			if(list == null){
				list = new ArrayList<>();
			}
			list.add(Integer.parseInt(text.trim()));
		}
		return list;
	}
}

