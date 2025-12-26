package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Employee;
import com.italia.controller.OrderSelection;

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

@Path("osselection")
public class OrderSelectionServices {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<OrderSelection> list(@Context HttpHeaders headers){
		System.out.println("Loading gatepass...");
		List<OrderSelection> os =  OrderSelection.getAll();
		System.out.println("Loaded "+ os.size() +" order selection pass...");
		return os;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(OrderSelection e) throws URISyntaxException {
		System.out.println("POST");
		long newSupplierId =  OrderSelection.save(e).getId();
		URI uri = new URI("/add/" + newSupplierId);
		//return Response.created(uri).build();
		if(newSupplierId>0) {
			return Response.status(Response.Status.CREATED).build();
		}else {
			return Response.status(Response.Status.NOT_MODIFIED).build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET osseelction  id " + id);
		//OrderSelection.delete("UPDATE osselections SET isactiveos=0 WHERE osid=" + id, new String[0]);
		OrderSelection.delete(id);
		List<OrderSelection> rsvs = OrderSelection.getAll();
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
