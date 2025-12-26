package com.italia.services;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.Payables;
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

@Path("payables")
public class PayableServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Payables> list(@Context HttpHeaders headers){
		System.out.println("Loading Payables...");
		List<Payables> recs = Payables.retrive(" ORDER BY pyid DESC", new String[0]);
		System.out.println("Loaded "+ recs.size() +" payables...");
		return recs;
	}
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Payables> searchParam(@PathParam("param") String param) {
		System.out.println("search param:" + param);
		List<Payables> recs =  Payables.retrive(" AND payee like '%"+ param +"%'", new String[0]);
		System.out.println("done Loaded "+ recs.size() +" payables...");
		return recs;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(Payables rec) throws URISyntaxException {
		System.out.println("POST");
		long id =  Payables.save(rec).getId();
		URI uri = new URI("/add/" + id);
		return Response.created(uri).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sms")
	public Response sms(Payables rec) throws URISyntaxException {
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
	public Response update(@PathParam("id") long id, Payables rec) {
		rec.setId(id);
		System.out.println("PUT:" + id);
		if (Payables.save(rec).getId()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		Payables.delete("UPDATE payables SET isactivepy=0 WHERE pyid=" + id, new String[0]);
			List<Payables> rsvs = Payables.retrive(" ORDER BY pyid DESC", new String[0]);
			if (rsvs != null) {
				return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		
	}
	
}
