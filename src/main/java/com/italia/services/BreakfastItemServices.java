package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Breakfast;
import com.italia.controller.BreakfastItems;
import com.italia.utils.DateUtils;

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

@Path("breakfastitems")
public class BreakfastItemServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<BreakfastItems> list(@Context HttpHeaders headers){
		System.out.println("Loading breakfast...");
		List<BreakfastItems> breaks = BreakfastItems.getAll(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), null);
		System.out.println("Loaded "+ breaks.size() +" breakfast...");
		return breaks;
	}
	
	@GET
	@Path("/search/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("date") String date) {
		System.out.println("GET breakfastitem date " + date);
		List<BreakfastItems> breaks = BreakfastItems.getAll(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), date);
		if (breaks != null) {
			return Response.ok(breaks, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(BreakfastItems brk) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  BreakfastItems.save(brk).getId();
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET break id " + id);
		Breakfast.delete("UPDATE breakfastitems SET isactivebt=0 WHERE btid=" + id, new String[0]);
		List<BreakfastItems> rsvs = BreakfastItems.getAll(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), null);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
