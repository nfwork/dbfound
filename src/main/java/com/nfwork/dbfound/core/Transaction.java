package com.nfwork.dbfound.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import com.nfwork.dbfound.core.Context.ConnObject;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.LogUtil;

public class Transaction {

	Map<String, ConnObject> connMap;

	private boolean open = false;

	private int transactionIsolation;

	private boolean readOnly;

	private Integer transactionIsolationHistory;

	public Transaction() {
	}

	/**
	 * 开始事务
	 */
	public void begin() {
		open = true;
	}

	public boolean isOpen() {
		return open;
	}

	/**
	 * 事务结束
	 */
	public void end() {
		if (!open) {
			return;
		} else {
			open = false;
		}

		if (connMap != null && !connMap.isEmpty()) {
			Collection<ConnObject> connObjects = connMap.values();
			for (ConnObject connObject : connObjects) {
				try {
					ConnectionProvide provide = connObject.provide;
					Connection connection = connObject.connection;
					provide.closeConnection(connection);
				} catch (Throwable e) {
					LogUtil.error("transaction close exception:" + e.getMessage(), e);
				}
			}
			connMap.clear();
		}
	}

	/**
	 * 提交事务
	 */
	public void commit() {
		if (!open || connMap == null || connMap.isEmpty()) {
			return;
		}
		Collection<ConnObject> connObjects = connMap.values();
		for (ConnObject connObject : connObjects) {
			try {
				connObject.connection.commit();
			} catch (SQLException e) {
				throw new DBFoundRuntimeException("transaction commit exception: " +e.getMessage(), e);
			}
			DBUtil.resetTransaction(connObject.connection,this);
		}
	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		if (!open || connMap == null || connMap.isEmpty()) {
			return;
		}
		Collection<ConnObject> connObjects = connMap.values();
		for (ConnObject connObject : connObjects) {
			try {
				connObject.connection.rollback();
			} catch (Throwable e) {
				LogUtil.error("transaction rollback exception:" + e.getMessage(), e);
			}
			DBUtil.resetTransaction(connObject.connection,this);
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Integer getTransactionIsolationHistory() {
		return transactionIsolationHistory;
	}

	public void setTransactionIsolationHistory(Integer transactionIsolationHistory) {
		this.transactionIsolationHistory = transactionIsolationHistory;
	}

	public int getTransactionIsolation() {
		return transactionIsolation;
	}

	public void setTransactionIsolation(int transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}
}
