package com.nfwork.dbfound.el;

import java.lang.reflect.Array;
import java.time.temporal.Temporal;
import java.util.*;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.reflector.Invoker;
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

		int length = express.length();
		while (length > 0 && express.charAt(length - 1) == '.') {
			length--;
		}
		if (length == 0) {
			if (express.isEmpty()) {
				return getDataByProperty(currentObject, "");
			}
			return null;
		}

		int start = 0;
		while (start < length) {
			if (currentObject == null) {
				return null;
			}
			int end = express.indexOf('.', start);
			boolean isLast;
			if (end == -1 || end >= length) {
				end = length;
				isLast = true;
			} else {
				isLast = false;
			}
			String currentExpress = express.substring(start, end).trim();
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
			if (isLast) {
				return nextObject;
			} else {
				currentObject = nextObject;
				start = end + 1;
			}
		}

		return null;
	}

	public static void setData(String express, Map<String, Object> root, Object value){
		if (express == null) {
			return;
		}

		int length = express.length();
		while (length > 0 && express.charAt(length - 1) == '.') {
			length--;
		}
		if (length == 0) {
			if (express.isEmpty()) {
				setDataByProperty(root, "", value);
			}
			return;
		}

		Object currentObj = root;
		Object nextObj;
		int start = 0;
		while (start < length) {
			int dot = express.indexOf('.', start);
			boolean isLast;
			int end;
			if (dot == -1 || dot >= length) {
				end = length;
				isLast = true;
			} else {
				end = dot;
				isLast = false;
			}
			String exp = express.substring(start, end).trim();

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

			if (isLast) {
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
						throw new DBFoundRuntimeException("dbfoundEl setData failed, cannot set array data into " + nextObj.getClass());
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
					throw new RuntimeException("cannot set array data into " + nextObj.getClass());
				}
			} else {
				if(nextObj == null) {
					nextObj = new HashMap<String, Object>();
					setDataByProperty(currentObj, exp, nextObj);
				}
				currentObj = nextObj;
			}

			start = end + 1;
		}
	}

	public static Object getDataByIndex(int index,Object object){
		if (object instanceof List) {
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
			Object value = currentMap.get(property);
			if (value != null || currentMap.containsKey(property)) {
				return value;
			}
			if (property.contains("_")) {
				property = underscoreToCamelCase(property);
				return currentMap.get(property);
			}
			return null;
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
				Map<String, Invoker> getMethods = reflector.getGetMethods();
				Invoker invoker = getMethods.get(property);
				if (invoker != null) {
					return invoker.invoke(currentObj, null);
				}
				if (property.contains("_")) {
					property = underscoreToCamelCase(property);
					invoker = getMethods.get(property);
					if (invoker != null) {
						return invoker.invoke(currentObj, null);
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

	private static List<Integer> findIndex(String value) {
		int start = value.indexOf("[");
		if(start == -1){
			return null;
		}
		List<Integer> list = null;
		while (start != -1) {
			int end = value.indexOf("]", start + 1);
			if(end == -1){
				break;
			}
			int number = 0;
			boolean hasNumber = false;
			boolean valid = true;
			for(int i = start + 1; i < end; i++){
				char c = value.charAt(i);
				if(c == ' '){
					continue;
				}
				if(c >= '0' && c <= '9'){
					hasNumber = true;
					int digit = c - '0';
					if(number > (Integer.MAX_VALUE - digit) / 10){
						throw new NumberFormatException("For input string: \"" + value.substring(start + 1, end).trim() + "\"");
					}
					number = number * 10 + digit;
				}else{
					valid = false;
					break;
				}
			}
			if(!valid){
				start = value.indexOf("[", end + 1);
				continue;
			}
			if(!hasNumber){
				if(end > start + 1){
					throw new NumberFormatException("For input string: \"" + value.substring(start + 1, end).trim() + "\"");
				}
				start = value.indexOf("[", end + 1);
				continue;
			}
			if(list == null){
				list = new ArrayList<>();
			}
			list.add(number);
			start = value.indexOf("[", end + 1);
		}
		return list;
	}
}

