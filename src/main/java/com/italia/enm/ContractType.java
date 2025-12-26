package com.italia.enm;

public enum ContractType {

	PERMANENT(0, "PERMANENT"),
	TERM(1,"TERM OR FIXED"),
	SEASONAL(2, "SEASONAL"),
	PROBATIONARY(3,"PROBATIONARY"),
	PROJECT_BASED(4,"PROJECT BASED"),
	CONTRACTUAL(5,"CONTRACTUAL"),
	PERMANENT_SALARY_INCREASED(6, "PERMANENT SALARY INCREASED"),
	AGENCY(7, "AGENCY");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private ContractType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(ContractType type : ContractType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return ContractType.TERM.getName();
	}
	public static int typeId(String name){
		for(ContractType type : ContractType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return ContractType.TERM.getId();
	}
	
	public static ContractType type(int id){
		for(ContractType type : ContractType.values()){
			if(id==type.getId()){
				return type;
			}
		}
		return ContractType.TERM;
	}
	
}
