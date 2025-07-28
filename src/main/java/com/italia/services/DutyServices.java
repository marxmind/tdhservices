package com.italia.services;


import java.util.ArrayList;
import java.util.List;

import com.italia.controller.Employee;
import com.italia.controller.StaffSchedule;
import com.italia.controller.TimeRecord;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("duty")
public class DutyServices {
	
	
	@GET
	@Path("/{date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("date") String date) {
		System.out.println("GET Duty " + date);
		List<TimeRecord> cash = TimeRecord.dutyNow(date);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	

	@GET
	@Path("/sched/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response duty(@PathParam("param") String param) {
		System.out.println("GET Duty " + param);
		int year = Integer.valueOf(param.split(":")[0]);
		int month = Integer.valueOf(param.split(":")[1]);
		List<StaffSchedule> staffs = StaffSchedule.getDuty(year,month);
		
		if(staffs.size()==0) {
			for(Employee e : Employee.getAll()) {
				StaffSchedule staff = StaffSchedule.builder()
						.eid(e.getEid())
						.name(e.getFullname().toUpperCase())
						.year(year)
						.month(month)
						.build();
				staffs.add(staff);
				
				String val = year +"-" + (month<10? "0" + month : month) + "-01";
				int id = StaffSchedule.getLatestId(val, e.getEid());
				StaffSchedule.saveAndUpdate(id, staff, id==0? "new" : "update", val,6, 6+9, e.getEmployeeDepartment(e.getEid()));
				
			}
		}
		
		if (staffs != null) {
			return Response.ok(staffs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/record/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recordtime(@PathParam("param") String param) {
		//eid:year:month:day:time
		System.out.println("GET duty recording: " + param);
		String[] vals = param.split(":");
		int eid = Integer.valueOf(vals[0]);
		int year = Integer.valueOf(vals[1]);
		int month = Integer.valueOf(vals[2]);
		String day = vals[3];
		double time = Double.valueOf(vals[4].replace(",", ""));
		int department = Employee.getEmployeeDepartment(eid);
		StaffSchedule staff = new StaffSchedule();
		staff.setEid(eid);
		staff.setYear(year);
		staff.setMonth(month);
		
		String val = year +"-" + (month<10? "0" + month : month) + "-" + day;
		int id = StaffSchedule.getLatestId(val, eid);
		StaffSchedule.saveAndUpdate(id, staff, id==0? "new" : "update", val,time, time+9, department);
		
		List<StaffSchedule> staffs = StaffSchedule.getDuty(val,year,month, eid);
		if (staffs != null) {
			return Response.ok(staffs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/delete/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recordDelete(@PathParam("param") String param) {
		System.out.println("GET duty recording deletion: " + param);
		String[] vals = param.split(":");
		int eid = Integer.valueOf(vals[0]);
		int year = Integer.valueOf(vals[1]);
		int month = Integer.valueOf(vals[2]);
		String day = vals[3];
		
		String val = year +"-" + (month<10? "0" + month : month) + "-" + day;
		StaffSchedule.delete("UPDATE staffsched SET isactivestaff=0 WHERE eid=" + eid + " AND dayin='"+ val +"'", new String[0]);
		
		List<StaffSchedule> staffs = StaffSchedule.getDuty(val,year,month, eid);
		if (staffs != null) {
			return Response.ok(staffs, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
}
