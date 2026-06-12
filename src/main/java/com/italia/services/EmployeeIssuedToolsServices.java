package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.EmployeeIssuedTools;
import com.italia.controller.PulseReport;

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

@Path("tools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeIssuedToolsServices {

	@GET
	public List<EmployeeIssuedTools> list(@Context HttpHeaders headers){
		System.out.println("Loading EmployeeIssuedToolsServices...");
		List<EmployeeIssuedTools> recs = EmployeeIssuedTools.retrive(" ORDER BY etid DESC LIMIT 100", new String[0]);
		System.out.println("Loaded "+ recs.size() +" EmployeeIssuedToolsServices...");
		return recs;
	}
	
	@GET
	@Path("/search-name/{name}")
	public Response searchParam(@PathParam("name") String name) {
		System.out.println("search param:" + name);
		List<EmployeeIssuedTools> recs =  EmployeeIssuedTools.retrive(" AND name like '%"+ name +"%' ORDER BY name", new String[0]);
		return Response.ok(recs, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/date-range/{vals}")
	public Response dateRangeParam(@PathParam("vals") String vals) {
		System.out.println("search param:" + vals);
		String[] params = vals.split(":");
		List<EmployeeIssuedTools> recs =  EmployeeIssuedTools.retrive(" AND (dateissued>='"+ params[0] +"%' AND dateissued<='"+ params[1] +"') ORDER BY etid DESC", new String[0]);
		return Response.ok(recs, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/employee/{eid}")
	public Response searchEmployeeTools(@PathParam("eid") long eid) {
		System.out.println("searchEmployeeTools param:" + eid);
		List<EmployeeIssuedTools> recs =  EmployeeIssuedTools.retrive(" AND eid=" + eid + " ORDER BY etid DESC", new String[0]);
		return Response.ok(recs, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/employeeid/{id}")
	public Response searchEmployeeToolsById(@PathParam("id") int id) {
		System.out.println("searchEmployeeTools id:" + id);
		List<EmployeeIssuedTools> recs =  EmployeeIssuedTools.retrive(" AND etid=" + id, new String[0]);
		if(recs!=null && recs.size()>0) {
			return Response.ok(recs.get(0), MediaType.APPLICATION_JSON).build();
		}else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Path("saving")
	public Response add(EmployeeIssuedTools rec) throws URISyntaxException {
		System.out.println("POST");
		EmployeeIssuedTools newTools =  EmployeeIssuedTools.save(rec);
		return Response.ok(newTools, MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("bulk")
	public Response bulk(List<EmployeeIssuedTools> recs) throws URISyntaxException {
		System.out.println("POST");
		
		for(EmployeeIssuedTools b : recs) {
			EmployeeIssuedTools.save(b);
		}
		
		List<EmployeeIssuedTools> rcs = EmployeeIssuedTools.retrive(" ORDER BY etid DESC LIMIT 100", new String[0]);
		return Response.ok(recs, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/delete/{param}")
	public Response delete(@PathParam("param") String param) {
		String[] vals = param.split(":");
		long id = Long.valueOf(vals[0]);
		long eid = Long.valueOf(vals[1]);
		PulseReport.delete("UPDATE employeeissuedtools SET isActive=0 WHERE etid=" + id + " AND eid=" + eid, new String[0]);
			List<PulseReport> rsvs = PulseReport.retrive(" AND eid="+ eid +" ORDER BY etid DESC", new String[0]);
			if (rsvs != null) {
				return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		
	}
	
}
