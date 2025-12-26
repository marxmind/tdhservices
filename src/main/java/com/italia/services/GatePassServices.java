package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.CashProcess;
import com.italia.controller.Email;
import com.italia.controller.Employee;
import com.italia.controller.Fields;
import com.italia.controller.FormProcess;
import com.italia.controller.GatePass;
import com.italia.enm.FormType;

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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Path("gatepass")
public class GatePassServices {

	private int id;
	private String dateTrans;
	private String remarks;
	private int modeType;
	private double amount;
	private int isActive;
	private int eid;
	private String fullName;
	private String issuedcheckby;
	private String timecheck;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<GatePass> list(@Context HttpHeaders headers){
		System.out.println("Loading gatepass...");
		List<GatePass> gates =  GatePass.getCurrentDayGatePass();
		System.out.println("Loaded "+ gates.size() +" gate pass...");
		return gates;
	}
	
	@GET
	@Path("/search/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("date") String date) {
		System.out.println("GET summary " + date);
		String[] split = date.split(":");
		List<GatePass> cash = GatePass.getDate(split[0],split[1]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, GatePass gate) {
		gate.setId(id);
		System.out.println("PUT:" + id);
		System.out.println("id: "+ gate.getId() +" name: " + gate.getRemarks() + " amount:" + gate.getAmount());
		
		GatePass pass = gate;
		String verifier = GatePass.getEid(Integer.valueOf(gate.getIssuedcheckby().translateEscapes()));
		if(!verifier.isEmpty()) {
			pass.setIssuedcheckby(verifier);
			if (GatePass.save(pass).getId()>0) {
				return Response.ok().build();
			} else {
				return Response.notModified().build();
			}
		}else {
			return Response.notModified().build();
		}
		
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("addos")
	public Response add(GatePass gate) throws URISyntaxException {
		System.out.println("POST: " + gate.getId());
		System.out.println("id: "+ gate.getId());
		
		int id = GatePass.save(gate).getId();
		GatePass.updateEmployeeCredit(gate, FormType.EMPLOYEE_OS);
		
		/*List<GatePass> forms = GatePass.retrieve(" AND paz.eid=" + gate.getEid(), new String[0]);
		if (forms != null) {
			return Response.ok(forms, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}*/
		//return Response.ok().build();
		if(id>0) {
			return Response.status(Response.Status.CREATED).build();
		}else {
			return Response.status(Response.Status.NOT_MODIFIED).build();
		}
	}
	
}
