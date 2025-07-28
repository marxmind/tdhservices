package com.italia.db.conf;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
	
	/**
	 * 
	 * @param database name from Conf.database name
	 * @return
	 */
	public static Connection getConnection(String dbName){
		Conf conf = Conf.getInstance();
		Connection conn = null;
		try{
			String driver = conf.getDatabaseDriver();
			Class.forName(driver);
			//String db_url = conf.getDatabaseUrl();
			String db_url = conf.getDatabaseUrl() + conf.getDatabaseAssignedIP();
			String port = conf.getDatabasePort(); 
			String timezone = "";
			if(conf.getDatabaseTimeZone()!=null && !conf.getDatabaseTimeZone().isEmpty()) {
				timezone = conf.getDatabaseTimeZone() +"&";
			}
			String url = db_url + ":" + port + "/" + dbName + "?"+ timezone +  conf.getDatabaseSSL();
			String u_name = conf.getDatabaseUserName();
			String pword = conf.getDatabasePassword();
			conn = DriverManager.getConnection(url, u_name, pword);
			System.out.println("DBConnected: " + url);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}