package com.italia.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class App {
	public static volatile App conf;
	private boolean logEnable;
	private double kidsPrice;
	private double adultPrice;
	private double seniorPrice;
	private double kidsNightPrice;
	private double adultNightPrice;
	private double seniorNightPrice;
	private double kidsWeekEndsPrice;
	private double adultWeekEndstPrice;
	private double seniorWeekEndsPrice;
	private String printerName;
	private boolean printerIsOn;
	private String ampmSet;
	private int timeStart;
	
	private String contactName1;
	private String contact1;
	
	private String contactName2;
	private String contact2;
	
	private String contactName3;
	private String contact3;
	
	private String appReleaseName;
	private String appReleaseUrlAndroid;
	private String appReleaseUrlIos;
	private String appApkLocation;
	
	private App() {}
	
 public static App getInstance() {
		
		if(conf == null) {
			synchronized(App.class) {
				if(conf ==  null) {
					conf = new App();
					conf.readConf();//reading configuration on app configuration file
				}
			}
		}
		
		return conf;
	}
	
 private void readConf() {
		try {
			
			File file = new File(GlobalVar.APP_CONF_FILE);
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			setLogEnable(prop.getProperty("LOG").equalsIgnoreCase("yes")? true : false);
			setKidsPrice(Double.valueOf(prop.getProperty("KIDS")));
			setAdultPrice(Double.valueOf(prop.getProperty("ADULT")));
			setSeniorPrice(Double.valueOf(prop.getProperty("SENIOR")));
			setPrinterName(prop.getProperty("PRINTER_NAME"));
			setPrinterIsOn(prop.getProperty("PRINTER_ON").equalsIgnoreCase("true")? true : false);
			setAmpmSet(prop.getProperty("AMPM-SET"));
			setTimeStart(Integer.valueOf(prop.getProperty("ADJUSTING-PRICE-ENTRANCE-TIME-START")));
			setKidsNightPrice(Double.valueOf(prop.getProperty("KIDS-NIGHT")));
			setAdultNightPrice(Double.valueOf(prop.getProperty("ADULT-NIGHT")));
			setSeniorNightPrice(Double.valueOf(prop.getProperty("SENIOR-NIGHT")));
			setKidsWeekEndsPrice(Double.valueOf(prop.getProperty("KIDS-WEEKENDS")));
			setAdultWeekEndstPrice(Double.valueOf(prop.getProperty("ADULT-WEEKENDS")));
			setSeniorWeekEndsPrice(Double.valueOf(prop.getProperty("SENIOR-WEEKENDS")));
			
			
			setContact1(prop.getProperty("CONTACT1"));
			setContact2(prop.getProperty("CONTACT2"));
			setContact3(prop.getProperty("CONTACT3"));
			
			setContactName1(prop.getProperty("CONTACT-NAME1"));
			setContactName2(prop.getProperty("CONTACT-NAME2"));
			setContactName3(prop.getProperty("CONTACT-NAME3"));
			
			setAppReleaseName(prop.getProperty("APP_NAME_APK"));
			setAppReleaseUrlAndroid(prop.getProperty("APP_RELEASE_DOWNLOAD_URL_ANDROID"));
			setAppReleaseUrlIos(prop.getProperty("APP_RELEASE_DOWNLOAD_URL_IOS"));
			setAppApkLocation(prop.getProperty("APP_APK_LOCATION"));
			
		}catch(Exception e) {}
 
 }
 
 
}
