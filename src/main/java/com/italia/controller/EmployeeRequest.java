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
public class EmployeeRequest {

	private long id;
	private String date;
	private String employee;
	private String requests;
	private int status;
	private int isActive;
	private long eid;
	
	public static List<EmployeeRequest> retrive(String sql, String[] params){
		List<EmployeeRequest> recs = new ArrayList<EmployeeRequest>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sqlTmp = "SELECT * FROM employeerequests WHERE isactiverq=1 ";
		
		sqlTmp = sqlTmp + sql;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqlTmp);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("employeerequests: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				EmployeeRequest rr = builder()
						.id(rs.getLong("rcid"))
						.date(rs.getString("datereq"))
						.employee(rs.getString("employee"))
						.requests(rs.getString("requests"))
						.status(rs.getInt("status"))
						.isActive(rs.getInt("isactiverq"))
						.eid(rs.getLong("eid"))
						.build();
				
				recs.add(rr);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return recs;
	}
	
	public static EmployeeRequest save(EmployeeRequest st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = EmployeeRequest.getInfo(st.getId() ==0? EmployeeRequest.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = EmployeeRequest.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = EmployeeRequest.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = EmployeeRequest.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static EmployeeRequest insertData(EmployeeRequest in, String type){
		String sql = "INSERT INTO employeerequests ("
				+ "rid,"
				+ "datereq,"
				+ "employee,"
				+ "requests,"
				+ "status,"
				+ "isactiverq,"
				+ "eid)" 
				+ " values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employeerequests");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			in.setId(id);
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			in.setId(id);
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getEmployee());
		ps.setString(cnt++, in.getRequests());
		ps.setInt(cnt++, in.getStatus());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getEid());
		
		LogU.add(in.getDate());
		LogU.add(in.getEmployee());
		LogU.add(in.getRequests());
		LogU.add(in.getStatus());
		LogU.add(in.getIsActive());
		LogU.add(in.getEid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeerequests : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static EmployeeRequest updateData(EmployeeRequest in){
		String sql = "UPDATE employeerequests SET "
				+ "datereq=?,"
				+ "employee=?,"
				+ "requests=?,"
				+ "status=?,"
				+ "isactiverq=?,"
				+ "eid=?" 
				+ " WHERE rid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employeerequests");
		
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getEmployee());
		ps.setString(cnt++, in.getRequests());
		ps.setInt(cnt++, in.getStatus());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getEid());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDate());
		LogU.add(in.getEmployee());
		LogU.add(in.getRequests());
		LogU.add(in.getStatus());
		LogU.add(in.getIsActive());
		LogU.add(in.getEid());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeerequests : " + s.getMessage());
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
		sql="SELECT rid FROM employeerequests ORDER BY rid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT rid FROM employeerequests WHERE rid=?");
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
		String sql = "UPDATE employeerequests SET isactiverq=0 WHERE rid=?";
		
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
		String sql = "UPDATE employeerequests SET isactiverq=0 WHERE rid=" + idx;
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
