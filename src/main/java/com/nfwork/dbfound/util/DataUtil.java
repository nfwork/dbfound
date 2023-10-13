package com.nfwork.dbfound.util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundPackageException;

/**
 * 数据转化工具类
 * 
 * @author John 2014年4月29日17:52:11
 */
public class DataUtil {

	public static boolean isNull(Object value) {
		return value == null || value.equals("");
	}

	public static boolean isNotNull(Object value) {
		return !isNull(value);
	}

	public static <T> T convertMapToBean(Map map, Class<T> clazz){
		if (map != null) {
			Set<Entry<String,Object>> columns = map.entrySet();
			T obj;
			try {
				obj = clazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new DBFoundPackageException(e.getMessage(),e);
			}
			for (Entry<String,Object> entry : columns) {
				if(entry.getValue() != null) {
					DBFoundEL.setDataByProperty(obj, entry.getKey(), entry.getValue());
				}
			}
			return obj;
		}
		return null;
	}

	public static Short shortValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Short) {
			return (Short) o;
		} else {
			return Short.parseShort(o.toString());
		}
	}

	public static Byte byteValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Byte) {
			return (Byte) o;
		} else {
			return Byte.parseByte(o.toString());
		}
	}

	public static Date dateValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Date) {
			return (Date) o;
		} else {
			String value = o.toString().trim();
			SimpleDateFormat format;
			if (value.length() == 10) {
				format = new SimpleDateFormat(DBFoundConfig.getDateFormat());
			} else {
				format = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
			}
			try {
				return format.parse(o.toString());
			} catch (ParseException e) {
				throw new DBFoundPackageException(e);
			}
		}
	}

	public static Long longValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Long) {
			return (Long) o;
		} else {
			return Long.parseLong(o.toString());
		}
	}

	public static Integer intValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Integer) {
			return (Integer) o;
		} else {
			return Integer.parseInt(o.toString());
		}
	}

	public static Double doubleValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Double) {
			return (Double) o;
		} else {
			return Double.parseDouble(o.toString());
		}
	}

	public static Float floatValue(Object o) {
		if (isNull(o)) {
			return null;
		} else if (o instanceof Float) {
			return (Float) o;
		} else {
			return Float.parseFloat(o.toString());
		}
	}

	public static String stringValue(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof String) {
			return (String) o;
		} else {
			return o.toString();
		}
	}

	public static int getDataLength(Object data){
		if (data instanceof Collection) {
			return ((Collection<?>)data).size();
		} else if (isArray(data)) {
			return Array.getLength( data );
		} else {
			return -1;
		}
	}

	public static Object getArrayDataByIndex(Object data, int index){
		return Array.get(data,index);
	}

	public static boolean isArray(Object obj) {
		return obj != null && obj.getClass().isArray();
	}
}
