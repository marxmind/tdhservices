package com.italia.services;

import java.util.List;
import com.italia.controller.Employee;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Path("employee")
public class EmployeeServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Employee> list(@Context HttpHeaders headers){
		System.out.println("Loading employee...");
		List<Employee> stocks = Employee.getAll();
		System.out.println("Loaded "+ stocks.size() +" employee...");
		return stocks;
	}
	
}
