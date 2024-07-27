package com.nfwork.dbfound.db.dialect;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;

public class DialectFactory {

	public static SqlDialect createDialect(String dialectType) {
		if (dialectType == null) {
			throw new DBFoundRuntimeException("dialect is empty, please check");
		}
		String className =  dialectType.trim();
		if(!dialectType.contains(".")){
			className = "com.nfwork.dbfound.db.dialect." + className;
		}
		try {
			 return (SqlDialect) Class.forName(className).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new DBFoundRuntimeException("SqlDialect init failed, please check the class: " + className +" is exists or it is implements SqlDialect", e);
		}
	}
}
