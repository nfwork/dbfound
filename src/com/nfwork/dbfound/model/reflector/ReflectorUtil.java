package com.nfwork.dbfound.model.reflector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nfwork.dbfound.util.DataUtil;

public class ReflectorUtil {

	public static <T> List<T> parseResultList(Class<T> clazz, ResultSet rs) {
		List<T> array = new ArrayList<T>();
		try {
			if (rs != null) {
				ResultSetMetaData md = rs.getMetaData();
				int columnCount = md.getColumnCount();
				Reflector reflector = Reflector.forClass(clazz);
				while (rs.next()) {
					ObjectFactory objectFactory = new DefaultObjectFactory();
					T obj = objectFactory.create(clazz);
					for (int i = 1; i <= columnCount; i++) {
						String columname = md.getColumnLabel(i);
						if (DataUtil.isNull(columname)) {
							columname = md.getColumnName(i);
						}
						String propertyname = reflector.getFieldName(columname);
						if (reflector.hasSetter(propertyname)) {
							Object columnvalue = rs.getObject(i);
							if (columnvalue != null) {
								try {
									Class<?> fieldtype = reflector.getSetterType(propertyname);

									if (fieldtype.equals(Short.class) || fieldtype.equals(short.class)) {
										columnvalue = rs.getShort(i);
									} else if (fieldtype.equals(Byte.class) || fieldtype.equals(byte.class)) {
										columnvalue = rs.getByte(i);
									} else if (fieldtype.equals(Integer.class) || fieldtype.equals(int.class)) {
										columnvalue = rs.getInt(i);
									} else if (fieldtype.equals(Long.class) || fieldtype.equals(long.class)) {
										columnvalue = rs.getLong(i);
									} else if (fieldtype.equals(Float.class) || fieldtype.equals(float.class)) {
										columnvalue = rs.getFloat(i);
									} else if (fieldtype.equals(Double.class) || fieldtype.equals(double.class)) {
										columnvalue = rs.getDouble(i);
									} else if (fieldtype.equals(Boolean.class) || fieldtype.equals(boolean.class)) {
										columnvalue = rs.getBoolean(i);
									} else if (fieldtype.equals(Date.class)) {
										columnvalue = rs.getTimestamp(i);
									} else if ( fieldtype.equals(java.sql.Date.class)) {
										Timestamp timestamp = rs.getTimestamp(i);
										columnvalue = new java.sql.Date(timestamp.getTime());
									} else if (fieldtype.equals(String.class)) {
										columnvalue = rs.getString(i);
									}

									reflector.getSetInvoker(propertyname).invoke(obj, new Object[] { columnvalue });
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					array.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}
}
