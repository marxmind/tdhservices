package com.italia.utils;

import java.io.File;

public class GlobalVar {
	public static String smsMSG = "Hi <recepient>, we have successfully reserved your room <room>. For inquiry please call 09173072767. The Dreamweavers Hill";
	public static String smsOnlineBookingResponeMSG = "Hi <recepient>, we have received your room <room>. For inquiry please call 09173072767. The Dreamweavers Hill";
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String POST_URL = "https://api.semaphore.co/api/v4/messages";
	public static final String GET_URL = "https://api.semaphore.co/api/v4/messages";
	public static final String PROVIDER_API = "e6a9a23dd6b10f587d95afc2488a1d37";
	public static final String PRIMARY_DRIVE=System.getenv("SystemDrive");
	public static final String APP_NAME =  "tdh";
	public static final String SEP = File.separator;
	public static final String APP_CONF_DIR = "C:"+SEP+APP_NAME+SEP+"conf"+SEP;
	public static final String APP_DATABASE_CONF = "C:"+SEP+APP_NAME+SEP+"conf"+SEP+"dbconf.max";
	public static final String SECURITY_ENCRYPTION_FORMAT="utf-8";
	public static final String LOG_FOLDER = PRIMARY_DRIVE + SEP + APP_NAME + SEP + "log" + SEP;
}
