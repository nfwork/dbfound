package com.nfwork.dbfound.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.nfwork.dbfound.db.dialect.DialectFactory;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;

public abstract class ConnectionProvide {

	public static final String MySqlDialect = "MySqlDialect";

	public static final String OracleDialect = "OracleDialect";

	public static final String SqlServerDialect = "SqlServerDialect";

	public static final String AccessDialect = "AccessDialect";

	public static final String InformixDialect = "InformixDialect";

	SqlDialect sqlDialect; // 数据库方言

	String provideName = "_default"; // 单元名称

	String dialect;

	public ConnectionProvide() {

	}

	public ConnectionProvide(String dialectName) {
		sqlDialect = DialectFactory.createDialect(dialectName);
	}

	public ConnectionProvide(String provideName, String dialectName) {
		sqlDialect = DialectFactory.createDialect(dialectName);
		if (provideName != null && !"".equals(provideName)) {
			this.provideName = provideName;
		}
	}

	public void regist() {
		if (sqlDialect == null) {
			sqlDialect = DialectFactory.createDialect(dialect);
		}
		ConnectionProvideManager.registSource(this);
	}

	public void unRegist() {
		ConnectionProvideManager.unRegistSource(this);
	}

	public void closeAutoCommit(Connection conn) {
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("closeAutoCommit异常:"
					+ e.getMessage(), e);
		}
	}

	public abstract Connection getConnection();

	public abstract void closeConnection(Connection connection);

	public String getProvideName() {
		return provideName;
	}

	public void setProvideName(String provideName) {
		this.provideName = provideName;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

}
