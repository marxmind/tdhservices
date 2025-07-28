package com.italia.enm;

/**
 * 
 * @author Mark Italia
 * @since 08-11-2018
 * @version 1.0
 *
 */
public enum FormType {
	
	CASH_ADVANCE(1, "CASH ADVANCE"),
	REQUEST_FORM(2, "REQUEST FORM"),
	LATE_FORM(3, "LATE FORM"),
	SWAP_FORM(4, "SWAP FORM"),
	LEAVE_FORM(5, "LEAVE FORM"),
	EMPLOYEE_OS(6, "EMPLOYEE ORDER"),
	EMERGENCY_LEAVE(7, "EMERGENCY LEAVE"),
	SICK_LEAVE(8, "SICK LEAVE");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private FormType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static FormType containId(int id) {
		for(FormType t : FormType.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return CASH_ADVANCE;
	}
	
	public static FormType containName(String name) {
		for(FormType t : FormType.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return CASH_ADVANCE;
	}
	
	
}