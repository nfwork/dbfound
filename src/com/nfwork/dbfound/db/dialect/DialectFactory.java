package com.nfwork.dbfound.db.dialect;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;

public class DialectFactory {

	public static SqlDialect createDialect(String dialectType) {
		if (dialectType == null) {
			throw new DBFoundRuntimeException("数据库dialect为空，请确认是否设置或进行初始化");
		}
		String className = "com.nfwork.dbfound.db.dialect."
				+ dialectType.trim();
		try {
			SqlDialect dialect = (SqlDialect) Class.forName(className)
					.newInstance();
			return dialect;
		} catch (Exception e) {
			LogUtil.error("数据库方言初始化错误，请确认是否存在方言实现类：" + className, e);
		}
		return null;
	}
}
