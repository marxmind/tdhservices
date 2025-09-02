package com.italia.enm;

public enum Department {

	MAIN(0, "MAIN OFFICE"),
	ADMIN(1, "ADMINISTRATOR"),
	STOCKROOM(2, "STOCK ROOM"),
	RESTAURANT(3, "RESTAURANT"),
	KITCHEN(4, "KITCHEN"),
	HOUSEKEEPING(5, "HOUSE KEEPING"),
	FO(6, "FRONT DESK"),
	CASHIER(7, "CASHIER"),
	PURCHASER(8, "PURCHASER"),
	GUARD(9, "SECURITY GUARD"),
	UTILITY(10, "UTILITY"),
	MAINTENANCE(11, "MAINTENANCE");
	
	
	private int id;
	private String name;
	
	private Department(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static Department containId(int id) {
		for(Department t : Department.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return KITCHEN;
	}
	
	public static Department containName(String name) {
		for(Department t : Department.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return KITCHEN;
	}
	
}
