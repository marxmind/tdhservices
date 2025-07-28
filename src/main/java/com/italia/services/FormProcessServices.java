package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.CashAdvanceBalance;
import com.italia.controller.EmployeePayable;
import com.italia.controller.FormProcess;
import com.italia.controller.TimeRecord;

import jakarta.ws.rs.Consumes;
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

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Path("forms")
public class FormProcessServices {

	
	private int id;
	private String dateTrans;
	private double amount;
	private String purpose;
	private String employee;
	private int eid;
	private String statusName;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<FormProcess> list(@Context HttpHeaders headers){
		System.out.println("Loading for approval...");
		List<FormProcess> forms =  FormProcess.getUnprocessedForms();
		System.out.println("Loaded "+ forms.size() +" forms pass...");
		return forms;
	}
	
	@GET
	@Path("/search/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET search id:" + id);
		List<FormProcess> forms = FormProcess.getEmployeeForms(id);
		if (forms != null) {
			return Response.ok(forms, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/name/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getName(@PathParam("name") String name) {
		System.out.println("GET search id:" + id);
		List<FormProcess> forms = FormProcess.getEmployeeByName(name);
		if (forms != null) {
			return Response.ok(forms, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/balanceca/{eid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBalance(@PathParam("eid") int eid) {
		System.out.println(eid);
		List<CashAdvanceBalance> forms = CashAdvanceBalance.getEmployeeBalance(eid);
		if (forms != null) {
			return Response.ok(forms, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/balanceca/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CashAdvanceBalance> allbalance(@Context HttpHeaders headers){
		System.out.println("Loading for all balance...");
		List<CashAdvanceBalance> bals =  CashAdvanceBalance.getAllEmployeeBalance();
		System.out.println("Loaded "+ bals.size() +" balance pass...");
		return bals;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(FormProcess form) throws URISyntaxException {
		System.out.println("POST");
		int eid =  FormProcess.save(form).getEid();
		URI uri = new URI("/add/" + eid);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update/{id}")
	public Response update(@PathParam("id") int id, FormProcess form) {
		System.out.println("Updating:" + id);
		System.out.println("id: "+ form.getId() +" name: " + form.getEmployee());
		
		if (FormProcess.save(form).getId()>0) {
			
			if("Finance Approved".equalsIgnoreCase(form.getStatusName()) ||
					"Admin Approved".equalsIgnoreCase(form.getStatusName())) {
				EmployeePayable.updateEmployeePayable(form.getEid(), form.getAmount(), "ADD");
			}
			
			
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
}
