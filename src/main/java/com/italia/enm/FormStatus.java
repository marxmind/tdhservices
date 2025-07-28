package com.italia.enm;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 12-25-2020
 *
 */
public enum FormStatus {
	
	FOR_APPROVAL(1, "FOR APPROVAL"),
	APPROVED_BY_MANAGER(2, "APPROVED BY MANAGER"),
	APPROVED_BY_FINANCE(3, "APPROVED BY FINANCE"),
	APPROVED_BY_ADMIN_OFFICER(4, "APPROVED BY ADMIN OFFICER"),
	APPROVED(5, "APPROVED"),
	FOR_CLARIFICATION(6, "FOR CLARIFICATION"),
	DENIED(7, "DENIED"),
	ON_HOLD(8, "ON HOLD");
	
	
	private int id;
	private String name;
	
	private FormStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static FormStatus containId(int id) {
		for(FormStatus t : FormStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return FOR_APPROVAL;
	}
	
	public static FormStatus containName(String name) {
		for(FormStatus t : FormStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return FOR_APPROVAL;
	}
	
}