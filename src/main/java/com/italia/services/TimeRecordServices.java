package com.italia.services;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.italia.controller.Employee;
import com.italia.controller.Food;
import com.italia.controller.TimeRecord;
import com.italia.utils.DateUtils;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
@Path("timerecord")
public class TimeRecordServices {
	
	private long id;
	private String dateTrans;
	private String time1;
	private String time2;
	private String time3;
	private String time4;
	private String time5;
	private String time6;
	private long eid;
	private String fullname;
	private int iscompleted;
	private int hasupdate;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimeRecord> list(@Context HttpHeaders headers){
		System.out.println("Loading time...");
		List<TimeRecord> records =  TimeRecord.getUnprocessedDTR();
		System.out.println("Loaded "+ records.size() +" records...");
		return records;
	}
	
	@GET
	@Path("/unprocessed/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadlatest(@PathParam("date") String date) {
		System.out.println("GET Path id");
		List<TimeRecord> times = TimeRecord.getByDate(date);
		if (times != null) {
			return Response.ok(times, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/time/{eid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employee(@PathParam("eid") int eid) {
		System.out.println("GET Path id");
		List<TimeRecord> times = TimeRecord.getUnprocessedDTREmployee(eid);
		if (times != null) {
			return Response.ok(times, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/record/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recordtime(@PathParam("val") String val) {
		System.out.println("GET time recording: " + eid);
		//String employeeName = Employee.getEmployeeName(eid);
		//Employee employee = Employee.getEmployee(eid);
		Employee employee = Employee.getEmployeePasscode(val);
		if(employee!=null) {
			String currentTime = new SimpleDateFormat("HH:mm").format(new Date()); 
			//record
			TimeRecord time = TimeRecord.recordTime(employee.getEid(), DateUtils.getCurrentDateYYYYMMDD(), currentTime);
			//get latest
			time.setTime1(currentTime);
			time.setTime2(employee.getFullname());
			time.setImgurl(employee.getImgurl());
			
			List<TimeRecord> times = new ArrayList<TimeRecord>();
			times.add(time);
			return Response.ok(times, MediaType.APPLICATION_JSON).build();
		}else {
			//return Response.status(Response.Status.NOT_FOUND).build();
			return Response.ok(TimeRecord.builder().fullname("Employee Not Found").time1("").eid(eid).build(), MediaType.APPLICATION_JSON).build();
		}
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET Path id");
		List<TimeRecord> times = TimeRecord.getUnprocessedDTR();
		if (times != null) {
			return Response.ok(times, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(TimeRecord time) throws URISyntaxException {
		System.out.println("POST");
		
		TimeRecord  tmp = time;
		tmp.setTime1(TimeRecord.replaceAsteriskToColon(time.getTime1()));
		tmp.setTime2(TimeRecord.replaceAsteriskToColon(time.getTime2()));
		tmp.setTime3(TimeRecord.replaceAsteriskToColon(time.getTime3()));
		tmp.setTime4(TimeRecord.replaceAsteriskToColon(time.getTime4()));
		tmp.setTime5(TimeRecord.replaceAsteriskToColon(time.getTime5()));
		tmp.setTime6(TimeRecord.replaceAsteriskToColon(time.getTime6()));
		
		System.out.println("time1: "+tmp.getTime1());
		System.out.println("time2: "+tmp.getTime2());
		System.out.println("time3: "+tmp.getTime3());
		System.out.println("time4: "+tmp.getTime4());
		System.out.println("time5: "+tmp.getTime5());
		System.out.println("time6: "+tmp.getTime6());
		
		long recId =  TimeRecord.checkAddedTime(tmp).getId();
		URI uri = new URI("/add/" + recId);
	
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, TimeRecord time) {
		time.setId(id);
		
		TimeRecord  tmp = time;
		tmp.setTime1(TimeRecord.replaceAsteriskToColon(time.getTime1()));
		tmp.setTime2(TimeRecord.replaceAsteriskToColon(time.getTime2()));
		tmp.setTime3(TimeRecord.replaceAsteriskToColon(time.getTime3()));
		tmp.setTime4(TimeRecord.replaceAsteriskToColon(time.getTime4()));
		tmp.setTime5(TimeRecord.replaceAsteriskToColon(time.getTime5()));
		tmp.setTime6(TimeRecord.replaceAsteriskToColon(time.getTime6()));
		
		System.out.println("Updating:" + id);
		System.out.println("id: "+ tmp.getId() +" name: " + tmp.getFullname());
		if (TimeRecord.save(tmp).getId()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete/{id}")
	public Response delete(@PathParam("id") int id) {
		System.out.println("DELETE");
		System.out.println("TimeRecord Id:" + id);
		if (TimeRecord.delete(id)) {
			System.out.println("Successfully deleted.. " + id);
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
	
}
