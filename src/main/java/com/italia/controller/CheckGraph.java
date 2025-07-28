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
public class CheckGraph {
	private String id;
	private String index;
	private String month;
	private String fund;
	private String total;
	private String year;
}
