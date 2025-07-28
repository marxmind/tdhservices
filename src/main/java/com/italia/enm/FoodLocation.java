package com.italia.enm;

public enum FoodLocation {
	RESTO(0, "Restaurant"),
	KITCHEN(1, "Kitchen"),
	BAR2(2, "Bar"),
	FUNCTION_HALL(3, "Function Hall"),
	ACCOMMODATION(4, "Accommodation"),
	HOUSE_KEEPING(5, "House Keeping"),
	STOCK_ROOM(6, "Stock Room");
	
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	private FoodLocation(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public static FoodLocation containId(int id) {
		for(FoodLocation t : FoodLocation.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return RESTO;
	}
	
	public static FoodLocation containName(String name) {
		for(FoodLocation t : FoodLocation.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return RESTO	;
	}
}
