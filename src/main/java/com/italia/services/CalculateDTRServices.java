package com.italia.services;

import java.util.List;
import com.italia.controller.Dtr;
import com.italia.controller.TimeRecord;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Path("calculatedtr")
public class CalculateDTRServices {
	
	private int eid;
	
	
	@GET
	@Path("{eid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("eid") int eid) {
		System.out.println("GET employee id " + eid);


		if (eid>0) {
			
			for(TimeRecord record : TimeRecord.getUnprocessedDTREmployee(eid)) {
				String dateTrans = record.getDateTrans();
				if(!record.getTime1().isEmpty() && !record.getTime2().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime1())
							.dtrOut(record.getTime2())
							.type(0)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 1 group...");
				}
				if(!record.getTime3().isEmpty() && !record.getTime4().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime3())
							.dtrOut(record.getTime4())
							.type(1)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 2 group...");
				}
				
				if(!record.getTime5().isEmpty() && !record.getTime6().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime5())
							.dtrOut(record.getTime6())
							.type(2)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 3 group...");
				}
				
				if(!record.getOtStart().isEmpty() && !record.getOtEnd().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getOtStart())
							.dtrOut(record.getOtEnd())
							.type(3)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving overtime time group...");
				}
				
				record.setIscompleted(1);
				TimeRecord.save(record);
			}
		
			List<TimeRecord> records =  TimeRecord.getUnprocessedDTR();
			
			if (records != null) {
				return Response.ok(records, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		
		}else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
	}
	
	/*
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{eid}")
	public Response update(@PathParam("eid") int eid, TimeRecord time) {
		
		if (eid>0) {
			
			for(TimeRecord record : TimeRecord.getUnprocessedDTREmployee(eid)) {
				String dateTrans = record.getDateTrans();
				if(!record.getTime1().isEmpty() && !record.getTime2().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime1())
							.dtrOut(record.getTime2())
							.type(0)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 1 group...");
				}
				if(!record.getTime3().isEmpty() && !record.getTime4().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime3())
							.dtrOut(record.getTime4())
							.type(1)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 2 group...");
				}
				
				if(!record.getTime5().isEmpty() && !record.getTime6().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime5())
							.dtrOut(record.getTime6())
							.type(2)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 3 group...");
				}
				
				record.setIscompleted(1);
				TimeRecord.save(record);
			}
			
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	*/
	
	/*
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/perday/{param}")
	public Response updatePerDay(@PathParam("param") String param, TimeRecord time) {
		System.out.println("calling per day generation " + param);
		int emeid=0;
		String date="";
		String[] val = param.split(":");
		emeid = Integer.valueOf(val[0]);
		date = val[1];
		if (emeid>0) {
			
			for(TimeRecord record : TimeRecord.getUnprocessedDTREmployeePerDay(emeid, date)) {
				String dateTrans = record.getDateTrans();
				if(!record.getTime1().isEmpty() && !record.getTime2().isEmpty()) {
					
				//String startTime = TimeRecord.checkAndChangeStartDutyIfPossible(emeid, date, record.getTime1());
					
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime1())
							.dtrOut(record.getTime2())
							.type(0)
							.isCompleted(0)
							.employee(emeid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 1 group...");
				}
				if(!record.getTime3().isEmpty() && !record.getTime4().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime3())
							.dtrOut(record.getTime4())
							.type(1)
							.isCompleted(0)
							.employee(emeid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 2 group...");
				}
				
				if(!record.getTime5().isEmpty() && !record.getTime6().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime5())
							.dtrOut(record.getTime6())
							.type(2)
							.isCompleted(0)
							.employee(emeid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 3 group...");
				}
				
				record.setIscompleted(1);
				TimeRecord.save(record);
			}
			
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}*/
	
	@GET
	@Path("/perday/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePerDay(@PathParam("param") String param) {
		System.out.println("calling per day generation " + param);
		int emeid=0;
		String date="";
		String[] val = param.split(":");
		emeid = Integer.valueOf(val[0]);
		date = val[1];
		if (emeid>0) {
			
			for(TimeRecord record : TimeRecord.getUnprocessedDTREmployeePerDay(emeid, date)) {
				String dateTrans = record.getDateTrans();
				if(!record.getTime1().isEmpty() && !record.getTime2().isEmpty()) {
					
				//String startTime = TimeRecord.checkAndChangeStartDutyIfPossible(emeid, date, record.getTime1());
					
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime1())
							.dtrOut(record.getTime2())
							.type(0)
							.isCompleted(0)
							.employee(emeid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 1 group...");
				}
				if(!record.getTime3().isEmpty() && !record.getTime4().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime3())
							.dtrOut(record.getTime4())
							.type(1)
							.isCompleted(0)
							.employee(emeid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 2 group...");
				}
				
				if(!record.getTime5().isEmpty() && !record.getTime6().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getTime5())
							.dtrOut(record.getTime6())
							.type(2)
							.isCompleted(0)
							.employee(emeid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving time 3 group...");
				}
				
				if(!record.getOtStart().isEmpty() && !record.getOtEnd().isEmpty()) {
					Dtr dtr = Dtr.builder()
							.dateTrans(dateTrans)
							.dtrIn(record.getOtStart())
							.dtrOut(record.getOtEnd())
							.type(3)
							.isCompleted(0)
							.employee(eid)
							.isActive(1)
							.remarks("")
							.build();
					Dtr.save(dtr);
					System.out.println("saving overtime time group...");
				}
				
				record.setIscompleted(1);
				TimeRecord.save(record);
			}
			
			List<TimeRecord> records =  TimeRecord.getUnprocessedDTR();
			
			if (records != null) {
				return Response.ok(records, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		} else {
			return Response.notModified().build();
		}
	}

	
}
