package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.CashProcess;
import com.italia.controller.House;
import com.italia.controller.Kitchen;
import com.italia.controller.Stocks;
import com.italia.utils.DateUtils;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("cashprocess")
public class CashProcessServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CashProcess> list(@Context HttpHeaders headers){
		System.out.println("Loading cash process...");
		List<CashProcess> cash = CashProcess.getAll();
		System.out.println("Loaded "+ cash.size() +" cashprocess...");
		return cash;
	}
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("param") String param) {
		System.out.println("GET search " + param);
		String date = param.split(":")[0];
		int userid = Integer.parseInt( param.split(":")[1]);
		List<CashProcess> cash = CashProcess.getByDate(date, userid);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(CashProcess cash) throws URISyntaxException {
		System.out.println("POST");
		int newStockId =  CashProcess.save(cash).getId();
		URI uri = new URI("/add/" + newStockId);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, CashProcess cash) {
		cash.setId(id);
		System.out.println("PUT:" + id);
		System.out.println("id: "+ cash.getId() +" name: " + cash.getCashType() + " amount:" + cash.getAmount());
		if (CashProcess.save(cash).getId()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response delete(@PathParam("id") int id) {
		System.out.println("DELETE");
		System.out.println("cash Id:" + id);
		if (CashProcess.delete(id)) {
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
}
