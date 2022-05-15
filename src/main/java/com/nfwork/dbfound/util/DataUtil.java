package com.nfwork.dbfound.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.reflector.DefaultObjectFactory;
import com.nfwork.dbfound.model.reflector.ObjectFactory;
import com.nfwork.dbfound.model.reflector.Reflector;

/**
 * 数据转化工具类
 * 
 * @author John 2014年4月29日17:52:11
 */
public class DataUtil {

	public static boolean isNull(Object value) {
		if (value == null || value.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotNull(Object value) {
		return !isNull(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertMapToBean(Map map, Class<T> clazz) {
		if (map != null) {
			Set<Entry> columns = map.entrySet();
			Reflector reflector = Reflector.forClass(clazz);

			ObjectFactory objectFactory = new DefaultObjectFactory();
			T obj = objectFactory.create(clazz);
			for (Entry entry : columns) {
				String name = DataUtil.stringValue(entry.getKey());
				String propertyname = reflector.getFieldName(name);
				if (reflector.hasSetter(propertyname)) {
					Object columnvalue = entry.getValue();
					if (columnvalue != null) {
						Class<?> fieldtype = reflector.getSetterType(propertyname);
						if (fieldtype.equals(Integer.class) || fieldtype.equals(int.class)) {
							columnvalue = DataUtil.intValue(columnvalue);
						} else if (fieldtype.equals(Long.class) || fieldtype.equals(long.class)) {
							columnvalue = DataUtil.longValue(columnvalue);
						} else if (fieldtype.equals(Float.class) || fieldtype.equals(float.class)) {
							columnvalue = DataUtil.floatValue(columnvalue);
						} else if (fieldtype.equals(Double.class) || fieldtype.equals(double.class)) {
							columnvalue = DataUtil.doubleValue(columnvalue);
						} else if (fieldtype.equals(Date.class) || fieldtype.equals(java.sql.Date.class)) {
							columnvalue = DataUtil.dateValue(columnvalue);
						} else if (fieldtype.equals(String.class)) {
							columnvalue = DataUtil.stringValue(columnvalue);
						} else if (fieldtype.equals(Short.class) || fieldtype.equals(short.class)) {
							columnvalue = DataUtil.shortValue(columnvalue);
						} else if (fieldtype.equals(Byte.class) || fieldtype.equals(byte.class)) {
							columnvalue = DataUtil.byteValue(columnvalue);
						}
						try {
							reflector.getSetInvoker(propertyname).invoke(obj, new Object[] { columnvalue });
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
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
}
