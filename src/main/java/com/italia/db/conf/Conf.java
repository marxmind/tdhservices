package com.italia.db.conf;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.italia.utils.GlobalVar;
import com.italia.utils.SecureChar;

import lombok.Data;
@Data
public class Conf {

	private static volatile Conf conf;
	private String databaseMain;
	private String databasePort;
	private String databaseUrl;
	private String databaseUrlServer;
	private String databaseDriver;
	private String databaseSSL;
	private String databaseTimeZone;
	private String databaseUserName;
	private String databasePassword;
	private String serverDatabase;
	private String serverDatabaseIp;
	private String databaseHomePath;
	private String databaseAssignedIP;
	
	private Conf() {
		System.out.println("initializing database information...");
	}
	
	public static Conf getInstance() {
		
		if(conf == null) {
			synchronized(Conf.class) {
				if(conf ==  null) {
					conf = new Conf();
					conf.readConf();
					System.out.println("reading database information");
				}
			}
		}
		
		return conf;
	}
	
	private void readConf() {
		try {
			
			File file = new File(GlobalVar.APP_DATABASE_CONF);
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			
			String u_name = SecureChar.decode(prop.getProperty("DATABASE_UNAME"));
			   u_name = u_name.replaceAll("mark", "");
			   u_name = u_name.replaceAll("rivera", "");
			   u_name = u_name.replaceAll("italia", "");
			String pword =  SecureChar.decode(prop.getProperty("DATABASE_PASSWORD"));
			   pword = pword.replaceAll("mark", "");
			   pword = pword.replaceAll("rivera", "");
			   pword = pword.replaceAll("italia", "");   
			conf.setDatabaseMain(prop.getProperty("DATABASE_NAME"));
			conf.setDatabaseDriver(prop.getProperty("DATABASE_DRIVER"));
			conf.setDatabaseUrl(prop.getProperty("DATABASE_URL"));
			conf.setDatabaseUrlServer(prop.getProperty("DATABASE_URL_SERVER"));
			conf.setDatabasePort(prop.getProperty("DATABASE_PORT"));
			conf.setDatabaseSSL(prop.getProperty("DATABASE_SSL"));
			conf.setDatabaseTimeZone(prop.getProperty("DATABASE_SERVER_TIME_ZONE"));
			conf.setDatabaseUserName(u_name);
			conf.setDatabasePassword(pword);
			conf.setServerDatabase(prop.getProperty("DATABASE_SERVER_DB_URL"));
			conf.setServerDatabaseIp(prop.getProperty("DATABASE_SERVER_IP"));
			conf.setDatabaseHomePath(prop.getProperty("DATABASE_HOME_PATH"));
			conf.setDatabaseAssignedIP(prop.getProperty("DATABASE_ASSIGNED_IP")); 
			
		}catch(Exception e) {
			System.out.println("Configuration file was not set. See error: " + e.getMessage());
		}
	}
	
}