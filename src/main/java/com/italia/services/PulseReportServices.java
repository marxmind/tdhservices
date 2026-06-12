package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.PulseReport;
import com.italia.controller.Receivables;

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

@Path("pulsereport")
public class PulseReportServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PulseReport> list(@Context HttpHeaders headers){
		System.out.println("Loading PulseReport...");
		List<PulseReport> recs = PulseReport.retrive(" ORDER BY pid DESC LIMIT 100", new String[0]);
		System.out.println("Loaded "+ recs.size() +" PulseReport...");
		return recs;
	}
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PulseReport> searchParam(@PathParam("param") String param) {
		System.out.println("search param:" + param);
		List<PulseReport> recs =  PulseReport.retrive(" AND (description like '%"+ param +"%' OR fixedby like '%"+ param +"%')", new String[0]);
		System.out.println("done Loaded "+ recs.size() +" PulseReport...");
		return recs;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(PulseReport rec) throws URISyntaxException {
		System.out.println("POST");
		long id =  PulseReport.save(rec).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		PulseReport.delete("UPDATE pulsereport SET isactivepu=0 WHERE pid=" + id, new String[0]);
			List<PulseReport> rsvs = PulseReport.retrive(" ORDER BY pid DESC", new String[0]);
			if (rsvs != null) {
				return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		
	}
	
}
