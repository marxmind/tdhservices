package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.utils.Currency;

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
public class CashAdvanceBalance {
	
	private String name;
	private String newca;
	private String balance;
	private String paidca;
	
	public static List<CashAdvanceBalance> getAllEmployeeBalance(){
		List<CashAdvanceBalance> amnts = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT bal.newadded,bal.balance,bal.deducted,ee.fullname FROM employeepayable bal, employees ee WHERE bal.eid=ee.eid AND bal.balance>0 ORDER BY ee.fullname");
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			CashAdvanceBalance cash = builder()
					.name(rs.getString("fullname"))
					.newca(Currency.formatAmount(rs.getDouble("newadded")))
					.balance(Currency.formatAmount(rs.getDouble("balance")))
					.paidca(Currency.formatAmount(rs.getDouble("deducted")))
					.build();
			
			amnts.add(cash);
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		
		}catch(Exception e){e.getMessage();}
		
		return amnts;
	}
	
	public static List<CashAdvanceBalance> getEmployeeBalance(int eid){
		List<CashAdvanceBalance> amnts = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT bal.newadded,bal.balance,bal.deducted,ee.fullname FROM employeepayable "
				+ "bal, employees ee WHERE bal.eid=ee.eid AND bal.balance>=0 AND ee.eid=" + eid
				+ " ORDER BY ee.fullname");
		System.out.println("Balance: SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			CashAdvanceBalance cash = builder()
					.name(rs.getString("fullname"))
					.newca(Currency.formatAmount(rs.getDouble("newadded")))
					.balance(Currency.formatAmount(rs.getDouble("balance")))
					.paidca(Currency.formatAmount(rs.getDouble("deducted")))
					.build();
			
			amnts.add(cash);
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		
		}catch(Exception e){e.getMessage();}
		
		return amnts;
	}
	
}
