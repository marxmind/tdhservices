package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Email;
import com.italia.controller.Employee;

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

@Path("emails")
public class EmailServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Email> list(@Context HttpHeaders headers){
		System.out.println("Loading email report...");
		List<Email> rsvs =  Email.retrieve(" ORDER BY emid DESC LIMIT 100", new String[0]);
		System.out.println("Loaded "+ rsvs.size() +" current email reports...");
		return rsvs;
	}
	
	@GET
	@Path("/individual/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response individual(@PathParam("id") int id) {
		System.out.println("GET member " + id);
		//List<Email> rsvs =  Email.retrieve(" AND memberReceiverid="+ id + " ORDER BY emid DESC LIMIT 100", new String[0]);
		List<Email> rsvs =  Email.retrieve(" AND (memberSenderId="+ id + " OR memberReceiverid="+ id +" ) ORDER BY timestampem", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/individual/unread/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unread(@PathParam("id") int id) {
		System.out.println("GET member " + id);
		List<Email> rsvs =  Email.retrieve(" AND memberReceiverid="+ id + " AND isread=0 ORDER BY emid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/individual/all/{read}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unreadall(@PathParam("read") int read) {
		System.out.println("GET member " + read);
		List<Email> rsvs =  Email.retrieve(" AND isread="+ read  +" ORDER BY emid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(Email email) throws URISyntaxException {
		System.out.println("POST: " + email.getId());
		System.out.println("id: "+ email.getId());
		long id = 0;
		if(email.getMemberReceiverId()==0) {
			for(Employee m : Employee.getAll()) {
				if(m.getEid()==email.getMemberSenderId()) {
					//do nothing
					//this is for administrative
				}else {
				email.setId(0);
				email.setMemberReceiverId(m.getEid());
				id =  Email.save(email).getId();
				}
			}
		}else {
			id =  Email.save(email).getId();
		}
		
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/update/read/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("param") String param) {
		
		long emid = Long.valueOf(param.split(":")[0]);
		long memberId = Long.valueOf(param.split(":")[1]);
		int isRead = Integer.valueOf(param.split(":")[2]);
		Email email = Email.builder().id(emid).isRead(isRead).build();
		Email em = Email.save(email);
		
		System.out.println("GET email " + emid);
		List<Email> rsvs =  Email.retrieve(" AND emid="+ em.getId() + " ORDER BY emid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") long id) {
		System.out.println("delete email " + id);
		Email.delete("UPDATE email SET isactiveem=0 WHERE emid=" + id, new String[0]);
		List<Email> rsvs =  Email.retrieve(" ORDER BY emid DESC LIMIT 100", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	
}
