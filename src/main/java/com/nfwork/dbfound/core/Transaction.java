package com.nfwork.dbfound.core;

import java.sql.Connection;
import com.nfwork.dbfound.db.dialect.SqlDialect;

public class Transaction {

	private ConnectionManager connectionManager;

	private boolean open = false;

	private int transactionIsolation = -1;

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

	Connection getConn(String provideName) {
		if (connectionManager == null) {
			connectionManager = new ConnectionManager();
		}
		return connectionManager.getTransactionConnection(provideName, this);
	}

	SqlDialect getConnDialect(String provideName) {
		return connectionManager.getSqlDialect(provideName);
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
		if (connectionManager != null) {
			connectionManager.closeTransactionConnections();
		}
	}

	/**
	 * 提交事务
	 */
	public void commit() {
		if (!open || connectionManager == null) {
			return;
		}
		connectionManager.commit(this);
	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		if (!open || connectionManager == null) {
			return;
		}
		connectionManager.rollback(this);
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
