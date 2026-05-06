package com.nfwork.dbfound.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.LogUtil;

final class ConnectionManager {

	private final Map<String, ConnectionResource> connMap = new HashMap<>();

	Connection getConnection(String provideName) {
		ConnectionResource connObject = connMap.get(provideName);
		if (connObject == null) {
			ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
			Connection conn = provide.getConnection();
			connObject = new ConnectionResource(provide, conn);
			connMap.put(provideName, connObject);
		}
		return connObject.connection;
	}

	Connection getTransactionConnection(String provideName, Transaction transaction) {
		ConnectionResource connObject = connMap.get(provideName);
		if (connObject == null) {
			ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
			Connection conn = provide.getConnection();

			DBUtil.prepareTransaction(conn, transaction);
			connObject = new ConnectionResource(provide, conn);
			connMap.put(provideName, connObject);
		}
		return connObject.connection;
	}

	SqlDialect getSqlDialect(String provideName) {
		return connMap.get(provideName).provide.getSqlDialect();
	}

	void closeConnections() {
		closeConnections("database connection close exception:");
	}

	void closeTransactionConnections() {
		closeConnections("transaction close exception:");
	}

	private void closeConnections(String errorMessage) {
		if (connMap.isEmpty()) {
			return;
		}
		Collection<ConnectionResource> connObjects = connMap.values();
		for (ConnectionResource connObject : connObjects) {
			closeConnection(connObject, errorMessage);
		}
		connMap.clear();
	}

	private void closeConnection(ConnectionResource connObject, String errorMessage) {
		try {
			ConnectionProvide provide = connObject.provide;
			Connection connection = connObject.connection;
			provide.closeConnection(connection);
		} catch (Throwable throwable) {
			LogUtil.error(errorMessage + throwable.getMessage(), throwable);
		}
	}

	void commit(Transaction transaction) {
		if (connMap.isEmpty()) {
			return;
		}
		Collection<ConnectionResource> connObjects = connMap.values();
		for (ConnectionResource connObject : connObjects) {
			try {
				connObject.connection.commit();
			} catch (SQLException e) {
				throw new DBFoundRuntimeException("transaction commit exception: " + e.getMessage(), e);
			}
			DBUtil.resetTransaction(connObject.connection, transaction);
		}
	}

	void rollback(Transaction transaction) {
		if (connMap.isEmpty()) {
			return;
		}
		Collection<ConnectionResource> connObjects = connMap.values();
		for (ConnectionResource connObject : connObjects) {
			try {
				connObject.connection.rollback();
			} catch (Throwable throwable) {
				LogUtil.error("transaction rollback exception:" + throwable.getMessage(), throwable);
			}
			DBUtil.resetTransaction(connObject.connection, transaction);
		}
	}
}
