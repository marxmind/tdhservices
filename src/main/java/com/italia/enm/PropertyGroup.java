package com.italia.enm;

public enum PropertyGroup {
	
	HK_BODEGA(0, "HOUSEKEEPING BODEGA"),
	IN_ROOM(1, "IN ROOM"),
	HK_STOCK_ROOM(2, "IN STOCK FOR ROOM AREA"),
	GUARD_HOUSE(3,"GUARD HOUSE"),
	FOAM(4, "FOAM"),
	BED(5, "BED"),
	RESTO_AREA(6, "RESTO AREA"),
	KITCHEN_1(7, "KITCHEN GROUP 1"),
	KITCHEN_2(8, "KITCHEN GROUP 2"),
	KITCHEN_3(9, "KITCHEN GROUP 3"),
	KITCHEN_4(10, "KITCHEN GROUP 4"),
	KITCHEN_5(11, "KITCHEN GROUP 5"),
	KITCHEN_6(12, "KITCHEN GROUP 6"),
	KITCHEN_7(13, "KITCHEN GROUP 7"),
	KITCHEN_8(14, "KITCHEN GROUP 8"),
	UTILITY(15, "UTILITY"),
	MAINTENANCE(16, "MAINTENANCE"),
	FRONT_DESK(17, "FRONT DESK"),
	ELECTRICAL_TOOLS(18, "ELECTRICAL TOOLS"),
	PLUMBER_TOOLS(19, "PLUMBER TOOLS"),
	SOUVENIR(20, "SOUVENIR"),
	COFFEE_SHOP(21, "COFFEE SHOP"),
	FUNCTION_HALL_SMALL(22, "FUNCTION HALL SMALL"),
	FUNCTION_HALL_BIG(23, "FUNCTION HALL BIG"),
	MAIN_OFFICE(24, "MAIN OFFICE"),
	SUB_OFFICE(25, "SUB OFFICE");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private PropertyGroup(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static PropertyGroup containId(int id) {
		for(PropertyGroup t : PropertyGroup.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return HK_BODEGA;
	}
	
	public static PropertyGroup containName(String name) {
		for(PropertyGroup t : PropertyGroup.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return HK_BODEGA;
	}
	
}
