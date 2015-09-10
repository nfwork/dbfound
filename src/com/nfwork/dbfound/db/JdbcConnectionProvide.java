package com.nfwork.dbfound.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.exception.DBFoundPackageException;

public class JdbcConnectionProvide extends ConnectionProvide {

	String url;
	String driverClass;
	String username;
	String password;

	public JdbcConnectionProvide() {

	}

	public JdbcConnectionProvide(String url, String driverClass,
			String dialect, String username, String password) {
		super(dialect);
		this.url = url;
		this.driverClass = driverClass;
		this.username = username;
		this.password = password;
	}

	public JdbcConnectionProvide(String connectionProvide, String url,
			String driverClass, String dialect, String username,
			String password) {
		super(connectionProvide, dialect);
		this.url = url;
		this.driverClass = driverClass;
		this.username = username;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		try {
			Class.forName(driverClass);
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new DBFoundPackageException("create connection exception：" + e.getMessage(), e);
		} catch (ClassNotFoundException ee) {
			throw new DBFoundPackageException(
					"jdbc driver not found：" + ee.getMessage(), ee);
		}
	}

	@Override
	public void closeConnection(Connection connection) {
		DBUtil.closeConnection(connection);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
