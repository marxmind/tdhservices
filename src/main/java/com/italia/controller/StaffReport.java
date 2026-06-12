package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
public class StaffReport {

	private long id;
	private String dateTrans;
	private long eid;
	private int isActive;
	private int departmentId;
	
	private String departmentName;
	private String employee;
	
	private List<StaffReportTrans> trans;
	
	public static List<StaffReport> retrieve(String sql, String[] params){
		List<StaffReport> staffs = new ArrayList<StaffReport>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		//String sqlTmp = "SELECT * FROM staffreport s, employees e, department d WHERE s.isactives=1 AND s.eid=e.eid AND s.departmentid=d.departmentid";
		
		String sqlTmp = "SELECT " + 
			    "sr.sid, " +
			    "sr.datetrans, " +
			    "sr.eid, " +
			    "sr.isactives, " +
			    "sr.departmentid, " +
			    "srt.stid," +
			    "srt.timetrans, " +
			    "srt.descriptions, " +
			    "srt.isactivets, " +
			    "e.fullname, " +
			    "d.departmentname, " +
			    
			    "srt.isactivets " +
			"FROM " +
			    "staffreporttrans srt "+
			"INNER JOIN  "+
			    "staffreport sr ON srt.sid = sr.sid AND sr.isactives=1 "+
			"INNER JOIN  "+
			    "employees e ON srt.eid = e.eid "+
			"LEFT JOIN  "+
			    "department d ON sr.departmentid = d.departmentid "+
			"WHERE  "+
			    "srt.isactivets = 1 ";
		
		
		
		sqlTmp = sqlTmp + sql;
		
		Map<Long, List<StaffReport>> staffData = new LinkedHashMap<Long, List<StaffReport>>();
		List<StaffReport> rpts = new ArrayList<StaffReport>();		
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqlTmp);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("pulsereport: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
			
				StaffReportTrans trans = StaffReportTrans.builder()
						.id(rs.getLong("stid"))
						.timeTrans(rs.getString("timetrans"))
						.descriptions(rs.getString("descriptions"))
						.eid(rs.getLong("eid"))
						.sid(rs.getLong("sid"))
						.isActive(rs.getInt("isactivets"))
						.build();
				
				List<StaffReportTrans> transList = new ArrayList<StaffReportTrans>();
				transList.add(trans);
				
				StaffReport staff = StaffReport.builder()
						.id(rs.getLong("sid"))
						.dateTrans(rs.getString("datetrans"))
						.eid(rs.getLong("eid"))
						.isActive(rs.getInt("isactives"))
						.departmentId(rs.getInt("departmentid"))
						.employee(rs.getString("fullname"))
						.departmentName(rs.getString("departmentname"))
						.trans(transList)
						.build();
				
				long id = staff.getId();
				
				if(staffData!=null) {
					if(staffData.containsKey(id)) {
						staffData.get(id).add(staff);
					}else {
						rpts = new ArrayList<StaffReport>();	
						rpts.add(staff);
						staffData.put(id, rpts);
					}
				}else {
					rpts.add(staff);
					staffData.put(id, rpts);
				}
				
				
			}
			
			TreeMap<Long, List<StaffReport>> sorted = new TreeMap<Long, List<StaffReport>>(staffData);
			
			if(staffData!=null) {
				
				for(long id : sorted.keySet()) {
					StaffReport s = new StaffReport();
					List<StaffReportTrans> tmplist = new ArrayList<StaffReportTrans>();
					for(StaffReport st : sorted.get(id)) {
						s = st;
						tmplist.addAll(st.getTrans());
					}
					s.setTrans(tmplist);
					
					staffs.add(s);
				}
				
				
			}
			
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return staffs;
	}
	
	public static StaffReport save(StaffReport st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = StaffReport.getInfo(st.getId() ==0? StaffReport.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = StaffReport.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = StaffReport.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = StaffReport.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static StaffReport insertData(StaffReport in, String type){
		String sql = "INSERT INTO staffreport ("
				+ "sid,"
				+ "datetrans,"
				+ "eid,"
				+ "isactives,"
				+ "departmentid)" 
				+ " Values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table staffreport");
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
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setLong(cnt++, in.getEid());
		ps.setInt(cnt++, in.getIsActive());
		ps.setInt(cnt++, in.getDepartmentId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getEid());
		LogU.add(in.getIsActive());
		LogU.add(in.getDepartmentId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to staffreport : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static StaffReport updateData(StaffReport in){
		String sql = "UPDATE staffreport SET "
				+ "datetrans=?,"
				+ "eid=?,"
				+ "isactives=?,"
				+ "departmentid=?" 
				+ " where sid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("UPDATING data into table staffreport");
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setLong(cnt++, in.getEid());
		ps.setInt(cnt++, in.getIsActive());
		ps.setInt(cnt++, in.getDepartmentId());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getEid());
		LogU.add(in.getIsActive());
		LogU.add(in.getDepartmentId());
		LogU.add(in.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error UPDATING data to staffreport : " + s.getMessage());
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
		sql="SELECT sid FROM staffreport ORDER BY sid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("sid");
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
		ps = conn.prepareStatement("SELECT sid FROM staffreport WHERE sid=?");
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
		String sql = "UPDATE staffreport SET isactives=0 WHERE sid=?";
		
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
		String sql = "UPDATE staffreport SET isactives=0 WHERE sid=" + idx;
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
