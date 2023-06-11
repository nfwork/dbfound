package com.nfwork.dbfound.model.reflector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import com.nfwork.dbfound.el.PropertyTransfer;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.resolver.TypeResolverTool;

public class ReflectorUtil extends PropertyTransfer {

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

							if (Enum.class.isAssignableFrom(fieldType)){
								String stringValue = rs.getString(i);
								if(stringValue!=null) {
									columnValue = EnumHandlerFactory.getEnumHandler(fieldType).locateEnum(stringValue);
								}
							} else {
								columnValue = TypeResolverTool.getValue(fieldType,rs,i,defaultCalendar);
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
			propertyName = underscoreToCamelCase(propertyName);
			if (reflector.hasSetter(propertyName)) {
				return propertyName;
			}
		}

		propertyName = reflector.getFieldName(labName.toLowerCase());
		if (reflector.hasSetter(propertyName)) {
			return propertyName;
		}
		if(propertyName.contains("_")) {
			propertyName = underscoreToCamelCase(propertyName);
			if (reflector.hasSetter(propertyName)) {
				return propertyName;
			}
		}

		return labName;
	}
}
