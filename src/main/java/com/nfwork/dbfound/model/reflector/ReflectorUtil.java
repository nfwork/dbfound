package com.nfwork.dbfound.model.reflector;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.util.StringUtil;

public class ReflectorUtil {

	public static <T> List<T> parseResultList(Class<T> clazz, ResultSet rs) throws SQLException {
		List<T> array = new ArrayList<>();

		if (rs != null) {
			Calendar defaultCalendar = Calendar.getInstance();
			
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			Reflector reflector = Reflector.forClass(clazz);

			Map<String,String> colNameMap = new HashMap<>();
			ObjectFactory objectFactory = new DefaultObjectFactory();

			while (rs.next()) {
				T obj = objectFactory.create(clazz);
				for (int i = 1; i <= columnCount; i++) {
					String labName =  md.getColumnLabel(i);
					String propertyName = colNameMap.get(labName);

					if (propertyName == null){
						propertyName = getPropertyName(reflector,labName);
						colNameMap.put(labName,propertyName);
					}

					if (reflector.hasSetter(propertyName)) {
						Object columnValue = rs.getObject(i);
						if (columnValue != null) {

							Class<?> fieldType = reflector.getSetterType(propertyName);

							if (fieldType.equals(String.class)) {
								columnValue = rs.getString(i);
							}else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
								columnValue = rs.getInt(i);
							} else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
								columnValue = rs.getLong(i);
							} else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
								columnValue = rs.getFloat(i);
							} else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
								columnValue = rs.getDouble(i);
							} else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
								columnValue = rs.getBoolean(i);
							} else if (fieldType.equals(java.sql.Date.class)) {
								columnValue = rs.getDate(i, defaultCalendar);
							} else if (fieldType.equals(Date.class)) {
								columnValue = rs.getTimestamp(i, defaultCalendar);
							} else if (Enum.class.isAssignableFrom(fieldType)){
								String stringValue = rs.getString(i);
								if(stringValue!=null) {
									columnValue = EnumHandlerFactory.getEnumHandler(fieldType).locateEnum(stringValue);
								}
							} else if (fieldType.equals(BigDecimal.class)){
								columnValue = rs.getBigDecimal(i);
							} else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
								columnValue = rs.getShort(i);
							} else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
								columnValue = rs.getByte(i);
							} else if (fieldType.equals(byte[].class)) {
								columnValue = rs.getBytes(i);
							} else if (fieldType.equals(LocalDateTime.class)) {
								columnValue = rs.getTimestamp(i, defaultCalendar).toLocalDateTime();
							} else if (fieldType.equals(LocalDate.class)) {
								columnValue = rs.getDate(i, defaultCalendar).toLocalDate();
							}

							try {
								reflector.getSetInvoker(propertyName).invoke(obj, new Object[] { columnValue });
							} catch (Exception e) {
								throw new DBFoundRuntimeException("reflector set properties failed",e);
							}
						}
					}
				}
				array.add(obj);
			}
		}

		return array;
	}


	private static String getPropertyName(Reflector reflector, String labName){
		String propertyName = reflector.getFieldName(labName);
		if (reflector.hasSetter(propertyName)) {
			return propertyName;
		}
		if(propertyName.contains("_")) {
			propertyName = StringUtil.underscoreToCamelCase(propertyName);
			if (reflector.hasSetter(propertyName)) {
				return propertyName;
			}
		}

		propertyName = reflector.getFieldName(labName.toLowerCase());
		if (reflector.hasSetter(propertyName)) {
			return propertyName;
		}
		if(propertyName.contains("_")) {
			propertyName = StringUtil.underscoreToCamelCase(propertyName);
			if (reflector.hasSetter(propertyName)) {
				return propertyName;
			}
		}

		return labName;
	}
}
