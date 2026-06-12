package com.italia.enm;

public enum RequestSavingStatus {

	REQUEST(0, "REQUEST"),
	APPROVED(1, "APPROVED"),
	DENIED(2, "DENIED"),
	CANCELLED(3, "CANCELLED");
	
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	private RequestSavingStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public static RequestSavingStatus containId(int id) {
		for(RequestSavingStatus t : RequestSavingStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return REQUEST;
	}
	
	public static RequestSavingStatus containName(String name) {
		for(RequestSavingStatus t : RequestSavingStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return REQUEST;
	}
	
}
