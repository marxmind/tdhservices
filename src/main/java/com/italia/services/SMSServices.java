package com.italia.services;

import java.util.ArrayList;
import java.util.List;
import com.italia.controller.Fields;
import com.italia.controller.SMS;
import com.italia.utils.Currency;
import com.italia.utils.GlobalVar;
import com.italia.utils.SendSMS;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("sms")
public class SMSServices {
	
	@GET
	@Path("/send/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendSMS(@PathParam("param") String param) {
		String[] vals = param.split(":"); 
		String date = vals[0];
		String numbers = vals[1];
		int month = Integer.valueOf(date.split("-")[1]);
		List<Fields> rpts = Fields.getYearSummary(0, 0, date);
		String entrance = "";
		String resto = "";
		String rooms = "";
		String other = "";
		String expense = "";
		String totalincome = "";
		
		for(Fields f : rpts) {
			if("Entrance".equalsIgnoreCase(f.getName())) {
				entrance = value(f, month);
			}
			if("Resto".equalsIgnoreCase(f.getName())) {
				resto = value(f, month);
			}
			if("Rooms/BDO/GCash".equalsIgnoreCase(f.getName())) {
				rooms = value(f, month);
			}
			if("Other Income".equalsIgnoreCase(f.getName())) {
				other = value(f, month);
			}
			if("Expenses".equalsIgnoreCase(f.getName())) {
				expense = value(f, month);
			}
			if("Total".equalsIgnoreCase(f.getName())) {
				totalincome = value(f, month);
			}
		}
		
		//System.out.println("Entrance: " + entrance);
		//System.out.println("Resto: " + resto);
		//System.out.println("Rooms: " + rooms);
		//System.out.println("Other: " + other);
		//System.out.println("Total Income: " + totalincome);
		//System.out.println("Expense: " + expense);
		
		double net = amountVal(totalincome) - amountVal(expense);
		
		System.out.println("Net: " + Currency.formatAmount(net));
		
		String msg = "Report: " + date;
		
		msg += "\nEntrance:" + entrance + "\nResto:" + resto + "\nRooms:" + rooms + "\nOther:" + other + "\nTotal:" + totalincome + "\nExpense:" + expense + "\nNet:" + Currency.formatAmount(net);
		//System.out.println("Number: " + numbers);
		//System.out.println("Message: " + msg);
		SMS sms = new SMS();
		sms.setStatus("ERROR");
		
		if(rpts!=null && rpts.size()>0) {
		String[] data = SendSMS.sendSMS(GlobalVar.PROVIDER_API, numbers, msg);
		
			if("SUCCESS".equalsIgnoreCase(data[0])) {
				sms.setStatus("SEND");
			}else {
				sms.setStatus("ERROR");
			}
		
		}
		
		
		List<SMS> msss = new ArrayList<SMS>();
		msss.add(sms);
		if (rpts!=null && rpts.size()>0) {
			return Response.ok(msss, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.ok(sms, MediaType.APPLICATION_JSON).build();
		}
	}
	
	private double amountVal(String val) {
		return Double.valueOf(val.replace(",", ""));
	}
	
	private String value(Fields f, int month) {
		switch(month) {
		case 1: return f.getF1();
		case 2: return f.getF2();
		case 3: return f.getF3();
		case 4: return f.getF4();
		case 5: return f.getF5();
		case 6: return f.getF6();
		case 7: return f.getF7();
		case 8: return f.getF8();
		case 9: return f.getF9();
		case 10: return f.getF10();
		case 11: return f.getF11();
		case 12: return f.getF12();
		default: return "0";
		}
	} 
	
}
