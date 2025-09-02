package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.italia.controller.PropertyInventory;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("property")
public class PropertyServices {

	@GET
	@Path("/all/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchProperty(@PathParam("param") String param) {
		List<PropertyInventory> flds = new ArrayList<PropertyInventory>();
		System.out.println("GET property  " + param);
	
		
		if("all".equalsIgnoreCase(param)) {
			flds = PropertyInventory.retrieve(" ORDER BY resname", new String[0]);
		}else {
			flds = PropertyInventory.retrieve(" AND resname like '%"+ param +"%'  ORDER BY resname", new String[0]);
		}
		
		
		if (flds != null) {
			return Response.ok(flds, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(PropertyInventory prop) throws URISyntaxException {
		System.out.println("POST ADDING PROPERTY....");
		long id =  PropertyInventory.save(prop).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET property id " + id);
		PropertyInventory.delete("UPDATE propertyinventory SET isactivep=0 WHERE pid=" + id, new String[0]);
		List<PropertyInventory> rsvs =  PropertyInventory.retrieve(" ORDER BY pid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
