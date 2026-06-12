package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.PulseStatus;
import com.italia.utils.GlobalVar;
import com.italia.utils.LogU;

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
public class EmployeeIssuedTools {

	
	private long id;
	private String dateIssued;
	private String name;
	private String serialNumber;
	private double lostAmount;
	private boolean isActive;
	private boolean isDamaged;
	private String remarks;
	private long eid;
	
	public static List<EmployeeIssuedTools> retrive(String sql, String[] params){
		List<EmployeeIssuedTools> recs = new ArrayList<EmployeeIssuedTools>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sqlTmp = "SELECT * FROM employeeissuedtools WHERE isActive=1 ";
		
		sqlTmp = sqlTmp + sql;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqlTmp);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("employeeissuedtools: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				EmployeeIssuedTools tool = builder()
						.id(rs.getLong("etid"))
						.dateIssued(rs.getString("dateissued"))
						.name(rs.getString("name"))
						.serialNumber(rs.getString("serialnumber"))
						.lostAmount(rs.getDouble("lostamount"))
						.remarks(rs.getString("remarks"))
						.isActive(rs.getInt("isActive")==0? false : true)
						.isDamaged(rs.getInt("isDamaged")==0? false : true)
						.eid(rs.getLong("eid"))
						.build();
				recs.add(tool);
				
			}
			
			
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return recs;
	}
	
	public static EmployeeIssuedTools save(EmployeeIssuedTools st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = EmployeeIssuedTools.getInfo(st.getId() ==0? EmployeeIssuedTools.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = EmployeeIssuedTools.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = EmployeeIssuedTools.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = EmployeeIssuedTools.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static EmployeeIssuedTools insertData(EmployeeIssuedTools in, String type){
		String sql = "INSERT INTO employeeissuedtools ("
				+ "etid,"
				+ "dateissued,"
				+ "name,"
				+ "serialnumber,"
				+ "lostamount,"
				+ "remarks,"
				+ "isActive,"
				+ "isDamaged,"
				+ "eid)" 
				+ " Values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employeeissuedtools");
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
		
		ps.setString(cnt++, in.getDateIssued());
		ps.setString(cnt++, in.getName());
		ps.setString(cnt++, in.getSerialNumber());
		ps.setDouble(cnt++, in.getLostAmount());
		ps.setString(cnt++, in.getRemarks());
		ps.setInt(cnt++, in.isActive()? 1 : 0);
		ps.setInt(cnt++, in.isDamaged()? 1 : 0);
		ps.setLong(cnt++, in.getEid());
		
		LogU.add(in.getDateIssued());
		LogU.add(in.getName());
		LogU.add(in.getSerialNumber());
		LogU.add(in.getLostAmount());
		LogU.add(in.getRemarks());
		LogU.add(in.isActive()? 1 : 0);
		LogU.add(in.isDamaged()? 1 : 0);
		LogU.add(in.getEid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeeissuedtools : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static EmployeeIssuedTools updateData(EmployeeIssuedTools in){
		String sql = "UPDATE employeeissuedtools SET "
				+ "dateissued=?,"
				+ "name=?,"
				+ "serialnumber=?,"
				+ "lostamount=?,"
				+ "remarks=?,"
				+ "isActive=?,"
				+ "isDamaged=?,"
				+ "eid=?" 
				+ " WHERE etid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employeeissuedtools");
		
		
		ps.setString(cnt++, in.getDateIssued());
		ps.setString(cnt++, in.getName());
		ps.setString(cnt++, in.getSerialNumber());
		ps.setDouble(cnt++, in.getLostAmount());
		ps.setString(cnt++, in.getRemarks());
		ps.setInt(cnt++, in.isActive()? 1 : 0);
		ps.setInt(cnt++, in.isDamaged()? 1 : 0);
		ps.setLong(cnt++, in.getEid());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateIssued());
		LogU.add(in.getName());
		LogU.add(in.getSerialNumber());
		LogU.add(in.getLostAmount());
		LogU.add(in.getRemarks());
		LogU.add(in.isActive()? 1 : 0);
		LogU.add(in.isDamaged()? 1 : 0);
		LogU.add(in.getEid());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employeeissuedtools : " + s.getMessage());
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
		sql="SELECT etid FROM employeeissuedtools ORDER BY etid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("etid");
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
		ps = conn.prepareStatement("SELECT etid FROM employeeissuedtools WHERE etid=?");
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
		String sql = "UPDATE employeeissuedtools SET isActive=0 WHERE etid=?";
		
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
		String sql = "UPDATE employeeissuedtools SET isActive=0 WHERE etid=" + idx;
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
