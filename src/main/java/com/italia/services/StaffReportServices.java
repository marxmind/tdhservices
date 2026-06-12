package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.italia.controller.Department;
import com.italia.controller.Food;
import com.italia.controller.FoodItem;
import com.italia.controller.FoodOrder;
import com.italia.controller.KitchenOrder;
import com.italia.controller.StaffReport;
import com.italia.controller.StaffReportTrans;
import com.italia.enm.OrderStatus;
import com.italia.utils.DateUtils;

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

@Path("staff-reports")
public class StaffReportServices {
	
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<StaffReport> list(@Context HttpHeaders headers){
		System.out.println("Loading StaffReport...");
		List<StaffReport> items =  StaffReport.retrieve (" ORDER BY sr.datetrans DESC", new String[0]);
		System.out.println("done Loaded "+ items.size() +" StaffReport...");
		return items;
	}
	
	/*@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(StaffReport staff) throws URISyntaxException {
		System.out.println("POST==================== " + staff.getEid());
		
		
		Map<String, Department> depMap = Department.loadAllDepartmentByName();
		if(depMap!=null && depMap.containsKey(staff.getDepartmentName())) {
			staff.setDepartmentId(depMap.get(staff.getDepartmentName()).getId());
		}
		
		StaffReport st = StaffReport.save(staff);
		String timeNow = DateUtils.getCurrentDateYYYYMMDD() + " " + DateUtils.getCurrentTIME() + ":00";
		StaffReportTrans.builder()
				.id(0)
				.timeTrans(timeNow)
				.eid(staff.getEid())
				.descriptions("Staff Report")
				.sid(st.getId())
				.isActive(1)
				.build().save();
		
		List<StaffReport> cash = StaffReport.retrieve(" AND sr.sid=" + st.getId(), new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}*/
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public StaffReport add(StaffReport staff) throws URISyntaxException {
		System.out.println("POST==================== " + staff.getEid());
		
		
		Map<String, Department> depMap = Department.loadAllDepartmentByName();
		if(depMap!=null && depMap.containsKey(staff.getDepartmentName())) {
			staff.setDepartmentId(depMap.get(staff.getDepartmentName()).getId());
		}
		
		StaffReport st = StaffReport.save(staff);
		String timeNow = DateUtils.getCurrentDateYYYYMMDD() + " " + DateUtils.getCurrentTIME() + ":00";
		StaffReportTrans.builder()
				.id(0)
				.timeTrans(timeNow)
				.eid(staff.getEid())
				.descriptions("Start of my recording ")
				.sid(st.getId())
				.isActive(1)
				.build().save();
		
		StaffReport cash = StaffReport.retrieve(" AND sr.sid=" + st.getId(), new String[0]).get(0);
		return cash;
		/*if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}*/
	}
	
	@GET 
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchStaff(@PathParam("id") int  id) {
		System.out.println("GET staff id " + id);
		List<StaffReport> cash = StaffReport.retrieve(" AND sr.sid=" + id, new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/search/{val}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchDate(@PathParam("val") String  param) {
		System.out.println("GET staff date " + param);
		String dateFrom = "";
		String dateTo = "";
		String sql = "";
		String all="*";
		if(param.contains(":")) {
			String[] vals = param.split(":");
			dateFrom = vals[0];
			dateTo = vals[1];
			all=vals[2];
		}else {
			dateFrom = param;
			dateTo = param;
		}
		
		if("*".equalsIgnoreCase(all)) {
			sql = " AND (sr.datetrans>='"+ dateFrom +"' AND sr.datetrans<='"+ dateTo +"')";
		}else {
			sql = " AND (sr.datetrans>='"+ dateFrom +"' AND sr.datetrans<='"+ dateTo +"') AND sr.eid=" + all;
		}
		
		List<StaffReport> cash = StaffReport.retrieve(sql, new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
