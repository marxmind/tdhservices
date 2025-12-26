package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Supplier;
import com.italia.controller.SupplierPayables;

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

@Path("supplierpayables")
public class SupplierPayablesServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SupplierPayables> list(@Context HttpHeaders headers){
		System.out.println("Loading supplier payable...");
		//List<SupplierPayables> suppliers = SupplierPayables.getAll(500);
		List<SupplierPayables> suppliers = SupplierPayables.getItems();
		System.out.println("Loaded "+ suppliers.size() +" payables...");
		return suppliers;
	}
	
	@GET
	@Path("/search/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		System.out.println("GET supplier payable " + name);
		List<SupplierPayables> sups =  SupplierPayables.retrieve(" AND (sup.fullname like '%"+ name +"%' OR tran.description like '%"+ name +"%') ORDER BY tran.stid DESC", new String[0]);
		if (sups != null) {
			return Response.ok(sups, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(SupplierPayables supplier) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  SupplierPayables.save(supplier).getId();
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET payables id " + id);
		Supplier.delete("UPDATE supplierpayables SET isactivest=0 WHERE stid=" + id, new String[0]);
		//List<SupplierPayables> rsvs =  SupplierPayables.retrieve(" ORDER BY stid DESC", new String[0]);
		List<SupplierPayables> rsvs = SupplierPayables.getItems();
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
