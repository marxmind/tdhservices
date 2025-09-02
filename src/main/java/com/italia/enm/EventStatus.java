package com.italia.enm;

public enum EventStatus {
	
	DEALING(0, "DEALING WITH CLIENT"),
	RESERVED(1, "RESERVED"),
	IN_PROGRESS(2, "IN PROGRESS"),
	SETTLED(3, "SETTLED"),
	EVENT_DONE_WITH_UNPAID(4, "EVENT DONE WITH UNPAID PAYMENTS"),
	CANCELLED(5, "CANCELLED"),
	REBOOKING(6, "REBOOKING");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private EventStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static EventStatus containId(int id) {
		for(EventStatus t : EventStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return DEALING;
	}
	
	public static EventStatus containName(String name) {
		for(EventStatus t : EventStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return DEALING;
	}
	
}
