package com.italia.services;

import java.util.List;

import com.italia.controller.CashProcess;
import com.italia.controller.Fields;
import com.italia.controller.GatePass;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
	
}
