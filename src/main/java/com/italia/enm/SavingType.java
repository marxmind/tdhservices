package com.italia.enm;

public enum SavingType {
	
	DEDUCTED_FROM_SALARY(1, "Deducted From Salary"),
	MONTHLY_THIRTEEN_MONTH_DEDUCTION(2, "Monthly thirteen month accummulated"),
	DIRECT_DEDUCTION(3, "Direct Saving Deduction");
	
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	private SavingType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public static SavingType containId(int id) {
		for(SavingType t : SavingType.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return DEDUCTED_FROM_SALARY;
	}
	
	public static SavingType containName(String name) {
		for(SavingType t : SavingType.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return DEDUCTED_FROM_SALARY;
	}
	
}
