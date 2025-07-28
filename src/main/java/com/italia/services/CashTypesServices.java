package com.italia.services;

import java.util.List;
import com.italia.controller.CashTypes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Path("cashtypes")
public class CashTypesServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CashTypes> list(@Context HttpHeaders headers){
		System.out.println("Loading cash process...");
		List<CashTypes> cash = CashTypes.getAll();
		System.out.println("Loaded "+ cash.size() +" cashtypes...");
		return cash;
	}
	
}
