package com.italia.services;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.Receivables;
import com.italia.utils.Currency;
import com.italia.utils.GlobalVar;
import com.italia.utils.SendSMS;

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

@Path("receivables")
public class ReceivableServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Receivables> list(@Context HttpHeaders headers){
		System.out.println("Loading receivables...");
		List<Receivables> recs = Receivables.retrive(" ORDER BY rcid DESC", new String[0]);
		System.out.println("Loaded "+ recs.size() +" receivables...");
		return recs;
	}
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Receivables> searchParam(@PathParam("param") String param) {
		System.out.println("search param:" + param);
		List<Receivables> recs =  Receivables.retrive(" AND customername like '%"+ param +"%'", new String[0]);
		System.out.println("done Loaded "+ recs.size() +" receivables...");
		return recs;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(Receivables rec) throws URISyntaxException {
		System.out.println("POST");
		long id =  Receivables.save(rec).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sms")
	public Response sms(Receivables rec) throws URISyntaxException {
		System.out.println("sms sending to client");
		
		String[] data = SendSMS.sendSMS(GlobalVar.PROVIDER_API, rec.getContactNo(), rec.getDescription());
		if("SUCCESS".equalsIgnoreCase(data[0])) {
			return Response.status(Response.Status.OK).build();//200
		}else {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		//return Response.status(Response.Status.ACCEPTED).build();//202
		
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") long id, Receivables rec) {
		rec.setId(id);
		System.out.println("PUT:" + id);
		if (Receivables.save(rec).getId()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
			Receivables.delete("UPDATE receivables SET isactiverc=0 WHERE rcid=" + id, new String[0]);
			List<Receivables> rsvs = Receivables.retrive(" ORDER BY rcid DESC", new String[0]);
			if (rsvs != null) {
				return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		
	}
	
}
