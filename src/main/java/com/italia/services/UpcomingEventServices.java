package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Horseback;
import com.italia.controller.Supplier;
import com.italia.controller.UpcomingEvents;

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

@Path("events")
public class UpcomingEventServices {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<UpcomingEvents> list(@Context HttpHeaders headers){
		System.out.println("Loading horseback...");
		List<UpcomingEvents> events = UpcomingEvents.getAll(0);
		System.out.println("Loaded "+ events.size() +" events...");
		return events;
	}
	
	@GET
	@Path("/search/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		System.out.println("GET events " + name);
		List<UpcomingEvents> sups =  UpcomingEvents.retrieve(" AND ev.companyname like '%"+ name +"%' OR cz.fullname like '%"+ name +"%'", new String[0]);
		if (sups != null) {
			return Response.ok(sups, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

	}
	
	@GET
	@Path("/month/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response month(@PathParam("param") String param) {
		System.out.println("GET events " + param);
		
		int month = Integer.valueOf(param.split(":")[1]);
		int year = Integer.valueOf(param.split(":")[0]);
		
		List<UpcomingEvents> sups =  UpcomingEvents.retrieve(" AND month(ev.dateeventstart)="+ month +" AND year(ev.dateeventstart)="+ year , new String[0]);
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
	public Response add(UpcomingEvents event) throws URISyntaxException {
		System.out.println("POST");
	
		long newSupplierId =  UpcomingEvents.save(event).getId();
		URI uri = new URI("/add/" + newSupplierId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET event id " + id);
		Supplier.delete("UPDATE upcomingevents SET isactivee=0 WHERE evid=" + id, new String[0]);
		List<Horseback> rsvs =  Horseback.retrieve(" ORDER BY ev.evid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
