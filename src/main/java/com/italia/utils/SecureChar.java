package com.italia.utils;

import java.util.Base64;

/**
 * 
 * @author mark italia
 * @since 11/16/2016
 * @version 1.0
 * this class is use for encoding and decoding of character
 */
public class SecureChar {

	public static String encode(String val){
		
		try{
		
		String encoded = Base64.getEncoder().encodeToString(val.getBytes(GlobalVar.SECURITY_ENCRYPTION_FORMAT));
		
		return encoded;
		}catch(Exception e){}
		return null;
	}
	public static String decode(String val){
		try{
			byte [] barr = Base64.getDecoder().decode(val);
			return new String(barr,GlobalVar.SECURITY_ENCRYPTION_FORMAT);
			}catch(Exception e){}
			return null;
	}
	
	
}