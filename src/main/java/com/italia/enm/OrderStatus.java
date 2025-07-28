package com.italia.enm;

public enum OrderStatus {

	COOKING(0, "Cooking"),
	FOR_SERVING(1, "For Serving"),
	COMPLETED(2, "Completed");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	private OrderStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public static OrderStatus containId(int id) {
		for(OrderStatus t : OrderStatus.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return COOKING;
	}
	
	public static OrderStatus containName(String name) {
		for(OrderStatus t : OrderStatus.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return COOKING;
	}
	
}
