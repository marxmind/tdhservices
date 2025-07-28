package com.italia.enm;

public enum LeaveFormType {

	SWAP_FORM(0,"SWAP", "SWAP FORM"),
	VACTION_LEAVE(1,"VL", "VACATION LEAVE FORM"),
	SICK_LEAVE(2, "SL","SICK LEAVE FORM"),
	STUDIES(3, "STUDIES","STUDIES LEAVE FORM"),
	EMERGENCY_LEAVE(4,"EL", "EMERGENCY LEAVE FORM"),
	MATERNITY_LEAVE(5, "ML","MATERNITY LEAVE FORM"),
	PATERNITY_LEAVE(6, "PL","PATERNITY LEAVE FORM"),
	OTHERS(7, "OTHERS","LEAVE FORM");
	
	private int id;
	private String code;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	private LeaveFormType(int id, String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	public static LeaveFormType containId(int id) {
		for(LeaveFormType t : LeaveFormType.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return SWAP_FORM;
	}
	
	public static LeaveFormType containName(String name) {
		for(LeaveFormType t : LeaveFormType.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return SWAP_FORM;
	}
	
	
}
