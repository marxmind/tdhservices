package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.CashType;
import com.italia.utils.Currency;
import com.italia.utils.DateUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Fields {

	private String name;
	private String f1;
	private String f2;
	private String f3;
	private String f4;
	private String f5;
	private String f6;
	private String f7;
	private String f8;
	private String f9;
	private String f10;
	private String f11;
	private String f12;
	private static double[] months = new double[12];
	
	public static List<Fields> getYearSummary(int year){
		List<Fields> sums = new ArrayList<Fields>();
		months = new double[12];
		
		
		Map<Integer, Double> mapEntranceData = new LinkedHashMap<Integer, Double>();
		
		String sql = "SELECT month(dateTrans) as month, sum(totalamount) as total FROM entrance WHERE isactiveent=1 AND year(dateTrans)=" + year + " GROUP BY month(dateTrans) ORDER BY month(dateTrans)";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		System.out.println("Entrance " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			mapEntranceData.put(rs.getInt("month"), rs.getDouble("total"));
			//System.out.println(rs.getInt("month") + " amount: " + rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//CASH FROM OTHER INCOME
		Map<Integer, Double> mapCashOtherData = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.CASH_OTHER_INCOME.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		ps = conn.prepareStatement(sql);
		System.out.println("Cash From Other Income " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			mapCashOtherData.put(rs.getInt("month"), rs.getDouble("total"));
			//System.out.println(rs.getInt("month") + " amount: " + rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//RESTO ORDER PAID
		Map<Integer, Double> mapRestoData = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.RESTO_ORDER.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
				
		ps = conn.prepareStatement(sql);
		System.out.println("Resto paid " + ps.toString());
		rs = ps.executeQuery();
				
		while(rs.next()){
			mapRestoData.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//RESTO ORDER PAID
		Map<Integer, Double> mapBankPaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.BDO.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
						
		ps = conn.prepareStatement(sql);
		System.out.println("Bank paid " + ps.toString());
		rs = ps.executeQuery();
						
		while(rs.next()){
			mapBankPaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//ACCOMMODATION PAID
		Map<Integer, Double> mapAccommodationaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.ACCOMMODATION_PAID.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
								
		ps = conn.prepareStatement(sql);
		System.out.println("accommodation paid " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapAccommodationaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//GCash ORDER PAID
		Map<Integer, Double> mapGCashPaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.GCASH.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
								
		ps = conn.prepareStatement(sql);
		System.out.println("GCash paid " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapGCashPaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//Combined ORDER PAID
		Map<Integer, Double> mapCombinedRoomhPaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
				+ "(cashtype="+ CashType.GCASH.getId() +" OR cashtype=" + CashType.ACCOMMODATION_PAID.getId() + " OR cashtype=" + CashType.BDO.getId() + ") "
						+ "and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
								
		ps = conn.prepareStatement(sql);
		System.out.println("GCash paid " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapCombinedRoomhPaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//Expense
		Map<Integer, Double> mapExpense = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
				+ "(cashtype="+ CashType.CASH_EXPENSES.getId() +" OR cashtype=" + CashType.ALL_EXPENSE.getId() + " OR cashtype=" + CashType.CASH_ADVANCE_EMPLOYEE.getId() + " OR cashtype="+ CashType.EMPLOYEE_SALARY.getId() 
				+ " OR cashtype=" + CashType.AMOUNT_PAID_SUPPLIER.getId() + " OR cashtype=" + CashType.FARE.getId() + " OR cashtype=" + CashType.MERRIENDA.getId() + " OR cashtype=" + CashType.CASH_GROCERRY_EXPENSE.getId()
				+ " OR cashtype=" + CashType.GASOLINE.getId() + " OR cashtype=" + CashType.SOLANE.getId() + " OR cashtype=" + CashType.MATERIAL_EXPENSE.getId() + " OR cashtype=" + CashType.RENOVATION_EXPENSE.getId() 
				+ " OR cashtype=" + CashType.PAYMENT_TO_TALENT.getId() + " OR cashtype=" + CashType.COMPANY_EXPENSE_ACTIVITY.getId() + " OR cashtype=" + CashType.NATIVE_CHICKEN_EXPENSES.getId() + " OR cashtype=" + CashType.TILAPIA_EXPENSES.getId()
				+ " OR cashtype=" + CashType.PAYMENT_TO_BUYNSELL.getId() + " OR cashtype=" + CashType.MINERAL_WATER_GALLON_ORDER.getId() + " OR cashtype=" + CashType.SACK_RICE_EXPENSE.getId() + " OR cashtype=" + CashType.VILLA_SHARE_INCOME.getId()
				+ " OR cashtype=" + CashType.SOUVENIR_EXPENSE.getId() + " OR cashtype=" + CashType.SOUVENIR_MINUS_CAPITAL.getId()
				+") "
				+ "and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
								
		ps = conn.prepareStatement(sql);
		System.out.println("Expense " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapExpense.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		
		Fields fld = new Fields();
		fld.setName("Entrance");
		for(int month=1; month<=12; month++) {
			
			if(mapEntranceData!=null && mapEntranceData.containsKey(month)) {
				fld = assignedMonth(month, mapEntranceData.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Resto");
		for(int month=1; month<=12; month++) {
			
			if(mapRestoData!=null && mapRestoData.containsKey(month)) {
				fld = assignedMonth(month, mapRestoData.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Rooms");
		for(int month=1; month<=12; month++) {
			
			if(mapAccommodationaid!=null && mapAccommodationaid.containsKey(month)) {
				fld = assignedMonth(month, mapAccommodationaid.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("GCash");
		for(int month=1; month<=12; month++) {
			
			if(mapGCashPaid!=null && mapGCashPaid.containsKey(month)) {
				fld = assignedMonth(month, mapGCashPaid.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Bank/BDO");
		for(int month=1; month<=12; month++) {
			
			if(mapBankPaid!=null && mapBankPaid.containsKey(month)) {
				fld = assignedMonth(month, mapBankPaid.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Other Income");
		for(int month=1; month<=12; month++) {
			
			if(mapCashOtherData!=null && mapCashOtherData.containsKey(month)) {
				fld = assignedMonth(month, mapCashOtherData.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Total");
		double[] month = months;
		for(int m=1; m<=12; m++) {
			fld = assignedMonth(m, month[m-1], fld);
		}
		sums.add(fld);
		
		//free space
		fld = new Fields();
		fld.setName("");
		for(int m=1; m<=12; m++) {
			fld = assignedMonth(m, 0.0, fld);
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Expenses");
		for(int mt=1; mt<=12; mt++) {
			
			if(mapExpense!=null && mapExpense.containsKey(mt)) {
				fld = assignedMonth(mt, mapExpense.get(mt), fld);
			}
			
		}
		sums.add(fld);
		
		//free space
		fld = new Fields();
		fld.setName("");
		for(int m=1; m<=12; m++) {
			fld = assignedMonth(m, 0.0, fld);
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Rooms/BDO/GCash");
		for(int m=1; m<=12; m++) {
			if(mapCombinedRoomhPaid!=null && mapCombinedRoomhPaid.containsKey(m)) {
				fld = assignedMonth(m, mapCombinedRoomhPaid.get(m), fld);
			}
		}
		sums.add(fld);
		
		}catch(Exception e){}
		
		return sums;
	}
	
	public static List<Fields> getYearSummary(int year, int monthh, String date){
		List<Fields> sums = new ArrayList<Fields>();
		months = new double[12];
		
		
		Map<Integer, Double> mapEntranceData = new LinkedHashMap<Integer, Double>();
		
		String sql = "SELECT month(dateTrans) as month, sum(totalamount) as total FROM entrance WHERE isactiveent=1 AND year(dateTrans)=" + year + " GROUP BY month(dateTrans) ORDER BY month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(totalamount) as total FROM entrance WHERE isactiveent=1 AND dateTrans='" + date + "' GROUP BY dateTrans";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(totalamount) as total FROM entrance WHERE isactiveent=1 AND year(dateTrans)=" + year + " AND month(dateTrans)="+ monthh +" GROUP BY month(dateTrans)";
			}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		System.out.println("Entrance " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			mapEntranceData.put(rs.getInt("month"), rs.getDouble("total"));
			//System.out.println(rs.getInt("month") + " amount: " + rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//CASH FROM OTHER INCOME
		Map<Integer, Double> mapCashOtherData = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.CASH_OTHER_INCOME.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.CASH_OTHER_INCOME.getId() +" and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.CASH_OTHER_INCOME.getId() +" and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
		
		ps = conn.prepareStatement(sql);
		System.out.println("Cash From Other Income " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			mapCashOtherData.put(rs.getInt("month"), rs.getDouble("total"));
			//System.out.println(rs.getInt("month") + " amount: " + rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//RESTO ORDER PAID
		Map<Integer, Double> mapRestoData = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.RESTO_ORDER.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.RESTO_ORDER.getId() +" and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.RESTO_ORDER.getId() +" and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
		
		ps = conn.prepareStatement(sql);
		System.out.println("Resto paid " + ps.toString());
		rs = ps.executeQuery();
				
		while(rs.next()){
			mapRestoData.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//RESTO ORDER PAID
		Map<Integer, Double> mapBankPaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.BDO.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.BDO.getId() +" and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.BDO.getId() +" and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
		
		ps = conn.prepareStatement(sql);
		System.out.println("Bank paid " + ps.toString());
		rs = ps.executeQuery();
						
		while(rs.next()){
			mapBankPaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//ACCOMMODATION PAID
		Map<Integer, Double> mapAccommodationaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.ACCOMMODATION_PAID.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.ACCOMMODATION_PAID.getId() +" and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.ACCOMMODATION_PAID.getId() +" and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
		
		ps = conn.prepareStatement(sql);
		System.out.println("accommodation paid " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapAccommodationaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//GCash ORDER PAID
		Map<Integer, Double> mapGCashPaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.GCASH.getId() +" and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.GCASH.getId() +" and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and cashtype="+ CashType.GCASH.getId() +" and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
		
		ps = conn.prepareStatement(sql);
		System.out.println("GCash paid " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapGCashPaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//Expense
		Map<Integer, Double> mapExpense = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
				+ "(cashtype="+ CashType.CASH_EXPENSES.getId() +" OR cashtype=" + CashType.ALL_EXPENSE.getId() + " OR cashtype=" + CashType.CASH_ADVANCE_EMPLOYEE.getId() + " OR cashtype="+ CashType.EMPLOYEE_SALARY.getId() 
				+ " OR cashtype=" + CashType.AMOUNT_PAID_SUPPLIER.getId() + " OR cashtype=" + CashType.FARE.getId() + " OR cashtype=" + CashType.MERRIENDA.getId() + " OR cashtype=" + CashType.CASH_GROCERRY_EXPENSE.getId()
				+ " OR cashtype=" + CashType.GASOLINE.getId() + " OR cashtype=" + CashType.SOLANE.getId() + " OR cashtype=" + CashType.MATERIAL_EXPENSE.getId() + " OR cashtype=" + CashType.RENOVATION_EXPENSE.getId() 
				+ " OR cashtype=" + CashType.PAYMENT_TO_TALENT.getId() + " OR cashtype=" + CashType.COMPANY_EXPENSE_ACTIVITY.getId() + " OR cashtype=" + CashType.NATIVE_CHICKEN_EXPENSES.getId() + " OR cashtype=" + CashType.TILAPIA_EXPENSES.getId()
				+ " OR cashtype=" + CashType.PAYMENT_TO_BUYNSELL.getId() + " OR cashtype=" + CashType.MINERAL_WATER_GALLON_ORDER.getId() + " OR cashtype=" + CashType.SACK_RICE_EXPENSE.getId() + " OR cashtype=" + CashType.VILLA_SHARE_INCOME.getId()
				+ " OR cashtype=" + CashType.SOUVENIR_EXPENSE.getId() + " OR cashtype=" + CashType.SOUVENIR_MINUS_CAPITAL.getId()
				+") "
				+ "and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
								
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
					+ "(cashtype="+ CashType.CASH_EXPENSES.getId() +" OR cashtype=" + CashType.ALL_EXPENSE.getId() + " OR cashtype=" + CashType.CASH_ADVANCE_EMPLOYEE.getId() + " OR cashtype="+ CashType.EMPLOYEE_SALARY.getId() 
					+ " OR cashtype=" + CashType.AMOUNT_PAID_SUPPLIER.getId() + " OR cashtype=" + CashType.FARE.getId() + " OR cashtype=" + CashType.MERRIENDA.getId() + " OR cashtype=" + CashType.CASH_GROCERRY_EXPENSE.getId()
					+ " OR cashtype=" + CashType.GASOLINE.getId() + " OR cashtype=" + CashType.SOLANE.getId() + " OR cashtype=" + CashType.MATERIAL_EXPENSE.getId() + " OR cashtype=" + CashType.RENOVATION_EXPENSE.getId() 
					+ " OR cashtype=" + CashType.PAYMENT_TO_TALENT.getId() + " OR cashtype=" + CashType.COMPANY_EXPENSE_ACTIVITY.getId() + " OR cashtype=" + CashType.NATIVE_CHICKEN_EXPENSES.getId() + " OR cashtype=" + CashType.TILAPIA_EXPENSES.getId()
					+ " OR cashtype=" + CashType.PAYMENT_TO_BUYNSELL.getId() + " OR cashtype=" + CashType.MINERAL_WATER_GALLON_ORDER.getId() + " OR cashtype=" + CashType.SACK_RICE_EXPENSE.getId() + " OR cashtype=" + CashType.VILLA_SHARE_INCOME.getId()
					+ " OR cashtype=" + CashType.SOUVENIR_EXPENSE.getId() + " OR cashtype=" + CashType.SOUVENIR_MINUS_CAPITAL.getId()
					+") "
					+ "and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
						+ "(cashtype="+ CashType.CASH_EXPENSES.getId() +" OR cashtype=" + CashType.ALL_EXPENSE.getId() + " OR cashtype=" + CashType.CASH_ADVANCE_EMPLOYEE.getId() + " OR cashtype="+ CashType.EMPLOYEE_SALARY.getId() 
						+ " OR cashtype=" + CashType.AMOUNT_PAID_SUPPLIER.getId() + " OR cashtype=" + CashType.FARE.getId() + " OR cashtype=" + CashType.MERRIENDA.getId() + " OR cashtype=" + CashType.CASH_GROCERRY_EXPENSE.getId()
						+ " OR cashtype=" + CashType.GASOLINE.getId() + " OR cashtype=" + CashType.SOLANE.getId() + " OR cashtype=" + CashType.MATERIAL_EXPENSE.getId() + " OR cashtype=" + CashType.RENOVATION_EXPENSE.getId() 
						+ " OR cashtype=" + CashType.PAYMENT_TO_TALENT.getId() + " OR cashtype=" + CashType.COMPANY_EXPENSE_ACTIVITY.getId() + " OR cashtype=" + CashType.NATIVE_CHICKEN_EXPENSES.getId() + " OR cashtype=" + CashType.TILAPIA_EXPENSES.getId()
						+ " OR cashtype=" + CashType.PAYMENT_TO_BUYNSELL.getId() + " OR cashtype=" + CashType.MINERAL_WATER_GALLON_ORDER.getId() + " OR cashtype=" + CashType.SACK_RICE_EXPENSE.getId() + " OR cashtype=" + CashType.VILLA_SHARE_INCOME.getId()
						+ " OR cashtype=" + CashType.SOUVENIR_EXPENSE.getId() + " OR cashtype=" + CashType.SOUVENIR_MINUS_CAPITAL.getId()
						+") "
						+ "and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
		
		ps = conn.prepareStatement(sql);
		System.out.println("Expense " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapExpense.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		//Combined ORDER PAID
		Map<Integer, Double> mapCombinedRoomhPaid = new LinkedHashMap<Integer, Double>();
		sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
				+ "(cashtype="+ CashType.GCASH.getId() +" OR cashtype=" + CashType.ACCOMMODATION_PAID.getId() + " OR cashtype=" + CashType.BDO.getId() + ") "
						+ "and year(dateTrans)="+ year +" group by month(dateTrans) order by month(dateTrans)";
		
		if(date!=null) {
			sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
					+ "(cashtype="+ CashType.GCASH.getId() +" OR cashtype=" + CashType.ACCOMMODATION_PAID.getId() + " OR cashtype=" + CashType.BDO.getId() + ") "
							+ "and dateTrans='"+ date +"' group by month(dateTrans)";
		}else {
			if(monthh>0) {
				sql = "SELECT month(dateTrans) as month, sum(amount) as total FROM cashprocess where isactivecash=1 and "
						+ "(cashtype="+ CashType.GCASH.getId() +" OR cashtype=" + CashType.ACCOMMODATION_PAID.getId() + " OR cashtype=" + CashType.BDO.getId() + ") "
								+ "and year(dateTrans)="+ year +" AND month(dateTrans)="+ monthh +" group by month(dateTrans) order by month(dateTrans)";
			}
		}
								
		ps = conn.prepareStatement(sql);
		System.out.println("combined paid " + ps.toString());
		rs = ps.executeQuery();
								
		while(rs.next()){
			mapCombinedRoomhPaid.put(rs.getInt("month"), rs.getDouble("total"));
		}
		
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		
		Fields fld = new Fields();
		fld.setName("Entrance");
		for(int month=1; month<=12; month++) {
			
			if(mapEntranceData!=null && mapEntranceData.containsKey(month)) {
				fld = assignedMonth(month, mapEntranceData.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Resto");
		for(int month=1; month<=12; month++) {
			
			if(mapRestoData!=null && mapRestoData.containsKey(month)) {
				fld = assignedMonth(month, mapRestoData.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Rooms");
		for(int month=1; month<=12; month++) {
			
			if(mapAccommodationaid!=null && mapAccommodationaid.containsKey(month)) {
				fld = assignedMonth(month, mapAccommodationaid.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("GCash");
		for(int month=1; month<=12; month++) {
			
			if(mapGCashPaid!=null && mapGCashPaid.containsKey(month)) {
				fld = assignedMonth(month, mapGCashPaid.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Bank/BDO");
		for(int month=1; month<=12; month++) {
			
			if(mapBankPaid!=null && mapBankPaid.containsKey(month)) {
				fld = assignedMonth(month, mapBankPaid.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Other Income");
		for(int month=1; month<=12; month++) {
			
			if(mapCashOtherData!=null && mapCashOtherData.containsKey(month)) {
				fld = assignedMonth(month, mapCashOtherData.get(month), fld);
			}
			
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Total");
		double[] month = months;
		for(int m=1; m<=12; m++) {
			fld = assignedMonth(m, month[m-1], fld);
		}
		sums.add(fld);
		
		//space
		fld = new Fields();
		fld.setName("");
		for(int m=1; m<=12; m++) {
			fld = assignedMonth(m, 0.0, fld);
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Expenses");
		for(int mt=1; mt<=12; mt++) {
			
			if(mapExpense!=null && mapExpense.containsKey(mt)) {
				fld = assignedMonth(mt, mapExpense.get(mt), fld);
			}
			
		}
		sums.add(fld);
		
		//space
		fld = new Fields();
		fld.setName("");
		for(int m=1; m<=12; m++) {
			fld = assignedMonth(m, 0.0, fld);
		}
		sums.add(fld);
		
		fld = new Fields();
		fld.setName("Rooms/BDO/GCash");
		for(int m=1; m<=12; m++) {
			if(mapCombinedRoomhPaid!=null && mapCombinedRoomhPaid.containsKey(m)) {
				fld = assignedMonth(m, mapCombinedRoomhPaid.get(m), fld);
			}
		}
		sums.add(fld);
		
		}catch(Exception e){}
		
		return sums;
	}
	
	private static Fields assignedMonth(int month, double amount, Fields fld){
		int index = month - 1;
		switch(month) {
			case 1 : fld.setF1(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 2 : fld.setF2(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 3 : fld.setF3(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 4 : fld.setF4(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 5 : fld.setF5(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 6 : fld.setF6(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 7 : fld.setF7(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 8 : fld.setF8(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 9 : fld.setF9(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 10 : fld.setF10(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 11 : fld.setF11(Currency.formatAmount(amount)); months[index]+=amount; break;
			case 12 : fld.setF12(Currency.formatAmount(amount)); months[index]+=amount; break;
		}
		return fld;
	}
	
	public static List<Fields> getAllYear(int fromYear){
		List<Fields> sums = new ArrayList<Fields>();
		
		for(int year=fromYear; year<=DateUtils.getCurrentYear(); year++) {
			 Fields f = builder().name(year+"").build();
			 sums.add(f);
			 List<Fields> flds = getYearSummary(year);
			 if(flds!=null &&  flds.size()>0) {
				 sums.addAll(flds);
			 }
		}
		
		return sums;
	}
	
	public static void main(String[] args) {
		for(Fields f : getYearSummary(2024)) {
			System.out.println(f.getName() + "\tf1: " + f.getF1() + "\tf2:" + f.getF2() + "\tf3:" + f.getF4() + "\tf4:" + f.getF4() + "\tf5:" + f.getF5() + "\tf6:" + f.getF6() + "\tf7:" + f.getF7() + "\tf8:" + f.getF8());
		}
	}
	
}
