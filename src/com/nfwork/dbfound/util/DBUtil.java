
package com.nfwork.dbfound.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 关闭相应数据库对象
 * @author Administrator
 *
 */
public class DBUtil {
	
	public static void closeConnection( Connection conn ){
		if( conn == null) return;
		try{
			conn.close();
		} catch(SQLException ex){
			LogUtil.warn(ex.getMessage());
		}
	}
	
	public static void closeResultSet( ResultSet rs ){
		if( rs == null) return;
		try{
			rs.close();
		} catch(SQLException ex){
			LogUtil.warn(ex.getMessage());
		}
	}
	
	public  static void closeStatement( Statement stmt ){
		if( stmt == null) return;
		try{
			stmt.close();
		} catch(SQLException ex){
			LogUtil.warn(ex.getMessage());
		}
	}
    
}
