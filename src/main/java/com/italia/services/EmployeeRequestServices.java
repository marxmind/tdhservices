package com.italia.services;

import java.util.List;
import com.italia.controller.EmployeeRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Path("erequest")
public class EmployeeRequestServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<EmployeeRequest> list(@Context HttpHeaders headers){
		System.out.println("Loading Payables...");
		List<EmployeeRequest> recs = EmployeeRequest.retrive(" ORDER BY rid DESC", new String[0]);
		System.out.println("Loaded "+ recs.size() +" employee requests...");
		return recs;
	}
	
}
