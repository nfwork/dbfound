package com.nfwork.dbfound.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import com.nfwork.dbfound.core.Context.ConnObject;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.util.LogUtil;

public class Transaction {

	Map<String, ConnObject> connMap;

	private boolean open = false;

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
		if (open == false) {
			return;
		} else {
			open = false;
		}

		if (connMap == null || connMap.isEmpty()) {
			return;
		} else {
			Collection<ConnObject> connObjects = connMap.values();
			for (ConnObject connObject : connObjects) {
				try {
					ConnectionProvide provide = connObject.provide;
					Connection connection = connObject.connection;
					provide.closeConnection(connection);
				} catch (Exception e) {
					LogUtil.error("事务关闭异常:" + e.getMessage(), e);
				}
			}
			connMap.clear();
		}
	}

	/**
	 * 提交事务
	 */
	public void commit() {
		if (open == false || connMap == null || connMap.isEmpty()) {
			return;
		}
		Collection<ConnObject> connObjects = connMap.values();
		for (ConnObject connObject : connObjects) {
			try {
				connObject.connection.commit();
			} catch (Exception e) {
				LogUtil.error("事务提交异常:" + e.getMessage(), e);
			}
		}
	}

	/**
	 * 提交并结束事务
	 */
	public void commitAndEnd() {
		if (open == false) {
			return;
		} else {
			open = false;
		}

		if (connMap == null || connMap.isEmpty()) {
			return;
		} else {
			Collection<ConnObject> connObjects = connMap.values();
			for (ConnObject connObject : connObjects) {
				try {
					connObject.connection.commit();
				} catch (Exception e) {
					LogUtil.error("事务提交异常:" + e.getMessage(), e);
				}
				try {
					ConnectionProvide provide = connObject.provide;
					Connection connection = connObject.connection;
					provide.closeConnection(connection);
				} catch (Exception e) {
					LogUtil.error("事务关闭异常:" + e.getMessage(), e);
				}
			}
			connMap.clear();
		}
	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		if (open == false || connMap == null || connMap.isEmpty()) {
			return;
		}
		Collection<ConnObject> connObjects = connMap.values();
		for (ConnObject connObject : connObjects) {
			try {
				connObject.connection.rollback();
			} catch (SQLException e) {
				LogUtil.error("事务回滚异常:" + e.getMessage(), e);
			}
		}
	}
}
