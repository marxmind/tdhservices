package com.italia.services;

import java.util.List;

import com.italia.controller.GatePass;
import com.italia.controller.Reservation;
import com.italia.utils.DateUtils;

import jakarta.ws.rs.GET;
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
	
	
}
