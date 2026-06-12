package com.italia.enm;

public enum PulseStatus {

	
	WAITING_FOR_ACTION(0, "WAITING FOR ACTION"),
	IN_PROGRESS(1, "IN PROGRESS"),
	COMPLETED(2, "COMPLETED"),
	ON_HOLD(3, "ON HOLD"),
	CANCELLED(4, "CANCELLED");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	private PulseStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public static PulseStatus containId(int id) {
		for(PulseStatus t : PulseStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return WAITING_FOR_ACTION;
	}
	
	public static PulseStatus containName(String name) {
		for(PulseStatus t : PulseStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return WAITING_FOR_ACTION;
	}
	
}
