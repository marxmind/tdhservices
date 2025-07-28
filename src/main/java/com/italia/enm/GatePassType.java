package com.italia.enm;

public enum GatePassType {
	
	LEFT_OVER(1, "LEFT OVER"),
	CASH_EXCHANGE(2, "CASH EXCHANGE"),
	CUSTOMER_TIP(3, "CUSTOMER TIP"),
	EMPLOYEE_OS(4,"EMPLOYEE ORDER SLIP"),
	BRING_HOME_MATERIAL(5,"BRING HOME MATERIAL"),
	OTHER(6, "OTHERS"),
	CASH_ADVANCE(7, "CASH ADVANCE"),
	CASH_FROM_OTHER_INCOME(8, "CASH FROM OTHER INCOME");
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private GatePassType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static GatePassType containId(int id) {
		for(GatePassType t : GatePassType.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return OTHER;
	}
	
	public static GatePassType containName(String name) {
		for(GatePassType t : GatePassType.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return OTHER;
	}
	
}