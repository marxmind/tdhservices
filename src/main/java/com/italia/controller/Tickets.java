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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Tickets {

	private long id;
	private String dateTrans;
	private int numberOfGuest;
	private int type;
	private double amount;
	private int isActive;
	
	
	public static List<Tickets> getALLTickets(){
		List<Tickets> tickets = new ArrayList<Tickets>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM tickets WHERE isactivet=1";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				Tickets ticket = builder()
						.id(rs.getLong("tid"))
						.dateTrans(rs.getString("datetrans"))
						.numberOfGuest(rs.getInt("numofguest"))
						.type(rs.getInt("typeid"))
						.amount(rs.getDouble("amount"))
						.isActive(rs.getInt("isactivet"))
						.build();
				tickets.add(ticket);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return tickets;
	}
	
	public static Tickets getById(int id){
		Tickets ticket = new Tickets();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM tickets WHERE isactivet=1 AND tid=" + id;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				ticket = builder()
						.id(rs.getLong("tid"))
						.dateTrans(rs.getString("datetrans"))
						.numberOfGuest(rs.getInt("numofguest"))
						.type(rs.getInt("typeid"))
						.amount(rs.getDouble("amount"))
						.isActive(rs.getInt("isactivet"))
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return ticket;
	}
	
	public static Tickets getByName(String name){
		Tickets ticket = new Tickets();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM stocks WHERE isactives=1 AND stocknames like '%"+ name +"%' ORDER BY stocknames";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			
			rs = ps.executeQuery();
			//System.out.println("getByName: " + ps.toString());
			while(rs.next()){
				
				ticket = builder()
						.id(rs.getLong("tid"))
						.dateTrans(rs.getString("datetrans"))
						.numberOfGuest(rs.getInt("numofguest"))
						.type(rs.getInt("typeid"))
						.amount(rs.getDouble("amount"))
						.isActive(rs.getInt("isactivet"))
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return ticket;
	}
	
	public static Tickets save(Tickets st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Tickets.getInfo(st.getId() ==0? Tickets.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Tickets.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Tickets.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Tickets.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save(){
		LogU.open(true, GlobalVar.LOG_FOLDER);
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			Tickets.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			Tickets.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			Tickets.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static Tickets insertData(Tickets st, String type){
		String sql = "INSERT INTO tickets ("
				+ "tid,"
				+ "datetrans,"
				+ "isactivet,"
				+ "typeid,"
				+ "numofguest,"
				+ "amount)" 
				+ " VALUES(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table tickets");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getType());
		ps.setInt(cnt++, st.getNumberOfGuest());
		ps.setDouble(cnt, st.getAmount());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getIsActive());
		LogU.add(st.getType());
		LogU.add(st.getNumberOfGuest());
		LogU.add(st.getAmount());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to tickets : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static Tickets updateData(Tickets st){
		String sql = "UPDATE tickets SET "
				+ "datetrans=?,"
				+ "typeid=?,"
				+ "numofguest=?,"
				+ "amount=?" 
				+ " WHERE tid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table tickets");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getType());
		ps.setInt(cnt++, st.getNumberOfGuest());
		ps.setDouble(cnt, st.getAmount());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getType());
		LogU.add(st.getNumberOfGuest());
		LogU.add(st.getAmount());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to tickets : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT tid FROM tickets ORDER BY tid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("tid");
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
		ps = conn.prepareStatement("SELECT tid FROM tickets WHERE tid=?");
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
		String sql = "UPDATE tickets SET isactivet=0 WHERE tid=?";
		
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
		String sql = "UPDATE tickets SET isactivet=0 WHERE tid=" + idx;
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