
package com.nfwork.dbfound.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtil {
	
	public static void closeConnection( Connection conn ){
		if( conn == null) return;
		try{
			conn.close();
		} catch(Exception ex){
			LogUtil.warn(ex.getMessage());
		}
	}
	
	public static void closeResultSet( ResultSet rs ){
		if( rs == null) return;
		try{
			rs.close();
		} catch(Exception ex){
			LogUtil.warn(ex.getMessage());
		}
	}
	
	public  static void closeStatement( Statement stmt ){
		if( stmt == null) return;
		try{
			stmt.close();
		} catch(Exception ex){
			LogUtil.warn(ex.getMessage());
		}
	}
    
}
