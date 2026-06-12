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
@ToString
@Builder
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PulseReport {

	private long id;
	private String dateTrans;
	private String description;
	private String fixedby;
	private int status;
	private int isActive;
	private long eid;
	private String dateCompleted;
	private String comments;
	
	public static List<PulseReport> retrive(String sql, String[] params){
		List<PulseReport> recs = new ArrayList<PulseReport>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sqlTmp = "SELECT * FROM pulsereport WHERE isactivepu=1 ";
		
		sqlTmp = sqlTmp + sql;
		
		List<PulseReport> waiting = new ArrayList<PulseReport>();
		List<PulseReport> progress = new ArrayList<PulseReport>();
		List<PulseReport> hold = new ArrayList<PulseReport>();
		List<PulseReport> others = new ArrayList<PulseReport>();
		
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
				
				PulseReport pu = builder()
						.id(rs.getLong("pid"))
						.dateTrans(rs.getString("datepu"))
						.description(rs.getString("description"))
						.fixedby(rs.getString("fixedby"))
						.status(rs.getInt("status"))
						.isActive(rs.getInt("isactivepu"))
						.eid(rs.getLong("eid"))
						.dateCompleted(rs.getString("datecompleted"))
						.comments(rs.getString("comments"))
						.build();
				
				//recs.add(pu);
				
				if(PulseStatus.WAITING_FOR_ACTION.getId()==pu.getStatus()) {
					waiting.add(pu);
				}else if(PulseStatus.IN_PROGRESS.getId()==pu.getStatus()) {
					progress.add(pu);
				}else if(PulseStatus.ON_HOLD.getId()==pu.getStatus()) {
					hold.add(pu);
				}else {
					others.add(pu);
				}
				
				
				
			}
			
			recs.addAll(waiting);
			recs.addAll(progress);
			recs.addAll(hold);
			recs.addAll(others);
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return recs;
	}
	
	public static PulseReport save(PulseReport st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = PulseReport.getInfo(st.getId() ==0? PulseReport.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = PulseReport.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = PulseReport.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = PulseReport.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static PulseReport insertData(PulseReport in, String type){
		String sql = "INSERT INTO pulsereport ("
				+ "pid,"
				+ "datepu,"
				+ "description,"
				+ "fixedby,"
				+ "status,"
				+ "isactivepu,"
				+ "eid,"
				+ "datecompleted,"
				+ "comments)" 
				+ " Values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table pulsereport");
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
		ps.setString(cnt++, in.getDescription());
		ps.setString(cnt++, in.getFixedby());
		ps.setInt(cnt++, in.getStatus());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getEid());
		ps.setString(cnt++, in.getDateCompleted());
		ps.setString(cnt++, in.getComments());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getDescription());
		LogU.add(in.getFixedby());
		LogU.add(in.getStatus());
		LogU.add(in.getIsActive());
		LogU.add(in.getEid());
		LogU.add(in.getDateCompleted());
		LogU.add(in.getComments());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to pulsereport : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static PulseReport updateData(PulseReport in){
		String sql = "UPDATE pulsereport SET "
				+ "datepu=?,"
				+ "description=?,"
				+ "fixedby=?,"
				+ "status=?,"
				+ "isactivepu=?,"
				+ "eid=?,"
				+ "datecompleted=?,"
				+ "comments=?" 
				+ " WHERE pid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table pulsereport");
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setString(cnt++, in.getDescription());
		ps.setString(cnt++, in.getFixedby());
		ps.setInt(cnt++, in.getStatus());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getEid());
		ps.setString(cnt++, in.getDateCompleted());
		ps.setString(cnt++, in.getComments());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getDescription());
		LogU.add(in.getFixedby());
		LogU.add(in.getStatus());
		LogU.add(in.getIsActive());
		LogU.add(in.getEid());
		LogU.add(in.getDateCompleted());
		LogU.add(in.getComments());
		LogU.add(in.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to pulsereport : " + s.getMessage());
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
		sql="SELECT pid FROM pulsereport ORDER BY pid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("pid");
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
		ps = conn.prepareStatement("SELECT pid FROM pulsereport WHERE pid=?");
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
		String sql = "UPDATE pulsereport SET isactivepu=0 WHERE pid=?";
		
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
		String sql = "UPDATE pulsereport SET isactivepu=0 WHERE pid=" + idx;
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
