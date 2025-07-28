package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.House;
import com.italia.controller.User;

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
@Path("user")
public class UserServices {

	private int id;
	private String username;
	private String password;
	private String fullName;
	private int isActive;
	private int accessLevel;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> list(@Context HttpHeaders headers){
		System.out.println("Loading users...");
		List<User> stocks = User.getAllUser();
		System.out.println("Loaded "+ stocks.size() +" users...");
		return stocks;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET Path id");
		User stock = User.getById(id);
		if (stock != null) {
			return Response.ok(stock, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(User user) throws URISyntaxException {
		System.out.println("POST");
		int newUserId =  User.save(user).getId();
		URI uri = new URI("/add/" + newUserId);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, User user) {
		user.setId(id);
		System.out.println("PUT:" + id);
		System.out.println("id: "+ user.getId() +" username: " + user.getUsername() + " fullname:" + user.getFullName());
		if (User.save(user).getId()>0) {
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
		if (User.delete(id)) {
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
}
