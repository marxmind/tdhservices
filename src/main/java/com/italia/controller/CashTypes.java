package com.italia.controller;

import java.util.ArrayList;
import java.util.List;

import com.italia.enm.CashType;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CashTypes {

	private int id;
	private String name;
	
	public static List<CashTypes> getAll(){
		List<CashTypes> types = new ArrayList<CashTypes>();
		
		for(CashType type : CashType.values()) {
			CashTypes tp = builder().id(type.getId()).name(type.getName()).build();
			types.add(tp);
		}
		
		return types;
	}
	
	
}
