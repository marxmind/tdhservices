package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.CashType;
import com.italia.enm.ReservationType;
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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {

	private long id;
	private String dateBooking;
	private String dateCheckIn;
	private String dateCheckOut;
	private String customerName;
	private String description;
	private double downpayment;
	private int customerId;
	private String roomName;
	private int user;
	private int sms;
	
	private int adultCount;
	private int childCount;
	private String vehiclePlates;
	
	private int status;
	private String timeCheckIn;
	private String timeCheckOut;
	
	public static List<Reservation> getManyTimesBooking(int numberOccured){
		String sql = "SELECT title, count(cid) as occurrence, cid FROM  reservation GROUP BY cid HAVING count(cid) >= "+ numberOccured +" ORDER BY occurrence ";
		List<Reservation> rsvs = new ArrayList<Reservation>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("Reservations PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Reservation rv = Reservation.builder()
						.id(rs.getLong("occurrence"))
						.dateBooking("")
						.dateCheckIn("")
						.dateCheckOut("")
						.customerId(rs.getInt("cid"))
						.customerName(rs.getString("title"))
						.description("")
						.downpayment(0)
						.roomName("")
						.user(0)
						.sms(0)
						.adultCount(0)
						.childCount(0)
						.vehiclePlates("")
						.timeCheckIn("")
						.timeCheckOut("")
						.status(0)
						.build();
				
				rsvs.add(rv);
				
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rsvs;
	}
	
	public static List<Reservation> getCustomerCid(long cid){
		List<Reservation> rsvs = new ArrayList<Reservation>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String tableRsv = "rsv";
		String tableUser = "usr";
		String tableCustomer = "cs";
		String sql = "SELECT rsv.*,"
				+ "usr.userdtlsid,usr.firstname,usr.middlename,usr.lastname,cs.*  "
				+ "FROM reservation "+ tableRsv + ", userdtls "+ tableUser +", customerprofile "+ tableCustomer +" WHERE "+ tableRsv +".isactiveres=1 AND " +
		tableRsv + ".userid= "+tableUser+".userdtlsid AND " +
		tableRsv	+ ".cid=" + tableCustomer + ".cid "; 
		
		
		sql += " AND "+ tableCustomer + ".cid="+ cid;
		sql += " ORDER BY  " + tableRsv + ".startDate DESC";
		
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("Reservations PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				String desc = rs.getString("description").replace("\n", " ");
				
				Reservation rv = Reservation.builder()
						.id(rs.getLong("rid"))
						.dateBooking(rs.getString("dateTrans"))
						.dateCheckIn(rs.getString("startDate"))
						.dateCheckOut(rs.getString("endDate"))
						.customerId(rs.getInt("cid"))
						.customerName(rs.getString("fullname"))
						.description(desc)
						.downpayment(rs.getDouble("price"))
						.roomName(ReservationType.containId(rs.getInt("scheduleType")).getName())
						.user(rs.getInt("userdtlsid"))
						.sms(rs.getInt("smssend"))
						.adultCount(rs.getInt("adultcount"))
						.childCount(rs.getInt("childcount"))
						.vehiclePlates(rs.getString("vehicleplates"))
						.timeCheckIn(rs.getString("startTime"))
						.timeCheckOut(rs.getString("endTime"))
						.status(rs.getInt("iswholeday"))
						.build();
				
				rsvs.add(rv);
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		
		return rsvs;
	}
	
	public static List<Reservation> getCustomerNames(String name){
		List<Reservation> rsvs = new ArrayList<Reservation>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String tableRsv = "rsv";
		String tableUser = "usr";
		String tableCustomer = "cs";
		String sql = "SELECT rsv.*,"
				+ "usr.userdtlsid,usr.firstname,usr.middlename,usr.lastname,cs.*  "
				+ "FROM reservation "+ tableRsv + ", userdtls "+ tableUser +", customerprofile "+ tableCustomer +" WHERE "+ tableRsv +".isactiveres=1 AND " +
		tableRsv + ".userid= "+tableUser+".userdtlsid AND " +
		tableRsv	+ ".cid=" + tableCustomer + ".cid "; 
		
		if(name==null) {
			sql += " ORDER BY " + tableRsv + ".cid DESC LIMIT 20";
		}else {
			sql += " AND "+ tableCustomer + ".fullname like '%"+ name +"%'";
			sql += " ORDER BY  "+ tableCustomer + ".fullname";
		}
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("Reservations PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				String desc = rs.getString("description").replace("\n", " ");
				
				Reservation rv = Reservation.builder()
						.id(rs.getLong("rid"))
						.dateBooking(rs.getString("dateTrans"))
						.dateCheckIn(rs.getString("startDate"))
						.dateCheckOut(rs.getString("endDate"))
						.customerId(rs.getInt("cid"))
						.customerName(rs.getString("fullname"))
						.description(desc)
						.downpayment(rs.getDouble("price"))
						.roomName(ReservationType.containId(rs.getInt("scheduleType")).getName())
						.user(rs.getInt("userdtlsid"))
						.sms(rs.getInt("smssend"))
						.adultCount(rs.getInt("adultcount"))
						.childCount(rs.getInt("childcount"))
						.vehiclePlates(rs.getString("vehicleplates"))
						.timeCheckIn(rs.getString("startTime"))
						.timeCheckOut(rs.getString("endTime"))
						.status(rs.getInt("iswholeday"))
						.build();
				
				rsvs.add(rv);
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		
		return rsvs;
	}
	
	public static List<Reservation> getAll(int year, int month, String date){
		List<Reservation> rsvs = new ArrayList<Reservation>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String tableRsv = "rsv";
		String tableUser = "usr";
		String tableCustomer = "cs";
		String sql = "SELECT rsv.*,"
				+ "usr.userdtlsid,usr.firstname,usr.middlename,usr.lastname,cs.*  "
				+ "FROM reservation "+ tableRsv + ", userdtls "+ tableUser +", customerprofile "+ tableCustomer +" WHERE "+ tableRsv +".isactiveres=1 AND " +
		tableRsv + ".userid= "+tableUser+".userdtlsid AND " +
		tableRsv	+ ".cid=" + tableCustomer + ".cid "; 
		
		if(date==null) {
			sql += " AND year("+ tableRsv + ".startDate)="+ year +" AND month(" + tableRsv + ".startDate)=" + month;
			sql += " ORDER BY " + tableRsv + ".startDate";
		}else {
			sql += " AND "+ tableRsv + ".startDate='"+ date +"'";
			sql += " ORDER BY  "+ tableCustomer + ".fullname";
		}
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("Reservations PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				String desc = rs.getString("description").replace("\n", " ");
				
				Reservation rv = Reservation.builder()
						.id(rs.getLong("rid"))
						.dateBooking(rs.getString("dateTrans"))
						.dateCheckIn(rs.getString("startDate"))
						.dateCheckOut(rs.getString("endDate"))
						.customerId(rs.getInt("cid"))
						.customerName(rs.getString("fullname"))
						.description(desc)
						.downpayment(rs.getDouble("price"))
						.roomName(ReservationType.containId(rs.getInt("scheduleType")).getName())
						.user(rs.getInt("userdtlsid"))
						.sms(rs.getInt("smssend"))
						.adultCount(rs.getInt("adultcount"))
						.childCount(rs.getInt("childcount"))
						.vehiclePlates(rs.getString("vehicleplates"))
						.timeCheckIn(rs.getString("startTime"))
						.timeCheckOut(rs.getString("endTime"))
						.status(rs.getInt("iswholeday"))
						.build();
				
				rsvs.add(rv);
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		
		return rsvs;
	}
	
	public static Reservation save(Reservation st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Reservation.getInfo(st.getId() ==0? Reservation.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Reservation.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Reservation.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Reservation.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static Reservation insertData(Reservation in, String type){
		String sql = "INSERT INTO reservation ("
				+ "rid,"
				+ "calendarid,"
				+ "dateTrans,"
				+ "title,"
				+ "description,"
				+ "startDate,"
				+ "startTime,"
				+ "endDate,"
				+ "endTime,"
				+ "scheduleType,"
				+ "iswholeday,"
				+ "isactiveres,"
				+ "userid,"
				+ "price,"
				+ "cid,"
				+ "smssend,"
				+ "adultcount,"
				+ "childcount,"
				+ "vehicleplates) " 
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table reservation");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			in.setId(Long.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			in.setId(Long.valueOf(id));
			LogU.add("logid: " + id);
		}
		int typeRoom = ReservationType.containName(in.getRoomName()).getId();
		ps.setString(cnt++, in.getDateCheckIn());
		ps.setString(cnt++, in.getDateBooking());
		ps.setString(cnt++, in.getCustomerName());
		ps.setString(cnt++, in.getDescription());
		ps.setString(cnt++, in.getDateCheckIn());
		ps.setString(cnt++, in.getTimeCheckIn());
		ps.setString(cnt++, in.getDateCheckOut());
		ps.setString(cnt++, in.getTimeCheckOut());
		ps.setInt(cnt++, typeRoom);
		ps.setInt(cnt++, 1);
		ps.setInt(cnt++, 1);
		ps.setLong(cnt++, in.getUser());
		ps.setDouble(cnt++, in.getDownpayment());
		ps.setLong(cnt++, in.getCustomerId());
		ps.setInt(cnt++, in.getSms());
		ps.setInt(cnt++, in.getAdultCount());
		ps.setInt(cnt++, in.getChildCount());
		ps.setString(cnt++, in.getVehiclePlates());
		
		LogU.add(in.getDateCheckIn());
		LogU.add(in.getDateBooking());
		LogU.add(in.getCustomerName());
		LogU.add(in.getDescription());
		LogU.add(in.getDateCheckIn());
		LogU.add(in.getTimeCheckIn());
		LogU.add(in.getDateCheckOut());
		LogU.add(in.getTimeCheckOut());
		LogU.add(typeRoom);
		LogU.add(1);
		LogU.add(1);
		LogU.add(in.getUser());
		LogU.add(in.getDownpayment());
		LogU.add(in.getCustomerId());
		LogU.add(in.getSms());
		LogU.add(in.getAdultCount());
		LogU.add(in.getChildCount());
		LogU.add(in.getVehiclePlates());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to reservation : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static Reservation updateData(Reservation in){
		String sql = "UPDATE reservation SET "
				+ "calendarid=?,"
				+ "dateTrans=?,"
				+ "title=?,"
				+ "description=?,"
				+ "startDate=?,"
				+ "startTime=?,"
				+ "endDate=?,"
				+ "endTime=?,"
				+ "scheduleType=?,"
				+ "iswholeday=?,"
				+ "userid=?,"
				+ "price=?,"
				+ "cid=?,"
				+ "smssend=?,"
				+ "adultcount=?,"
				+ "childcount=?,"
				+ "vehicleplates=? " 
				+ " WHERE rid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table reservation");
		
		int typeRoom = ReservationType.containName(in.getRoomName()).getId();
		ps.setString(cnt++, in.getDateCheckIn());
		ps.setString(cnt++, in.getDateBooking());
		ps.setString(cnt++, in.getCustomerName());
		ps.setString(cnt++, in.getDescription());
		ps.setString(cnt++, in.getDateCheckIn());
		ps.setString(cnt++, in.getTimeCheckIn());
		ps.setString(cnt++, in.getDateCheckOut());
		ps.setString(cnt++, in.getTimeCheckOut());
		ps.setInt(cnt++, typeRoom);
		ps.setInt(cnt++, 1);
		ps.setLong(cnt++, in.getUser());
		ps.setDouble(cnt++, in.getDownpayment());
		ps.setLong(cnt++, in.getCustomerId());
		ps.setInt(cnt++, in.getSms());
		ps.setInt(cnt++, in.getAdultCount());
		ps.setInt(cnt++, in.getChildCount());
		ps.setString(cnt++, in.getVehiclePlates());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateCheckIn());
		LogU.add(in.getDateBooking());
		LogU.add(in.getCustomerName());
		LogU.add(in.getDescription());
		LogU.add(in.getDateCheckIn());
		LogU.add(in.getTimeCheckIn());
		LogU.add(in.getDateCheckOut());
		LogU.add(in.getTimeCheckOut());
		LogU.add(typeRoom);
		LogU.add(1);
		LogU.add(in.getUser());
		LogU.add(in.getDownpayment());
		LogU.add(in.getCustomerId());
		LogU.add(in.getSms());
		LogU.add(in.getAdultCount());
		LogU.add(in.getChildCount());
		LogU.add(in.getVehiclePlates());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to reservation : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT rid FROM reservation ORDER BY rid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("rid");
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT rid FROM reservation WHERE rid=?");
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
		String sql = "UPDATE reservation SET isactiveres=0 WHERE rid=?";
		
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
	
	public static void openUpdate(String sql){
		
		Connection conn = null;
		PreparedStatement ps = null;
		//String sql = "UPDATE reservation SET isactiveres=0 WHERE rid=?";
		
		//String[] params = new String[1];
		//params[0] = getId()+"";
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);		
		ps.executeUpdate();
		ps.close();
		DBConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public static boolean delete(int idx){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE reservation SET isactiveres=0 WHERE rid=" + idx;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			DBConnect.close(conn);
			System.out.println("Executing deletion....");
			return true;
			}catch(Exception e){e.getMessage();}
		
		return false;
	}
}
