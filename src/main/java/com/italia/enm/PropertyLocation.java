package com.italia.enm;

public enum PropertyLocation {

	FO(0, "FRONT DESK AREA"),
	RESTO(1, "RESTAURANT AREA"),
	HOUSEKEEPING_AREA(2, "HOUSEKEEPING AREA"),
	GURAD_HOUSE(3,"GUARD HOUSE AREA"),
	SOUVENER_SHOP(4, "SOUVINER SHOP"),
	KITCHEN(5, "KITCHEN AREA"),
	UTILITY_ROOM(6, "RESTO AREA"),
	GLAMPING_1(7, "GLAMPING ONE"),
	GLAMPING_2(8, "GLAMPING TWO"),
	GLAMPING_3(9, "GLAMPING THREE"),
	GLAMPING_4(10, "GLAMPING FOUR"),
	GLAMPING_5(11, "GLAMPING FIVE"),
	GLAMPING_6(12, "GLAMPING SIX"),
	GLAMPING_7(13, "GLAMPING SEVEN"),
	GLAMPING_8(14, "GLAMPING EIGHT"),
	VILLA_SOCIMER(15, "VILLA SOCIMER"),
	VILLA_MARIE(16, "VILLA MARIE"),
	VILLA_MERCEDES(17, "VILLA MERCEDES"),
	VILLA_ADORA(18, "VILLA ADORA"),
	EXECUTIVE_VILLA(19, "EXECUTIVE VILLA"),
	COFFEE_SHOP(20, "COFFEE SHOP"),
	FUNCTION_HALL_SMALL(21, "FUNCTION HALL SMALL"),
	FUNCTION_HALL_BIG(22, "FUNCTION HALL BIG"),
	ELECTRICAL_TOOL_AREA(23, "ELECTRICAL TOOL AREA"),
	PLUMBING_TOOL_AREA(24, "PLUMBING TOOL AREA"),
	MAIN_OFFICE(25, "MAIN OFFICE"),
	SUB_OFFICE(26, "SUB OFFICE"),
	MAINTENANCE_TOOL_AREA(27, "MAINTENANCE TOOL AREA");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private PropertyLocation(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static PropertyLocation containId(int id) {
		for(PropertyLocation t : PropertyLocation.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return HOUSEKEEPING_AREA;
	}
	
	public static PropertyLocation containName(String name) {
		for(PropertyLocation t : PropertyLocation.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return HOUSEKEEPING_AREA;
	}
	
}
