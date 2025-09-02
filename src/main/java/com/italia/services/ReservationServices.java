package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.italia.controller.Breakfast;
import com.italia.controller.BreakfastItems;
import com.italia.controller.GatePass;
import com.italia.controller.Reservation;
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

@Path("reservation")
public class ReservationServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Reservation> list(@Context HttpHeaders headers){
		System.out.println("Loading gatepass...");
		List<Reservation> rsvs =  Reservation.getAll(0, 0, DateUtils.getCurrentDateYYYYMMDD());
		System.out.println("Loaded "+ rsvs.size() +" current reservation...");
		return rsvs;
	}

	@GET
	@Path("/search/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("date") String date) {
		System.out.println("GET reservation " + date);
		List<Reservation> rsvs =  Reservation.getAll(0, 0, date);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/month/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response month(@PathParam("param") String param) {
		System.out.println("GET reservation " + param);
		String[] spl = param.split(":");
		int year = Integer.valueOf(spl[0]);
		int month = Integer.valueOf(spl[1]);
		String date = spl[2].contains("null")? null : spl[2];
		List<Reservation> rsvs =  Reservation.getAll(year, month, date);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/name/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response names(@PathParam("param") String param) {
		System.out.println("GET reservation " + param);
		String name = param.contains("null")? null : param;
		List<Reservation> rsvs =  Reservation.getCustomerNames(name);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/update/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("param") String param) {
		System.out.println("GET reservation " + param);
		List<Reservation> rsvs =  new ArrayList<Reservation>();
		
		if(param.contains(":")) {
			String[] vals = param.split(":");
			long id = Long.valueOf(vals[0]);
			int year = Integer.valueOf(vals[1]);
			int month = Integer.valueOf(vals[2]);
			
			if("CHECKINTIME".equalsIgnoreCase(vals[3])) {
				Reservation.openUpdate("UPDATE reservation SET startTime='"+ DateUtils.getCurrentTIME() +"' WHERE rid=" + id);
			}else if("CHECKOUTTIME".equalsIgnoreCase(vals[3])) {
				Reservation.openUpdate("UPDATE reservation SET endTime='"+ DateUtils.getCurrentTIME() +"' WHERE rid=" + id);
			}else if("PLATENO".equalsIgnoreCase(vals[3])) {
				Reservation.openUpdate("UPDATE reservation SET vehicleplates='"+ vals[4] +"' WHERE rid=" + id);
			}else if("PAX".equalsIgnoreCase(vals[3])) {
				int adult = Integer.valueOf(vals[4]);
				int child = Integer.valueOf(vals[5]);
				Reservation.openUpdate("UPDATE reservation SET adultcount="+ adult +", childcount="+ child +" WHERE rid=" + id);
			}
			
			
			rsvs =  Reservation.getAll(year, month, null);
			
			
		}
		
		
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(Reservation rs) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  Reservation.save(rs).getId();		
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	
}
