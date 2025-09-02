package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.italia.controller.ResortInventoryTransaction;
import com.italia.enm.PropertyGroup;
import com.italia.enm.PropertyLocation;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("propertytran")
public class PropertyTranServices {

	@GET
	@Path("/all/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchProperty(@PathParam("param") String param) {
		List<ResortInventoryTransaction> flds = new ArrayList<ResortInventoryTransaction>();
		System.out.println("GET propertytran  " + param);
	
		
		if("all".equalsIgnoreCase(param)) {
			flds = ResortInventoryTransaction.retrieve(" ORDER BY prop.resname", new String[0]);
		}else {
			flds = ResortInventoryTransaction.retrieve(" AND prop.resname like '%"+ param +"%'  ORDER BY prop.resname", new String[0]);
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
	public Response add(ResortInventoryTransaction prop) throws URISyntaxException {
		System.out.println("POST ADDING PROPERTY....");
		
		System.out.println("property:" + prop.getProperty().getId());
		System.out.println("location:" + PropertyLocation.containId(prop.getLocationId()).getName());
		System.out.println("group:" + PropertyGroup.containId(prop.getGroupId()).getName());
		
		long id =  ResortInventoryTransaction.save(prop).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET property id " + id);
		ResortInventoryTransaction.delete("UPDATE resortinventorytrans SET isactivet=0 WHERE tid=" + id, new String[0]);
		List<ResortInventoryTransaction> rsvs =  ResortInventoryTransaction.retrieve(" ORDER BY tr.tid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	
}
