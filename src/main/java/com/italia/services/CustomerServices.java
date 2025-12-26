package com.italia.services;

import java.util.List;

import com.italia.controller.CustomerProfile;
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

@Path("customer")
public class CustomerServices {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CustomerProfile> list(@Context HttpHeaders headers){
		System.out.println("Loading customer...");
		List<CustomerProfile> rsvs =  CustomerProfile.getAll(" ORDER BY cid DESC");
		System.out.println("Loaded "+ rsvs.size() +" current customer...");
		return rsvs;
	}
	
	@GET
	@Path("/search/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		System.out.println("GET customer " + name);
		List<CustomerProfile> rsvs =  CustomerProfile.getAll(" AND fullname like '%"+ name +"%'");
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET customer " + id);
		CustomerProfile.delete("UPDATE customerprofile set isactivec=0 WHERE cid=?" + id, new String[0]);
		return Response.ok(id, MediaType.APPLICATION_JSON).build();
		
	}
	
}
