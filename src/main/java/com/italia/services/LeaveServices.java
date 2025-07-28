package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.LeaveForm;
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
@Path("leave")
public class LeaveServices {

	private long id;
	private String fileDate;
	private long employeeId;
	private int type;
	private String purpose;
	private String reliever;
	private int days;
	private String startDate;
	private String endDate;
	private int status;
	private String notes;
	private int isActive;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<LeaveForm> list(@Context HttpHeaders headers){
		System.out.println("Loading leaves...");
		List<LeaveForm> leaves =  LeaveForm.latestAll();
		System.out.println("done Loaded "+ leaves.size() +" leaves...");
		return leaves;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(LeaveForm leave) throws URISyntaxException {
		System.out.println("POST ADDING LEAVES....");
		long id =  LeaveForm.save(leave).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	
	@GET
	@Path("/searchid/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LeaveForm> searchId(@PathParam("id") int id) {
		System.out.println("search eid:" + id);
		List<LeaveForm> leaves =  LeaveForm.searchId(id);
		System.out.println("done Loaded "+ leaves.size() +" leaves...");
		return leaves;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("update")
	public Response update(LeaveForm leave) throws URISyntaxException {
		System.out.println("POST UPDATE LEAVES....");
		long id =  LeaveForm.save(leave).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("Deleting:" + id);
		System.out.println("id: "+ id);
		if (id>0) {
			LeaveForm leave = LeaveForm.builder().id(id).build();
			leave.delete(true);
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
}
