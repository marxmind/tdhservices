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
public class User {
	private int id;
	private String username;
	private String password;
	private String fullName;
	private int isActive;
	private int accessLevel;
	private int department;
	private int eid;
	
	
	public static List<User> getAllUser(){
		List<User> users = new ArrayList<User>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM appuser WHERE isactiveu=1 ";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("user: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				final User user = builder()
						.id(rs.getInt("usid"))
						.username(rs.getString("username"))
						.password(rs.getString("password"))
						.fullName(rs.getString("fullname"))
						.isActive(rs.getInt("isactiveu"))
						.accessLevel(rs.getInt("levelu"))
						.department(rs.getInt("depid"))
						.eid(rs.getInt("eid"))
						.build();
				users.add(user);
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return users;
	}
	
	public static User getById(int id){
		User user = new User();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM appuser WHERE isactiveu=1 AND usid=" + id;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("user: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				user = builder()
						.id(rs.getInt("usid"))
						.username(rs.getString("username"))
						.password(rs.getString("password"))
						.fullName(rs.getString("fullname"))
						.isActive(rs.getInt("isactiveu"))
						.accessLevel(rs.getInt("levelu"))
						.department(rs.getInt("depid"))
						.eid(rs.getInt("eid"))
						.build();
				
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return user;
	}
	
	public static User save(User st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = User.getInfo(st.getId() ==0? User.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = User.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = User.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = User.insertData(st, "3");
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
			User.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			User.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			User.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static User insertData(User st, String type){
		String sql = "INSERT INTO appuser ("
				+ "usid,"
				+ "username,"
				+ "password,"
				+ "fullname,"
				+ "isactiveu,"
				+ "levelu,"
				+ "depid,"
				+ "eid)" 
				+ " VALUES(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table appuser");
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
		
		
		ps.setString(cnt++, st.getUsername());
		ps.setString(cnt++, st.getPassword());
		ps.setString(cnt++, st.getFullName());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getAccessLevel());
		ps.setInt(cnt++, st.getDepartment());
		ps.setInt(cnt++, st.getEid());
		
		LogU.add(st.getUsername());
		LogU.add(st.getPassword());
		LogU.add(st.getFullName());
		LogU.add(st.getIsActive());
		LogU.add(st.getAccessLevel());
		LogU.add(st.getDepartment());
		LogU.add(st.getEid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to appuser : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static User updateData(User st){
		String sql = "UPDATE appuser SET"
				+ "username=?,"
				+ "password=?,"
				+ "fullname=?,"
				+ "levelu=?,"
				+ "depid=?,"
				+ "eid=?" 
				+ " WHERE usid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table appuser");
		
		ps.setString(cnt++, st.getUsername());
		ps.setString(cnt++, st.getPassword());
		ps.setString(cnt++, st.getFullName());
		ps.setInt(cnt++, st.getAccessLevel());
		ps.setInt(cnt++, st.getDepartment());
		ps.setInt(cnt++, st.getEid());
		ps.setInt(cnt++, st.getId());
		
		LogU.add(st.getUsername());
		LogU.add(st.getPassword());
		LogU.add(st.getFullName());
		LogU.add(st.getAccessLevel());
		LogU.add(st.getDepartment());
		LogU.add(st.getEid());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to appuser : " + s.getMessage());
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
		sql="SELECT usid FROM appuser ORDER BY usid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("usid");
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
		ps = conn.prepareStatement("SELECT usid FROM appuser WHERE usid=?");
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
		String sql = "UPDATE appuser SET isactiveu=0 WHERE usid=?";
		
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
		String sql = "UPDATE housestocks SET isactiveh=0 WHERE hid=" + idx;
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
