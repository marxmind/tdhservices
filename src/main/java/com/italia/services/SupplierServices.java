package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.PropertyInventory;
import com.italia.controller.Supplier;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("supplier")
public class SupplierServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Supplier> list(@Context HttpHeaders headers){
		System.out.println("Loading supplier...");
		List<Supplier> suppliers = Supplier.getAll();
		System.out.println("Loaded "+ suppliers.size() +" payables...");
		return suppliers;
	}
	
	@GET
	@Path("/search/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		System.out.println("GET supplier " + name);
		List<Supplier> sups =  Supplier.retrieve(" AND fullname like '%"+ name +"%'", new String[0]);
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
	public Response add(Supplier supplier) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  Supplier.save(supplier).getId();
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET supplier id " + id);
		Supplier.delete("UPDATE supplier SET isactives=0 WHERE sid=" + id, new String[0]);
		List<Supplier> rsvs =  Supplier.retrieve(" ORDER BY sid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
