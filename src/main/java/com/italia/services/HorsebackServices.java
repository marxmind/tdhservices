package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Horseback;
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

@Path("horseback")
public class HorsebackServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Horseback> list(@Context HttpHeaders headers){
		System.out.println("Loading horseback...");
		List<Horseback> horses = Horseback.getAll(0);
		System.out.println("Loaded "+ horses.size() +" waivers...");
		return horses;
	}
	
	@GET
	@Path("/search/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		System.out.println("GET waiver " + name);
		List<Horseback> sups =  Horseback.retrieve(" AND customer like '%"+ name +"%'", new String[0]);
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
	public Response add(Horseback horse) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  Horseback.save(horse).getId();
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET horse id " + id);
		Supplier.delete("UPDATE horseback SET isactiveh=0 WHERE hid=" + id, new String[0]);
		List<Horseback> rsvs =  Horseback.retrieve(" ORDER BY hid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
