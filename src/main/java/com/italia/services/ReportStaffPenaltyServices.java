package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.EmployeePayable;
import com.italia.controller.FormProcess;
import com.italia.controller.LeaveForm;
import com.italia.controller.ReportStaffPenalty;

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

@Path("penaltyreport")
public class ReportStaffPenaltyServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReportStaffPenalty> list(@Context HttpHeaders headers){
		System.out.println("Loading penalty report...");
		List<ReportStaffPenalty> rsvs =  ReportStaffPenalty.retrieve(" ORDER BY rid DESC", new String[0]);
		System.out.println("Loaded "+ rsvs.size() +" current penalty reports...");
		return rsvs;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(ReportStaffPenalty penalty) throws URISyntaxException {
		System.out.println("POST ADDING PENALTY....");
		long id =  ReportStaffPenalty.save(penalty).getId();
		URI uri = new URI("/add/" + id);
		
		if(penalty.getStatus()==1) {//APPROVED
			
		FormProcess form =FormProcess.builder()
			.id(0)
			.amount(penalty.getPenaltyRate())
			.dateTrans(penalty.getDateTrans())
			.eid(Integer.valueOf(penalty.getReportedStaffEid()+""))
			.purpose(penalty.getRemarks())
			.statusName("For Approval")
			.build();
		
		FormProcess update = FormProcess.save(form);
		update.setStatusName("Admin Approved");
		update.save();
			
			EmployeePayable.updateEmployeePayable(Integer.valueOf(penalty.getReportedStaffEid()+""), penalty.getPenaltyRate(), "ADD");
		}
		
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/search/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		System.out.println("GET penalty staff name " + name);
		List<ReportStaffPenalty> rsvs =  ReportStaffPenalty.retrieve(" AND e.fullname like '%"+ name +"%' ORDER BY rid DESC", new String[0]);
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
		System.out.println("GET penalty staff id " + id);
		ReportStaffPenalty.delete("UPDATE reportstaffpenalty SET isactivereport=0 WHERE rid=" + id, new String[0]);
		List<ReportStaffPenalty> rsvs =  ReportStaffPenalty.retrieve(" ORDER BY rid DESC", new String[0]);
		if (rsvs != null) {
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
