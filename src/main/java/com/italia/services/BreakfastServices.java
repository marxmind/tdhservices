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

@Path("breakfast")
public class BreakfastServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Breakfast> list(@Context HttpHeaders headers){
		System.out.println("Loading breakfast...");
		List<Breakfast> breaks = Breakfast.getAll(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), null);
		System.out.println("Loaded "+ breaks.size() +" breakfast...");
		return breaks;
	}
	
	@GET
	@Path("/search/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("date") String date) {
		System.out.println("GET breakfast date " + date);
		List<Breakfast> breaks = Breakfast.getAll(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), date);
		for(Breakfast bk : breaks) {
			for(BreakfastItems item : bk.getItems()) {
				System.out.println("item: " + item.getFood().getFoodName());
			}
		}
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
	public Response add(Breakfast brk) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  Breakfast.save(brk).getId();
		
		for(BreakfastItems item : brk.getItems()) {
			
			double qty = 0;
			
			if(item.getId()==0) {
				qty = BreakfastItems.isExisting(item.getFood().getFid(), item.getBreakfast().getId());
			}
			
			if(item.getQty()<=0) {
				item.delete();
			}else {
				double newQty = item.getQty() + qty;
				item.setQty(newQty);
				item.setBreakfast(Breakfast.builder().id(newSupplierId).build());
				item.save();
				System.out.println("saving.... items");
			}
		}
		
		
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET payables id " + id);
		Breakfast.delete("UPDATE breakfast SET isactiveb=0 WHERE bid=" + id, new String[0]);
		List<Breakfast> rsvs = Breakfast.getAll(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), null);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
