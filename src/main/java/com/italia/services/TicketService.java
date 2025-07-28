package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.Tickets;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Path("ticket")
public class TicketService {
	
	private long id;
	private String dateTrans;
	private int numberOfGuest;
	private int type;
	private double amount;
	private int isActive;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Tickets> list(@Context HttpHeaders headers){
		System.out.println("Loading tickets...");
		List<Tickets> ticket = Tickets.getALLTickets();
		System.out.println("Loaded "+ ticket.size() +" tickets...");
		return ticket;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET Path id");
		Tickets ticekt = Tickets.getById(id);
		if (ticekt != null) {
			return Response.ok(ticekt, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(Tickets ticekt) throws URISyntaxException {
		System.out.println("POST");
		long newUserId =  Tickets.save(ticekt).getId();
		URI uri = new URI("/add/" + newUserId);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, Tickets ticket) {
		ticket.setId(id);
		System.out.println("PUT:" + id);
		System.out.println("id: "+ ticket.getId() +" numberofguest: " + ticket.getNumberOfGuest() + " amount:" + ticket.getAmount());
		if (Tickets.save(ticket).getId()>0) {
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
		System.out.println("user Id:" + id);
		if (Tickets.delete(id)) {
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
	
}
