package com.italia.enm;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2020
 *
 */
public enum DownpaymentMode {
	
	ON_PROCESS(0, "PROCESSING"),
	CASH(1, "CASH"),
	GCASH(2,"GCASH"),
	BDO(3,"BDO"),
	LANDBANK(4,"LANDBANK"),
	DBP(5,"DBP"),
	BPI(6,"BPI"),
	EASTWEST(7,"EASTWEST"),
	PAYMAY(8,"PAYMAYA");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private DownpaymentMode(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(DownpaymentMode type : DownpaymentMode.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return DownpaymentMode.ON_PROCESS.getName();
	}
	
	public static DownpaymentMode typeModeName(int id){
		for(DownpaymentMode type : DownpaymentMode.values()){
			if(id==type.getId()){
				return type;
			}
		}
		return DownpaymentMode.ON_PROCESS;
	}
	
	public static int typeId(String name){
		for(DownpaymentMode type : DownpaymentMode.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return DownpaymentMode.ON_PROCESS.getId();
	}
	
	public static DownpaymentMode typeModeId(String name){
		for(DownpaymentMode type : DownpaymentMode.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type;
			}
		}
		return DownpaymentMode.ON_PROCESS;
	}
	
}