package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.utils.GlobalVar;
import com.italia.utils.LogU;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Mark Italia
 * @since 08/03/2025
 * @version 1.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UpcomingEvents {
	
	private long id;
	private String dateReserved;
	private String dateEventStart;
	private String dateEventEnd;
	private double numberOfDays;
	private String companyName;
	private String notes;
	private CustomerProfile reservedBy;
	private double totalBill;
	private String fullpaymentdate;
	private double downpayment;
	private String downpaymentdate;
	private int numberOfPax;
	private int eventType;
	private int isActive;
	private int eventStatus;
	
	
	public static List<UpcomingEvents> getAll(int limit){
		String sql = " ORDER BY ev.evid DESC ";
		if(limit>0) {
			sql += " LIMIT " + limit;
		}
		return retrieve(sql, new String[0]);
	}
	
	public static List<UpcomingEvents> retrieve(String sqlAdd, String[] params){
		List<UpcomingEvents> props = new ArrayList<UpcomingEvents>();
		
		String tableCus = "cz";
		String tableEvent = "ev";
		String sql = "SELECT * FROM upcomingevents "+ tableEvent +", customerprofile "+ tableCus +" WHERE "+ tableEvent +".isactivee=1 AND " + tableEvent + ".cid=" + tableCus + ".cid ";		
		sql = sql + sqlAdd;		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("upcomingevents sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			String address = rs.getString("address")==null? "" : rs.getString("address");
			address = address.isEmpty()? "" : rs.getString("address");
			
			String contact = rs.getString("contactno")==null? "" : rs.getString("contactno");
			contact = contact.isEmpty()? "" : rs.getString("contactno");
			
			CustomerProfile cp = CustomerProfile.builder()
					.id(rs.getLong("cid"))
					.dateTrans(rs.getString("datetrans"))
					.idNumber(rs.getString("idno"))
					.fullname(rs.getString("fullname"))
					.address(address)
					.contactNo(contact)
					.isActive(rs.getInt("isactivec"))
					.build();
			
			UpcomingEvents prop = builder()
					.id(rs.getLong("evid"))
					.dateReserved(rs.getString("dateres"))
					.dateEventStart(rs.getString("dateeventstart"))
					.dateEventEnd(rs.getString("dateeventend"))
					.numberOfDays(rs.getDouble("numberofdays"))
					.companyName(rs.getString("companyname"))
					.notes(rs.getString("notes"))
					.totalBill(rs.getDouble("totalbill"))
					.fullpaymentdate(rs.getString("fullpaymentdate"))
					.downpayment(rs.getDouble("downpayment"))
					.downpaymentdate(rs.getString("downpaymentdate"))
					.numberOfPax(rs.getInt("pax"))
					.eventType(rs.getInt("eventtype"))
					.eventStatus(rs.getInt("eventstatus"))
					.isActive(rs.getInt("isactivee"))
					.reservedBy(cp)
					.build();
			
			props.add(prop);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return props;
	}
	
	public static UpcomingEvents save(UpcomingEvents vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = UpcomingEvents.getInfo(vr.getId() ==0? UpcomingEvents.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = UpcomingEvents.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = UpcomingEvents.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = UpcomingEvents.insertData(vr, "3");
			}
			LogU.close();
		}
		return vr;
	}
	
	public void save() {
		save(this);
	}
	
	public static UpcomingEvents insertData(UpcomingEvents vr, String type){
		String sql = "INSERT INTO upcomingevents ("
				+ "evid,"
				+ "dateres,"
				+ "dateeventstart,"
				+ "dateeventend,"
				+ "numberofdays,"
				+ "companyname,"
				+ "notes,"
				+ "totalbill,"
				+ "fullpaymentdate,"
				+ "downpayment,"
				+ "downpaymentdate,"
				+ "pax,"
				+ "eventtype,"
				+ "isactivee,"
				+ "eventstatus,"
				+ "cid)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table upcomingevents");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			vr.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			vr.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, vr.getDateReserved());
		ps.setString(cnt++, vr.getDateEventStart());
		ps.setString(cnt++, vr.getDateEventEnd());
		ps.setDouble(cnt++, vr.getNumberOfDays());
		ps.setString(cnt++, vr.getCompanyName());
		ps.setString(cnt++, vr.getNotes());
		ps.setDouble(cnt++, vr.getTotalBill());
		ps.setString(cnt++, vr.getFullpaymentdate());
		ps.setDouble(cnt++, vr.getDownpayment());
		ps.setString(cnt++, vr.getDownpaymentdate());
		ps.setInt(cnt++, vr.getNumberOfPax());
		ps.setInt(cnt++, vr.getEventType());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setInt(cnt++, vr.getEventStatus());
		ps.setLong(cnt++, vr.getReservedBy().getId());
		
		
		LogU.add(vr.getDateReserved());
		LogU.add(vr.getDateEventStart());
		LogU.add(vr.getDateEventEnd());
		LogU.add(vr.getNumberOfDays());
		LogU.add(vr.getCompanyName());
		LogU.add(vr.getNotes());
		LogU.add(vr.getTotalBill());
		LogU.add(vr.getFullpaymentdate());
		LogU.add(vr.getDownpayment());
		LogU.add(vr.getDownpaymentdate());
		LogU.add(vr.getNumberOfPax());
		LogU.add(vr.getEventType());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEventStatus());
		LogU.add(vr.getReservedBy().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to upcomingevents : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static UpcomingEvents updateData(UpcomingEvents vr){
		String sql = "UPDATE upcomingevents SET "
				+ "dateres=?,"
				+ "dateeventstart=?,"
				+ "dateeventend=?,"
				+ "numberofdays=?,"
				+ "companyname=?,"
				+ "notes=?,"
				+ "totalbill=?,"
				+ "fullpaymentdate=?,"
				+ "downpayment=?,"
				+ "downpaymentdate=?,"
				+ "pax=?,"
				+ "eventtype=?,"
				+ "isactivee=?,"
				+ "eventstatus=?,"
				+ "cid=?" 
				+ " WHERE evid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		LogU.add("inserting data into table upcomingevents");
		
		ps.setString(cnt++, vr.getDateReserved());
		ps.setString(cnt++, vr.getDateEventStart());
		ps.setString(cnt++, vr.getDateEventEnd());
		ps.setDouble(cnt++, vr.getNumberOfDays());
		ps.setString(cnt++, vr.getCompanyName());
		ps.setString(cnt++, vr.getNotes());
		ps.setDouble(cnt++, vr.getTotalBill());
		ps.setString(cnt++, vr.getFullpaymentdate());
		ps.setDouble(cnt++, vr.getDownpayment());
		ps.setString(cnt++, vr.getDownpaymentdate());
		ps.setInt(cnt++, vr.getNumberOfPax());
		ps.setInt(cnt++, vr.getEventType());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setInt(cnt++, vr.getEventStatus());
		ps.setLong(cnt++, vr.getReservedBy().getId());
		ps.setLong(cnt++, vr.getId());
		
		
		LogU.add(vr.getDateReserved());
		LogU.add(vr.getDateEventStart());
		LogU.add(vr.getDateEventEnd());
		LogU.add(vr.getNumberOfDays());
		LogU.add(vr.getCompanyName());
		LogU.add(vr.getNotes());
		LogU.add(vr.getTotalBill());
		LogU.add(vr.getFullpaymentdate());
		LogU.add(vr.getDownpayment());
		LogU.add(vr.getDownpaymentdate());
		LogU.add(vr.getNumberOfPax());
		LogU.add(vr.getEventType());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEventStatus());
		LogU.add(vr.getReservedBy().getId());
		LogU.add(vr.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to upcomingevents : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT evid FROM upcomingevents  ORDER BY evid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("evid");
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT evid FROM upcomingevents WHERE evid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		DBConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE upcomingevents set isactivee=0 WHERE evid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		DBConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
}
