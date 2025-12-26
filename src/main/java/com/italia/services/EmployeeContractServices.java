package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Employee;
import com.italia.controller.EmployeeContractDtls;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("contract")
public class EmployeeContractServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<EmployeeContractDtls> list(@Context HttpHeaders headers){
		System.out.println("Loading employee contract...");
		List<EmployeeContractDtls> cons = EmployeeContractDtls.retrieve(" ORDER BY conid DESC", new String[0]);
		System.out.println("Loaded "+ cons.size() +" employee contract...");
		return cons;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(EmployeeContractDtls e) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  EmployeeContractDtls.save(e).getEid();
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/search/{eid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchId(@PathParam("eid") int eid) {
		System.out.println("GET contract eid " + eid);
		List<EmployeeContractDtls> rsvs = EmployeeContractDtls.retrieve(" AND eid="+eid + " ORDER BY conid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET break id " + id);
		EmployeeContractDtls.delete("UPDATE employeecontractdtls SET isactivecont=0 WHERE conid=" + id, new String[0]);
		List<EmployeeContractDtls> rsvs = EmployeeContractDtls.retrieve(" ORDER BY conid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

}
