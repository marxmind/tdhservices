package com.italia.services;

import java.util.List;
import com.italia.controller.Fields;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("fields")
public class FieldsServices {
	
	
	@GET
	@Path("/summary/{year}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("year") String year) {
		System.out.println("GET summary " + year);
		List<Fields> cash = Fields.getYearSummary(Integer.valueOf(year));
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response specific(@PathParam("param") String param) {
		System.out.println("GET search param " + param);
		
		String vals[] = param.split(":");
		int year = Integer.valueOf(vals[0]);
		int month = Integer.valueOf(vals[1]);
		String date = vals[2].equalsIgnoreCase("null")? null : vals[2];
		
		List<Fields> cash = Fields.getYearSummary(year, month, date);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
