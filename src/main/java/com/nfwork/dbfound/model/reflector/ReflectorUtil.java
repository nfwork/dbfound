package com.nfwork.dbfound.model.reflector;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.enums.EnumTypeHandler;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;

public class ReflectorUtil {

	public static <T> List<T> parseResultList(Class<T> clazz, ResultSet rs, Context context) throws SQLException {
		List<T> array = new ArrayList<T>();

		if (rs != null) {
			
			Calendar defaultCalendar = Calendar.getInstance();
			
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			Reflector reflector = Reflector.forClass(clazz);

			Map<String,String> colNameMap = new HashMap<String, String>();

			int totalCounts = 0;
			while (rs.next()) {
				if (context.isQueryLimit() && ++totalCounts > context.getQueryLimitSize()) {
					break;
				}
				ObjectFactory objectFactory = new DefaultObjectFactory();
				T obj = objectFactory.create(clazz);
				for (int i = 1; i <= columnCount; i++) {
					String colName = md.getColumnName(i);

					// 判断是否有as 逻辑，如果没有as，强制转化为小写
					String labName =  md.getColumnLabel(i);
					if (labName.equalsIgnoreCase(colName)){
						colName = colName.toLowerCase();
					}else{
						colName = labName;
					}

					String propertyname =colNameMap.get(colName);
					if (propertyname == null){
						propertyname = reflector.getFieldName(colName);
						if (!reflector.hasSetter(propertyname)) {
							propertyname = StringUtil.underscoreToCamelCase(propertyname);
						}
						colNameMap.put(colName,propertyname);
					}

					if (reflector.hasSetter(propertyname)) {
						Object columnvalue = rs.getObject(i);
						if (columnvalue != null) {

							Class<?> fieldtype = reflector.getSetterType(propertyname);

							if (fieldtype.equals(String.class)) {
								columnvalue = rs.getString(i);
							}else if (fieldtype.equals(Integer.class) || fieldtype.equals(int.class)) {
								columnvalue = rs.getInt(i);
							} else if (fieldtype.equals(Long.class) || fieldtype.equals(long.class)) {
								columnvalue = rs.getLong(i);
							} else if (fieldtype.equals(Float.class) || fieldtype.equals(float.class)) {
								columnvalue = rs.getFloat(i);
							} else if (fieldtype.equals(Double.class) || fieldtype.equals(double.class)) {
								columnvalue = rs.getDouble(i);
							} else if (fieldtype.equals(Boolean.class) || fieldtype.equals(boolean.class)) {
								columnvalue = rs.getBoolean(i);
							} else if (fieldtype.equals(java.sql.Date.class)) {
								columnvalue = rs.getDate(i, defaultCalendar);
							} else if (fieldtype.equals(Date.class)) {
								columnvalue = rs.getTimestamp(i, defaultCalendar);
							} else if (Enum.class.isAssignableFrom(fieldtype)){
								EnumTypeHandler handler = EnumHandlerFactory.getEnumHandler(fieldtype);
								String svalue = rs.getString(i);
								if(svalue!=null) {
									columnvalue = handler.locateEnum(svalue);
								}
							} else if (fieldtype.equals(BigDecimal.class)){
								columnvalue = rs.getBigDecimal(i);
							} else if (fieldtype.equals(Short.class) || fieldtype.equals(short.class)) {
								 columnvalue = rs.getShort(i);
							} else if (fieldtype.equals(Byte.class) || fieldtype.equals(byte.class)) {
								 columnvalue = rs.getByte(i);
							}

							try {
								reflector.getSetInvoker(propertyname).invoke(obj, new Object[] { columnvalue });
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
				array.add(obj);
			}
		}

		return array;
	}
}
