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
public class BusinessGraph {
	private String id;
	private String index;
	private String month;
	private String type;
	private String total;
	private String year;
}
