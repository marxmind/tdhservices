package com.italia.enm;

public enum EventType {

	FULLBOARD(0, "FULL BOARD"),
	RESTO_BUFFET(1, "RESTO BUFFET"),
	ACCOMMODATION_BUFFET(2, "ACCOMMODATION BUFFET"),
	DINNER(3, "DINNER"),
	LUNCH(4, "LUNCH"),
	VENUE_RENT(5, "VENUE RENT"),
	SPECIAL_EVENT(6, "SPECIAL EVENT"),
	WEDDING(7, "WEDDING"),
	BIRTHDAY(8, "BIRTHDAY");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private EventType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static EventType containId(int id) {
		for(EventType t : EventType.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return FULLBOARD;
	}
	
	public static EventType containName(String name) {
		for(EventType t : EventType.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return FULLBOARD;
	}
	
}
