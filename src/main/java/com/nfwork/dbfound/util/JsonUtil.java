package com.nfwork.dbfound.util;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.el.PropertyTransfer;
import com.nfwork.dbfound.json.JSONArray;
import com.nfwork.dbfound.json.JSONNull;
import com.nfwork.dbfound.json.JSONObject;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.enums.EnumTypeHandler;
import com.nfwork.dbfound.model.reflector.Reflector;

/**
 * Json数据处理
 * 
 * @author Administrator
 * 
 */
public class JsonUtil extends PropertyTransfer {

	/**
	 * 将一个实体类对象转换成Json数据格式
	 * 
	 * @param bean
	 *            需要转换的实体类对象
	 * @return 转换后的Json格式字符串
	 */
	public static String beanToJson(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");

		Reflector reflector = Reflector.forClass(bean.getClass());
		List<String> properties = reflector.getSerializableFieldList();

		if(properties != null && properties.size() > 0){
			for (String property : properties){
				try {
					String name;
					if(DBFoundConfig.isCamelCaseToUnderscore() && !(bean instanceof ResponseObject)){
						name = camelCaseToUnderscore(property);
					}else{
						name = property;
					}
					name = objectToJson(name);
					String value = objectToJson(reflector.getGetInvoker(property).invoke(bean,null));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception ignored) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		}else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * 将一个List对象转换成Json数据格式返回
	 * 
	 * @param list
	 *            需要进行转换的List对象
	 * @return 转换后的Json数据格式字符串
	 */
	public static String listToJson(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * 将一个对象数组转换成Json数据格式返回
	 * 
	 * @param array
	 *            需要进行转换的数组对象
	 * @return 转换后的Json数据格式字符串
	 */
	public static String arrayToJson(Object array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null) {
			for (int i =0; i < Array.getLength(array); i++) {
				json.append(objectToJson(Array.get(array,i)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * 将一个Map对象转换成Json数据格式返回
	 * 
	 * @param map
	 *            需要进行转换的Map对象
	 * @return 转换后的Json数据格式字符串
	 */
	public static String mapToJson(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(objectToJson(key));
				json.append(":");
				json.append(objectToJson(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * 将一个Set对象转换成Json数据格式返回
	 * 
	 * @param set
	 *            需要进行转换的Set对象
	 * @return 转换后的Json数据格式字符串
	 */
	public static String setToJson(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	private static String numberToJson(Number number) {
		String s = number.toString();
		if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
			int index = s.length()-1;
			while (s.charAt(index) == '0') {
				index--;
			}
			if (s.charAt(index) == '.') {
				index--;
			}
			s = s.substring(0, index+1);
		}
		return s;
	}

	private static String booleanToJson(Boolean bool) {
		return bool.toString();
	}

	private static String nullToJson() {
		return "";
	}

	public static String stringToJson(String s) {
		if (s == null) {
			return nullToJson();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	private static String objectToJson(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("null");
		} else if (obj instanceof Number) {
			json.append(numberToJson((Number) obj));
		} else if (obj instanceof String) {
			json.append("\"").append(stringToJson(obj.toString())).append("\"");
		} else if (obj instanceof Date) {
			json.append("\"").append(LocalDateUtil.formatDate((Date)obj)).append("\"");
		} else if (obj instanceof Boolean) {
			json.append(booleanToJson((Boolean) obj));
		} else if(obj instanceof Temporal) {
			String value = LocalDateUtil.formatTemporal((Temporal) obj);
			json.append("\"").append(value).append("\"");
		} else if (obj instanceof List) {
			json.append(listToJson((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(mapToJson((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(setToJson((Set<?>) obj));
		} else if (obj instanceof byte[]) {
			json.append("\"").append(obj).append("\"");
		} else if (obj.getClass().isArray()) {
			json.append(arrayToJson(obj));
		} else if (obj instanceof Enum) {
			EnumTypeHandler handler = EnumHandlerFactory.getEnumHandler(obj.getClass());
			Object value = handler.getEnumValue(obj);
			json.append(objectToJson(value));
		} else if (obj instanceof InputStream) {
			json.append("\"InputStream\"");
		} else {
			json.append(beanToJson(obj));
		}
		return json.toString();
	}

	// ============================================================================================

	/**
	 * 将Json格式的字符串转换成指定的对象返回
	 * 
	 * @param jsonString
	 *            Json格式的字符串
	 * @param pojoCalss
	 *            转换后的对象类型
	 * @return 转换后的对象
	 */
	@SuppressWarnings("unchecked")
	public static Object jsonToObject(String jsonString, Class pojoCalss) {
		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		pojo = JSONObject.toBean(jsonObject, pojoCalss);
		return pojo;
	}

	/**
	 * 将Json格式的字符串转换成对象数组返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @return 转换后的对象数组
	 */
	public static Object[] jsonToObjectArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return jsonArray.toArray();
	}

	/**
	 * 将Json格式的字符串转换成指定对象组成的List返回
	 * 
	 * @param jsonString
	 *            Json格式的字符串
	 * @param pojoClass
	 *            转换后的List中对象类型
	 * @return 转换后的List对象
	 */
	@SuppressWarnings("unchecked")
	public static List jsonToList(String jsonString, Class pojoClass) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List list = new ArrayList();
		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject, pojoClass);
			list.add(pojoValue);
		}
		return list;
	}

	/**
	 * 将Json格式的字符串转换成字符串数组返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @return 转换后的字符串数组
	 */
	public static String[] jsonToStringArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] stringArray = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

	/**
	 * 将Json格式的字符串转换成Long数组返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @return 转换后的Long数组
	 */
	public static Long[] jsonToLongArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Long[] longArray = new Long[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			longArray[i] = jsonArray.getLong(i);
		}
		return longArray;
	}

	/**
	 * 将Json格式的字符串转换成Integer数组返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @return 转换后的Integer数组
	 */
	public static Integer[] jsonToIntegerArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer[] integerArray = new Integer[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			integerArray[i] = jsonArray.getInt(i);
		}
		return integerArray;
	}

	/**
	 * 将Json格式的字符串转换成日期数组返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @param DataFormat
	 *            返回的日期格式
	 * @return 转换后的日期数组
	 */
	public static Date[] jsonToDateArray(String jsonString, String DataFormat) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Date[] dateArray = new Date[jsonArray.length()];
		String dateString;
		Date date;
		for (int i = 0; i < jsonArray.length(); i++) {
			dateString = jsonArray.getString(i);
			// date = DateUtil.parseDate(dateString, DataFormat);
			SimpleDateFormat dateformat = new SimpleDateFormat(DataFormat);
			try {
				date = dateformat.parse(dateString);
				dateArray[i] = date;
			} catch (ParseException e) {
				LogUtil.error(e.getMessage(), e);
			}
		}
		return dateArray;
	}

	/**
	 * 将Json格式的字符串转换成Double数组返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @return 转换后的Double数组
	 */
	public static Double[] jsonToDoubleArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Double[] doubleArray = new Double[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			doubleArray[i] = jsonArray.getDouble(i);
		}
		return doubleArray;
	}

	/**
	 * 将json对象装话为 list对象，list里面的值为map int string 数组
	 * 
	 * @param jsonString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List jsonToList(String jsonString) {
		Object[] objs = JsonUtil.jsonToObjectArray(jsonString);
		List maps = jsonToList(objs);
		return maps;
	}

	@SuppressWarnings("unchecked")
	private static List jsonToList(Object[] objs) {
		List maps = new ArrayList();
		for (Object obj : objs) {
			if (obj == null || obj instanceof JSONNull) {
				maps.add(null);
			} else if (obj instanceof JSONArray) {
				Object os[] = ((JSONArray) obj).toArray();
				maps.add(jsonToList(os));
			} else if (obj instanceof JSONObject) {
				JSONObject object = (JSONObject) obj;
				maps.add(jsonToMap(object));
			} else {
				maps.add(obj);
			}
		}
		return maps;
	}

	/**
	 * 将Json格式的字符串转换成Map对象返回
	 * 
	 * @param jsonString
	 *            需要进行转换的Json格式字符串
	 * @return 转换后的Map对象
	 */
	public static Map<String, Object> jsonToMap(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		return jsonToMap(jsonObject);
	}

	private static Map<String, Object> jsonToMap(JSONObject jsonObject) {
		Iterator keyIter = jsonObject.keys();
		String key;
		Object value;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		while (keyIter.hasNext()) {
			key = (String) keyIter.next();
			value = jsonObject.get(key);
			if (value == null || value instanceof JSONNull) {
				valueMap.put(key, null);
			} else if (value instanceof JSONArray) {
				Object os[] = ((JSONArray) value).toArray();
				valueMap.put(key, jsonToList(os));
			} else if (value instanceof JSONObject) {
				JSONObject object = (JSONObject) value;
				valueMap.put(key, jsonToMap(object));
			} else {
				valueMap.put(key, value);
			}
		}
		return valueMap;
	}
}
