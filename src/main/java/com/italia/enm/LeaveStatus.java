package com.italia.enm;

public enum LeaveStatus {

	FOR_APPROVAL(0, "FOR APPROVAL"),
	APPROVED(1, "APPROVED"),
	DENIED(2, "DENIED"),
	HOLD(3, "HOLD"),
	CANCELLED(4, "CANCELLED");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private LeaveStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static LeaveStatus containId(int id) {
		for(LeaveStatus t : LeaveStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return FOR_APPROVAL;
	}
	
	public static LeaveStatus containName(String name) {
		for(LeaveStatus t : LeaveStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return FOR_APPROVAL;
	}
	
}
