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
public class CollectionGraph {
	private String id;
	private String index;
	private String form;
	private String amount;
	private String year;
}
