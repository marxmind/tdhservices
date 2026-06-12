package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.FoodItem;
import com.italia.controller.FoodOrder;
import com.italia.controller.KitchenOrder;
import com.italia.controller.StaffReport;
import com.italia.controller.StaffReportTrans;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("staff-report-trans")
public class StaffReportTransService {

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StaffReportTrans> list(@Context HttpHeaders headers){
		System.out.println("Loading Loading StaffReportTrans......");
		List<StaffReportTrans> items =  StaffReportTrans.retrieve(" ORDER BY stid DESC", new String[0]);
		System.out.println("done Loaded "+ items.size() +" StaffReportTrans...");
		return items;
	}
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StaffReportTrans> search(@PathParam("param") String  param){
		System.out.println("Loading Loading StaffReportTrans......");
		List<StaffReportTrans> items =  StaffReportTrans.retrieve(" AND descriptions like '%"+ param +"%' ORDER BY stid DESC", new String[0]);
		System.out.println("done Loaded "+ items.size() +" StaffReportTrans...");
		return items;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public StaffReportTrans add(StaffReportTrans staff) throws URISyntaxException {
		System.out.println("POST==================== " + staff.getEid());
		StaffReportTrans st = StaffReportTrans.save(staff);
		//URI uri = new URI("/add/" + st.getId());
		//return Response.created(uri).build();
		StaffReportTrans cash = StaffReportTrans.retrieve(" AND stid=" + staff.getId(), new String[0]).get(0);
		return cash;
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchStaff(@PathParam("id") int  id) {
		System.out.println("GET staff id " + id);
		List<StaffReportTrans> cash = StaffReportTrans.retrieve(" AND sid=" + id, new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete/{id}")
	public Response delete(@PathParam("id") int id) {
		System.out.println("DELETE");
		System.out.println("staffreporttrans Id:" + id);
		StaffReportTrans.delete("UPDATE staffreporttrans SET isactivets=0 WHERE stid=" + id, new String[0]);
		return Response.ok(new StaffReportTrans(), MediaType.APPLICATION_JSON).build();
	}
	
}
