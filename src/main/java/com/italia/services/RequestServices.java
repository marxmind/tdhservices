package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.Request;
import com.italia.controller.Stocks;

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
import lombok.Data;
import lombok.ToString;


@ToString
@Path("request")
@Data
public class RequestServices {	
	
	private long id;
	private String stockName;
	private String dateTrans;
	private double qty;
	private int department;
	private int stockid;
	private int status;
	private int isActive;
	private int userid;
	private double appqty;
	private String barcode;
	
	/*
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Request> list(@Context HttpHeaders headers){
		System.out.println("Loading stocks...");
		List<Request> reqs = Request.getAllRequests();
		System.out.println("Loaded "+ reqs.size() +" requests...");
		return reqs;
	}*/
	
	@GET
	@Path("/{department}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Request> list(@PathParam("department") int department){
		System.out.println("Loading stocks...");
		List<Request> reqs = Request.getByDepartment(department);
		
		System.out.println("Loaded "+ reqs.size() +" requests...");
		return reqs;
	}
	
	/*
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET Path id");
		Request req = Request.getById(id);
		if (req != null) {
			return Response.ok(req, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}*/
	
	@GET
	@Path("/search/{department}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("department") int department) {
		System.out.println("GET search " + department);
		List<Request> stock = Request.getByDepartment(department);
		if (stock != null) {
			return Response.ok(stock, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(Request req) throws URISyntaxException {
		System.out.println("POST: " + req.getId());
		System.out.println("id: "+ req.getId() +" name: " + req.getStockName() + " qty:" + req.getQty() + " appqty:" + req.getAppqty());
		long newStockId =  Request.save(req).getId();
		URI uri = new URI("/add/" + newStockId);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, Request req) {
		req.setId(id);
		System.out.println("PUT:" + id);
		System.out.println("id: "+ req.getId() +" name: " + req.getStockName() + " qty:" + req.getQty());
		if (Request.save(req).getId()>0) {
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
		System.out.println("Req Id:" + id);
		if (Request.delete(id)) {
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
}