package com.italia.enm;

public enum DeliveryStatus {

	REQUEST(0, "REQUEST"),
	DELIVERED(1, "DELIVERED"),
	PENDING(2, "PENDING"),
	CANCELLED(3, "CANCELLED"),
	DRAFT(4, "DRAFT"),
	FOLLOWUP(5, "FOLLOW-UP");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	private DeliveryStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public static DeliveryStatus containId(int id) {
		for(DeliveryStatus t : DeliveryStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return REQUEST;
	}
	
	public static DeliveryStatus containName(String name) {
		for(DeliveryStatus t : DeliveryStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return REQUEST;
	}
	
}
