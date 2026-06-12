package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Employee;
import com.italia.controller.EmployeeSaving;
import com.italia.controller.Fields;
import com.italia.controller.RequestSaving;
import com.italia.enm.RequestSavingStatus;
import com.italia.utils.App;
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

@Path("request-saving")
public class RequestSavingServices {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<RequestSaving> list(@Context HttpHeaders headers){
		System.out.println("Loading request saving...");
		List<RequestSaving> recs = RequestSaving.retrive(" ORDER BY rsid DESC", new String[0]);
		System.out.println("Loaded "+ recs.size() +" requests...");
		return recs;
	}
	
	@GET
	@Path("/request/employee/{eid}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RequestSaving> employeesaving(@PathParam("eid") long eid) {
		System.out.println("search eid:" + eid);
		List<RequestSaving> recs =  RequestSaving.retrive(" AND eid="+ eid + " ORDER BY rsid DESC", new String[0]);
		System.out.println("done Loaded "+ recs.size() +" request saving...");
		return recs;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saving")
	public Response add(RequestSaving rec) throws URISyntaxException {
		System.out.println("POST");
		RequestSaving rq =  RequestSaving.save(rec);
		
		if(RequestSavingStatus.REQUEST.getId()==rq.getStatus()) {//request for approval
			calculateSavingAndSendToApprover(rq);
		}
		
		if(rq.getStatus()>0) {//inform employee
			sendEmployeeNotification(rec);
		}
		
		
		URI uri = new URI("/add/" + rq.getId());
		return Response.created(uri).build();
	}
	
	private void sendEmployeeNotification(RequestSaving rec) {
		Employee em = Employee.getEmployeeById(rec.getEid());
		String contact = em.getContactNo().replace("-", "");
		contact = contact.replace("+63", "0");
		if(em.getEid()>0) {
			
			String msg = RequestSavingStatus.APPROVED.getName();
			if(RequestSavingStatus.APPROVED.getId()==rec.getStatus()) {
				msg = "Hi " + em.getFullname() + ", your request for saving withdrawal has been "+ RequestSavingStatus.containId(rec.getStatus()).getName() +", Amount applied: " + Currency.formatAmount(rec.getAmountReq()) + ". Amount approved: "+ Currency.formatAmount(rec.getAmountApp());
			}else if(RequestSavingStatus.DENIED.getId()==rec.getStatus() || RequestSavingStatus.CANCELLED.getId()==rec.getStatus()) {
				msg = "Hi " + em.getFullname() + ", your request for saving withdrawal has been "+ RequestSavingStatus.containId(rec.getStatus()).getName() +", Amount applied: " + Currency.formatAmount(rec.getAmountReq());
			}
			
			String[] data = SendSMS.sendSMS(GlobalVar.PROVIDER_API, contact, msg);
		}
		
	}
	
	
	private boolean calculateSavingAndSendToApprover(RequestSaving rec) {
		List<EmployeeSaving> saving = EmployeeSaving.retrieveEmployeeSavingType(rec.getEid(), 4);//both direct and deducted from salary
		double totalSaving = 0d;
		for(EmployeeSaving e : saving) {
			totalSaving += e.getAmount();
		}
		
		String total = Currency.formatAmount(totalSaving);
		if(totalSaving>0) {
			String employee = Employee.getEmployeeName(Integer.valueOf(rec.getEid()+""));
			App app = App.getInstance();
			
			String[] data = SendSMS.sendSMS(GlobalVar.PROVIDER_API, app.getContact1(), "Hi, " + app.getContactName1() + " Request for saving withdrawal. Reason: "+ rec.getReason() +", Amount applied: " + Currency.formatAmount(rec.getAmountReq()) + ". Thank you. From " + employee);
			String[] data2 = SendSMS.sendSMS(GlobalVar.PROVIDER_API, app.getContact2(), "Hi, " + app.getContactName2() + " Request for saving withdrawal. Reason: "+ rec.getReason() +", Amount applied: " + Currency.formatAmount(rec.getAmountReq()) + ". Thank you. From " + employee);
			String[] data3 = SendSMS.sendSMS(GlobalVar.PROVIDER_API, app.getContact3(), "Hi, " + app.getContactName3() + " Request for saving withdrawal. Reason: "+ rec.getReason() +", Amount applied: " + Currency.formatAmount(rec.getAmountReq()) + ". Available saving is "+ Currency.formatAmount(total)  +". Thank you. From " + employee);
			
			if("SUCCESS".equalsIgnoreCase(data3[0])) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update/{id}")
	public Response update(@PathParam("id") long id, RequestSaving rec) {
		rec.setId(id);
		System.out.println("PUT:" + id);
		if (RequestSaving.save(rec).getId()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		RequestSaving.delete("UPDATE requestsaving SET isactiverq=0 WHERE rsid=" + id, new String[0]);
			List<RequestSaving> rsvs = RequestSaving.retrive(" ORDER BY rsid DESC", new String[0]);
			if (rsvs != null) {
				return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		
	}
	
}
