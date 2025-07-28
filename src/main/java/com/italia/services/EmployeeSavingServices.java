package com.italia.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.italia.controller.EmployeePayable;
import com.italia.controller.EmployeeSaving;
import com.italia.controller.Fields;
import com.italia.controller.FormProcess;
import com.italia.utils.Currency;
import com.italia.utils.DateUtils;
import com.italia.utils.GlobalVar;
import com.italia.utils.SendSMS;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("employeesaving")
public class EmployeeSavingServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Fields> list(@Context HttpHeaders headers){
		System.out.println("Loading 13 month employee...");
		List<Fields> flds = new ArrayList<Fields>();
		
		 Map<String, Map<String, Double>> data =  EmployeeSaving.getAllEmployee13thMonthIncentives(DateUtils.getCurrentYear());
		 
		 for(String name : data.keySet()) {
			 Fields f = new Fields();
			 f.setName(name);
			 for(String month : data.get(name).keySet()) {
				 switch(month) {
				 	case "January" : f.setF1(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "February" : f.setF2(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "March" : f.setF3(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "April" : f.setF4(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "May" : f.setF5(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "June" : f.setF6(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "July" : f.setF7(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "August" : f.setF8(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "September" : f.setF9(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "October" : f.setF10(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "November" : f.setF11(Currency.formatAmount(data.get(name).get(month))); break;
				 	case "December" : f.setF12(Currency.formatAmount(data.get(name).get(month))); break;
				 }
			 }
			 flds.add(f);
		 }
		System.out.println("done Loaded "+ flds.size() +" employee 13th month...");
		return flds;
	}
	
	public void addDeductionAsCA(double amount, int eid) {
		FormProcess form = FormProcess.builder()
				.id(0)
				.amount(amount)
				.dateTrans(DateUtils.getCurrentDateYYYYMMDD())
				.eid(eid)
				.purpose("Saving Inquiry Deduction")
				.statusName("Admin Approved")
				.build();
				
		FormProcess update = FormProcess.save(form);
		update.setStatusName("Admin Approved");
		update.save();
		
				EmployeePayable.updateEmployeePayable(eid, amount, "ADD");
	}
	
	@GET
	@Path("/savingtype/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchSavingType(@PathParam("param") String param) {
		List<Fields> flds = new ArrayList<Fields>();
		System.out.println("GET Saving  " + param);
		long eid = Long.valueOf(param.split(":")[0]);
		int type = Integer.valueOf(param.split(":")[1]);
		
		//4 both duduction from salary and direct saving
		//5 all types
		
		List<EmployeeSaving> saving = EmployeeSaving.retrieveEmployeeSavingType(eid, type);
		double totalSaving = 0d;
		for(EmployeeSaving e : saving) {
			totalSaving += e.getAmount();
			flds.add(
					Fields.builder().name(e.getEmployee().getFullname()).f1(e.getDateTrans()).f2(e.getSaveTypeName()).f3(Currency.formatAmount(e.getAmount())) .build()
					);
			
		}
		
		if(totalSaving>0) {
			
			EmployeeSaving siv = saving.get(0);
			String[] data = SendSMS.sendSMS(GlobalVar.PROVIDER_API, siv.getRemarks(), "Hi, " + siv.getEmployee().getFullname() + " Current saving Php" + Currency.formatAmount(totalSaving) + ". This transaction has a service fee of Php3.00.");
			if("SUCCESS".equalsIgnoreCase(data[0])) {
				flds.add(
						Fields.builder().name("").f1("SMS SENT TO YOUR MOBILE NUMBER: " + siv.getRemarks()).f2("").f3("") .build()
						);
				flds.add(
						Fields.builder().name("").f1("This transaction will cost Php3.00  fee that will add to your cash advance.").f2("").f3("") .build()
						);
				//add deduction
				addDeductionAsCA(3.0, Integer.valueOf(eid+""));
				
			}else {
				flds.add(
						Fields.builder().name("").f1("SMS NOT SENT TO YOUR MOBILE NUMBER: " + siv.getRemarks()).f2("").f3("") .build()
						);
			}
		}
		
		
		if (flds != null) {
			return Response.ok(flds, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

}
