
package com.nfwork.dbfound.util;

import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.exception.DBFoundPackageException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	
	public static void closeConnection( Connection conn ){
		if( conn == null) return;
		try{
			conn.close();
		} catch(Throwable ex){
			LogUtil.warn(ex.getMessage());
		}
	}
	
	public static void closeResultSet( ResultSet rs ){
		if( rs == null) return;
		try{
			rs.close();
		} catch(Throwable ex){
			LogUtil.warn(ex.getMessage());
		}
	}
	
	public  static void closeStatement( Statement stmt ){
		if( stmt == null) return;
		try{
			stmt.close();
		} catch(Throwable ex){
			LogUtil.warn(ex.getMessage());
		}
	}

	public static void prepareTransaction(Connection con, Transaction transaction) {
		try {
			if (transaction.isReadOnly()) {
				con.setReadOnly(true);
			}

			if(transaction.getTransactionIsolation()>0) {
				int currentIsolation = con.getTransactionIsolation();
				if(currentIsolation != transaction.getTransactionIsolation()) {
					transaction.setTransactionIsolationHistory(currentIsolation);
					con.setTransactionIsolation(transaction.getTransactionIsolation());
				}
			}

			if (con.getAutoCommit()) {
				con.setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("prepareTransaction failed:"+ e.getMessage(), e);
		}
	}

	public static void resetTransaction(Connection con, Transaction transaction) {
		try {
			if(transaction.getTransactionIsolationHistory() != null){
				con.setTransactionIsolation(transaction.getTransactionIsolationHistory());
				transaction.setTransactionIsolationHistory(null);
			}
			if(transaction.isReadOnly()) {
				con.setReadOnly(false);
			}
			if(!con.getAutoCommit()){
				con.setAutoCommit(true);
			}
		} catch (Throwable e) {
			LogUtil.error("resetTransaction failed:"+ e.getMessage(), e);
		}
	}
    
}
