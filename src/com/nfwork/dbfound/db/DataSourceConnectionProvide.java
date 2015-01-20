package com.nfwork.dbfound.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.DBUtil;

public class DataSourceConnectionProvide extends ConnectionProvide {

	DataSource dataSource;

	String jndiName;

	public DataSourceConnectionProvide() {

	}

	public DataSourceConnectionProvide(DataSource dataSource, String dialectName) {
		super(dialectName);
		this.dataSource = dataSource;
	}

	public DataSourceConnectionProvide(String unitName, DataSource dataSource,
			String dialectName) {
		super(unitName, dialectName);
		this.dataSource = dataSource;
	}

	public DataSourceConnectionProvide(String unitName, String dataSourceName,
			String dialectName) {
		super(unitName, dialectName);
		init(dataSourceName);
	}

	public DataSourceConnectionProvide(String dataSourceName, String dialectName) {
		super(dialectName);
		init(dataSourceName);
	}

	@Override
	public void closeConnection(Connection connection) {
		DBUtil.closeConnection(connection);
	}

	@Override
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new DBFoundPackageException("创建数据库连接异常:" + e.getMessage(), e);
		}
	}

	/**
	 * 初始化外部连接池
	 * 
	 * @param dataSource
	 */
	public synchronized void init(String dataSource) {
		Context initContext = null;
		Object object = null;
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			object = envContext.lookup(dataSource);
		} catch (Exception e) {
			try {
				object = initContext.lookup(dataSource);
			} catch (NamingException e1) {
				throw new RuntimeException(e1);
			}
		}
		this.dataSource = (DataSource) object;
	}

	@Override
	public void regist() {
		if (dataSource == null) {
			init(jndiName);
		}
		super.regist();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

}
