package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.FormStatus;
import com.italia.enm.FormType;
import com.italia.enm.LeaveFormType;
import com.italia.enm.LeaveStatus;
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
public class LeaveForm {

	private long id;
	private String fileDate;
	private long employeeId;
	private int type;
	private String purpose;
	private String reliever;
	private int days;
	private String startDate;
	private String endDate;
	private int status;
	private String notes;
	private int isActive;
	private String employee;
	private String statusName;
	private String typeName;
	
	/**
	 * 
	 * Status : 0:For Approval 1:Approved 2: Denied 3: Hold 4: Cancelled
	 */
	
	public static List<LeaveForm> latestAll(){
		String sql = " ORDER BY lv.lid DESC LIMIT 100";
		return retrieve(sql, new String[0]);
	}
	
	public static List<LeaveForm> latestLeave(LeaveStatus leave){
		String sql = " AND lv.status=" + leave.getId();
		return retrieve(sql, new String[0]);
	}
	
	public static List<LeaveForm> searchId(long id){
		String sql = " AND lv.eid=" + id + " ORDER BY lv.lid DESC LIMIT 100";
		return retrieve(sql, new String[0]);
	}
	
	
	public static List<LeaveForm> retrieve(String sqlAdd, String[] params){
		List<LeaveForm> vrs = new ArrayList<LeaveForm>();
		
		String tabLv = "lv";
		String tabEm = "em";
		
		String sql = "SELECT * FROM leaverequest "+ tabLv +", employees "+ tabEm +"  WHERE " + tabLv + ".isactive=1  AND "
				+ tabLv +".eid=" + tabEm + ".eid ";
		
		
				
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
		System.out.println("Leave form sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			LeaveForm s = LeaveForm.builder()
					.id(rs.getLong("lid"))
					.fileDate(rs.getString("fileddate"))
					.employeeId(rs.getLong("eid"))
					.purpose(rs.getString("purpose"))
					.type(rs.getInt("leavetype"))
					.reliever(rs.getString("reliever"))
					.days(rs.getInt("numdays"))
					.startDate(rs.getString("startdate"))
					.endDate(rs.getString("enddate"))
					.status(rs.getInt("status"))
					.notes(rs.getString("notes"))
					.isActive(rs.getInt("isactive"))
					.employee(rs.getString("fullname"))
					.statusName(LeaveStatus.containId(rs.getInt("status")).getName())
					.typeName(LeaveFormType.containId(rs.getInt("leavetype")).getName())
					.build();
			
			vrs.add(s);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static LeaveForm save(LeaveForm in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = LeaveForm.getInfo(in.getId()==0? LeaveForm.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = LeaveForm.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = LeaveForm.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = LeaveForm.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		LeaveForm.save(this);
	}
	
	public static LeaveForm insertData(LeaveForm in, String type){
		String sql = "INSERT INTO leaverequest ("
				+ "lid,"
				+ "fileddate,"
				+ "eid,"
				+ "leavetype,"
				+ "purpose,"
				+ "reliever,"
				+ "numdays,"
				+ "startdate,"
				+ "enddate,"
				+ "status,"
				+ "notes,"
				+ "isactive) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table leaverequest");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			in.setId(Integer.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			in.setId(Integer.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		
		
		ps.setString(cnt++, in.getFileDate());
		ps.setLong(cnt++, in.getEmployeeId());
		ps.setInt(cnt++, in.getType());
		ps.setString(cnt++, in.getPurpose());
		ps.setString(cnt++, in.getReliever());
		ps.setInt(cnt++, in.getDays());
		ps.setString(cnt++, in.getStartDate());
		ps.setString(cnt++, in.getEndDate());
		ps.setInt(cnt++, in.getStatus());
		ps.setString(cnt++, in.getNotes());
		ps.setInt(cnt++, in.getIsActive());
		
		LogU.add(in.getFileDate());
		LogU.add(in.getEmployeeId());
		LogU.add(in.getType());
		LogU.add(in.getPurpose());
		LogU.add(in.getReliever());
		LogU.add(in.getDays());
		LogU.add(in.getStartDate());
		LogU.add(in.getEndDate());
		LogU.add(in.getStatus());
		LogU.add(in.getNotes());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to leaverequest : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static LeaveForm updateData(LeaveForm in){
		String sql = "UPDATE leaverequest SET "
				+ "fileddate=?,"
				+ "eid=?,"
				+ "leavetype=?,"
				+ "purpose=?,"
				+ "reliever=?,"
				+ "numdays=?,"
				+ "startdate=?,"
				+ "enddate=?,"
				+ "status=?,"
				+ "notes=?" 
				+ " WHERE lid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table leaverequest");
		
		
		ps.setString(cnt++, in.getFileDate());
		ps.setLong(cnt++, in.getEmployeeId());
		ps.setInt(cnt++, in.getType());
		ps.setString(cnt++, in.getPurpose());
		ps.setString(cnt++, in.getReliever());
		ps.setInt(cnt++, in.getDays());
		ps.setString(cnt++, in.getStartDate());
		ps.setString(cnt++, in.getEndDate());
		ps.setInt(cnt++, in.getStatus());
		ps.setString(cnt++, in.getNotes());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getFileDate());
		LogU.add(in.getEmployeeId());
		LogU.add(in.getType());
		LogU.add(in.getPurpose());
		LogU.add(in.getReliever());
		LogU.add(in.getDays());
		LogU.add(in.getStartDate());
		LogU.add(in.getEndDate());
		LogU.add(in.getStatus());
		LogU.add(in.getNotes());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to leaverequest : " + s.getMessage());
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
		sql="SELECT lid FROM leaverequest  ORDER BY lid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("lid");
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
		int result =0;
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
		ps = conn.prepareStatement("SELECT lid FROM leaverequest WHERE lid=?");
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE leaverequest set isactive=0 WHERE lid=?";
		
		if(!retain){
			sql = "DELETE FROM leaverequest WHERE lid=?";
		}
		
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
