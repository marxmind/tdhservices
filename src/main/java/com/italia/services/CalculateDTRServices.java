package com.italia.services;

import com.italia.controller.Dtr;
import com.italia.controller.TimeRecord;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
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
	}
	

	
}
