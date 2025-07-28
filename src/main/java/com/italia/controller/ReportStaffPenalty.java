package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public class ReportStaffPenalty {

	
	private long id;
	private String dateTrans;
	private long reportedStaffEid;
	private long reportedByEid;
	private long approveByEid;
	private String remarks;
	private int status;
	private double penaltyRate;
	private int isActive;
	
	private String statusName;
	private String reportedStaffName;
	private String reportedByName;
	private String approveByName;
	
	
	private static Map<Long, String> getAllEmployees(){
		Map<Long, String> mapData = new LinkedHashMap<Long, String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "SELECT eid,fullname FROM employees WHERE isactiveem=1";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("reportstaffpenalty PS: " + ps.toString());
			rs = ps.executeQuery();
			mapData.put(0l, "No Assigned");
			while(rs.next()){
				mapData.put(rs.getLong("eid"), rs.getString("fullname"));
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		return mapData;
	}
	
	private static String statusLabel(int id) {
		String status = "For Approval";
		if(id==0) {
			status = "Pending Approval";
		}else if(id==1) {
			status = "Approved";
		}else if(id==2) {
			status = "Denied";
		}else if(id==3) {
			status = "Cancelled";
		}
		return status;
	}
	
	public static List<ReportStaffPenalty> retrieve(String sql, String[] params){
		
		Map<Long, String> employees =  getAllEmployees();
		
		List<ReportStaffPenalty> rpts = new ArrayList<ReportStaffPenalty>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String tableRpt = "rp";
		String sqlTmp = "SELECT rp.*, e.eid,e.fullname FROM reportstaffpenalty rp, employees e WHERE rp.reid = e.eid AND " + tableRpt + ".isactivereport=1 "; 
		
		sqlTmp = sqlTmp + sql;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqlTmp);
			System.out.println("reportstaffpenalty PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				String staff = employees.get(rs.getLong("reid"));
				String reportBy = employees.get(rs.getLong("byeid"));
				String approver = "";
				
				try { approver = employees.get(rs.getLong("approveeid")); }catch(Exception e) {approver = "";}
				
				ReportStaffPenalty rpt = builder()
						.id(rs.getLong("rid"))
						.dateTrans(rs.getString("rdate"))
						.reportedStaffEid(rs.getLong("reid"))
						.reportedByEid(rs.getLong("byeid"))
						.remarks(rs.getString("remarks"))
						.status(rs.getInt("status"))
						.penaltyRate(rs.getDouble("penaltyrate"))
						.approveByEid(rs.getLong("approveeid"))
						.isActive(rs.getInt("isactivereport"))
						.statusName(statusLabel(rs.getInt("status")))
						.reportedStaffName(staff)
						.reportedByName(reportBy)
						.approveByName(approver)
						.build();
				
				rpts.add(rpt);
				
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		
		return rpts;
	}
	
	
	public static ReportStaffPenalty save(ReportStaffPenalty st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = ReportStaffPenalty.getInfo(st.getId() ==0? ReportStaffPenalty.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = ReportStaffPenalty.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = ReportStaffPenalty.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = ReportStaffPenalty.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static ReportStaffPenalty insertData(ReportStaffPenalty in, String type){
		String sql = "INSERT INTO reportstaffpenalty ("
				+ "rid,"
				+ "rdate,"
				+ "reid,"
				+ "byeid,"
				+ "remarks,"
				+ "status,"
				+ "penaltyrate,"
				+ "approveeid,"
				+ "isactivereport) " 
				+ " values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table reportstaffpenalty");
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
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setLong(cnt++, in.getReportedStaffEid());
		ps.setLong(cnt++, in.getReportedByEid());
		ps.setString(cnt++, in.getRemarks());
		ps.setInt(cnt++, in.getStatus());
		ps.setDouble(cnt++, in.getPenaltyRate());
		ps.setLong(cnt++, in.getApproveByEid());
		ps.setInt(cnt++, in.getIsActive());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getReportedStaffEid());
		LogU.add(in.getReportedByEid());
		LogU.add(in.getRemarks());
		LogU.add(in.getStatus());
		LogU.add(in.getPenaltyRate());
		LogU.add(in.getApproveByEid());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to reportstaffpenalty : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static ReportStaffPenalty updateData(ReportStaffPenalty in){
		String sql = "UPDATE reportstaffpenalty SET "
				+ "rdate=?,"
				+ "reid=?,"
				+ "byeid=?,"
				+ "remarks=?,"
				+ "status=?,"
				+ "penaltyrate=?,"
				+ "approveeid=?" 
				+ " WHERE rid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table reportstaffpenalty");
		
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setLong(cnt++, in.getReportedStaffEid());
		ps.setLong(cnt++, in.getReportedByEid());
		ps.setString(cnt++, in.getRemarks());
		ps.setInt(cnt++, in.getStatus());
		ps.setDouble(cnt++, in.getPenaltyRate());
		ps.setLong(cnt++, in.getApproveByEid());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getReportedStaffEid());
		LogU.add(in.getReportedByEid());
		LogU.add(in.getRemarks());
		LogU.add(in.getStatus());
		LogU.add(in.getPenaltyRate());
		LogU.add(in.getApproveByEid());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to reportstaffpenalty : " + s.getMessage());
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
		sql="SELECT rid FROM reportstaffpenalty ORDER BY rid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT rid FROM reportstaffpenalty WHERE rid=?");
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
		String sql = "UPDATE reportstaffpenalty SET isactivereport=0 WHERE rid=?";
		
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
		String sql = "UPDATE reportstaffpenalty SET isactivereport=0 WHERE rid=" + idx;
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
