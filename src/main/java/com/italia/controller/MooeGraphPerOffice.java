package com.italia.controller;

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
public class MooeGraphPerOffice {

	private String id;
	private String name;
	private String year;
	private String amount;
	
	
}
