package com.italia.services;

import org.glassfish.jersey.server.ResourceConfig;

import com.italia.controller.Breakfast;

public class LSServices extends ResourceConfig {
	
	public LSServices() {
		System.out.println("loading.......services");
		packages(StockRoomServices.class.getPackage().getName());
		packages(RestoServices.class.getPackage().getName());
		packages(KitchenServices.class.getPackage().getName());
		packages(HouseServices.class.getPackage().getName());
		packages(UserServices.class.getPackage().getName());
		packages(RequestServices.class.getPackage().getName());
		packages(EmployeeServices.class.getPackage().getName());
		packages(CashProcessServices.class.getPackage().getName());
		packages(CashTypesServices.class.getPackage().getName());
		packages(TimeRecordServices.class.getPackage().getName());
		packages(CalculateDTRServices.class.getPackage().getName());
		packages(GatePassServices.class.getPackage().getName());
		packages(FieldsServices.class.getPackage().getName());
		packages(FormProcessServices.class.getPackage().getName());
		packages(DutyServices.class.getPackage().getName());
		packages(SMSServices.class.getPackage().getName());
		packages(LeaveServices.class.getPackage().getName());
		packages(ReservationServices.class.getPackage().getName());
		packages(ReportStaffPenaltyServices.class.getPackage().getName());
		packages(FoodServices.class.getPackage().getName());
		packages(FoodOrderServices.class.getPackage().getName());
		packages(FoodItemServices.class.getPackage().getName());
		packages(KitchenOrderServices.class.getPackage().getName());
		packages(EmployeeSavingServices.class.getPackage().getName());
		packages(PropertyServices.class.getPackage().getName());
		packages(PropertyTranServices.class.getPackage().getName());
		packages(SupplierServices.class.getPackage().getName());
		packages(SupplierPayablesServices.class.getPackage().getName());
		packages(BreakfastServices.class.getPackage().getName());
		packages(BreakfastItemServices.class.getPackage().getName());
		packages(HorsebackServices.class.getPackage().getName());
		packages(UpcomingEventServices.class.getPackage().getName());
		System.out.println("end loading.......services");
	}
	
}
