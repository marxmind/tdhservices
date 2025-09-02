package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.ReservationType;
import com.italia.utils.GlobalVar;
import com.italia.utils.LogU;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Mark Italia
 * @since 8/20/2025
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Breakfast {

	private long id;
	private String dateRec;
	private String dateServing;
	private String timeServing;
	private String notes;
	private int isActive;
	private Reservation reservation;
	private int status;
	private String waiter;
	
	private List<BreakfastItems> items;
	
	public static List<Breakfast> getAll(int year, int month, String date){
		List<Breakfast> rsvs = new ArrayList<Breakfast>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String tableBf = "bf";
		String tableRv = "rv";
		String sql = "SELECT * FROM breakfast " + tableBf + ", reservation " + tableRv + " WHERE " + tableBf + ".isactiveb=1 AND " + tableBf + ".rid=" + tableRv + ".rid ";
		
		
		if(date==null) {
			sql += " AND year("+ tableBf + ".dateserve)="+ year +" AND month(" + tableBf + ".dateserve)=" + month;
			sql += " ORDER BY " + tableBf + ".bid";
		}else {
			sql += " AND "+ tableBf + ".dateserve='"+ date +"'";
			sql += " ORDER BY  "+ tableBf + ".bid";
		}
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("breakfast PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				String desc = rs.getString("description").replace("\n", " ");
				System.out.println("desc==========================" + desc);
				Reservation rv = Reservation.builder()
						.id(rs.getLong("rid"))
						.dateBooking(rs.getString("dateTrans"))
						.dateCheckIn(rs.getString("startDate"))
						.dateCheckOut(rs.getString("endDate"))
						.customerId(rs.getInt("cid"))
						//.customerName(rs.getString("fullname"))
						.description(desc)
						.downpayment(rs.getDouble("price"))
						.roomName(ReservationType.containId(rs.getInt("scheduleType")).getName())
						.user(rs.getInt("userid"))
						.sms(rs.getInt("smssend"))
						.build();
				
				
				List<BreakfastItems> items = BreakfastItems.retriveByBreakfastId(rs.getLong("bid"));
				
				Breakfast bf = builder()
						.id(rs.getLong("bid"))
						.dateRec(rs.getString("daterec"))
						.dateServing(rs.getString("dateserve"))
						.timeServing(rs.getString("timeserve"))
						.notes(rs.getString("notes"))
						.isActive(rs.getInt("isactiveb"))
						.reservation(rv)
						.items(items)
						.status(rs.getInt("status"))
						.waiter(rs.getString("waiter"))
						.build();
				
				rsvs.add(bf);
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		return rsvs;
	}
	
	public static Breakfast save(Breakfast st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Breakfast.getInfo(st.getId() ==0? Breakfast.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Breakfast.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Breakfast.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Breakfast.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static Breakfast insertData(Breakfast in, String type){
		String sql = "INSERT INTO breakfast ("
				+ "bid,"
				+ "daterec,"
				+ "dateserve,"
				+ "timeserve,"
				+ "notes,"
				+ "isactiveb,"
				+ "rid,"
				+ "status,"
				+ "waiter) " 
				+ " values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table breakfast");
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
		
		ps.setString(cnt++, in.getDateRec());
		ps.setString(cnt++, in.getDateServing());
		ps.setString(cnt++, in.getTimeServing());
		ps.setString(cnt++, in.getNotes());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getReservation().getId());
		ps.setInt(cnt++, in.getStatus());
		ps.setString(cnt++, in.getWaiter());
		
		LogU.add(in.getDateRec());
		LogU.add(in.getDateServing());
		LogU.add(in.getTimeServing());
		LogU.add(in.getNotes());
		LogU.add(in.getIsActive());
		LogU.add(in.getReservation().getId());
		LogU.add(in.getStatus());
		LogU.add(in.getWaiter());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to breakfast : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static Breakfast updateData(Breakfast in){
		String sql = "UPDATE breakfast SET "
				+ "daterec=?,"
				+ "dateserve=?,"
				+ "timeserve=?,"
				+ "notes=?,"
				+ "isactiveb=?,"
				+ "rid=?,"
				+ "status=?,"
				+ "waiter=? " 
				+ " WHERE bid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table breakfast");
		
		
		ps.setString(cnt++, in.getDateRec());
		ps.setString(cnt++, in.getDateServing());
		ps.setString(cnt++, in.getTimeServing());
		ps.setString(cnt++, in.getNotes());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getReservation().getId());
		ps.setInt(cnt++, in.getStatus());
		ps.setString(cnt++, in.getWaiter());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateRec());
		LogU.add(in.getDateServing());
		LogU.add(in.getTimeServing());
		LogU.add(in.getNotes());
		LogU.add(in.getIsActive());
		LogU.add(in.getReservation().getId());
		LogU.add(in.getStatus());
		LogU.add(in.getWaiter());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to breakfast : " + s.getMessage());
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
		sql="SELECT bid FROM breakfast ORDER BY bid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("bid");
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
		ps = conn.prepareStatement("SELECT bid FROM breakfast WHERE bid=?");
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
		String sql = "UPDATE breakfast SET isactiveb=0 WHERE bid=?";
		
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
	
	public static boolean delete(int idx){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE breakfast SET isactiveres=0 WHERE bid=" + idx;
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
