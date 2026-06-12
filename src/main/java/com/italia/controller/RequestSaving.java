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
public class RequestSaving {

	private long id;
	private String date;
	private String reason;
	private double amountReq;
	private double amountApp;
	private int status;
	private int isActive;
	private long eid;
	
	public static List<RequestSaving> retrive(String sql, String[] params){
		List<RequestSaving> reqs = new ArrayList<RequestSaving>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sqlTmp = "SELECT * FROM requestsaving WHERE isactiverq=1 ";
		
		sqlTmp = sqlTmp + sql;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqlTmp);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("requestsaving: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				RequestSaving rq = builder()
						.id(rs.getLong("rsid"))
						.date(rs.getString("datereq"))
						.reason(rs.getString("reason"))
						.amountReq(rs.getDouble("amountreq"))
						.amountApp(rs.getDouble("amountapp"))
						.status(rs.getInt("status"))
						.isActive(rs.getInt("isactiverq"))
						.eid(rs.getLong("eid"))
						.build();				
				reqs.add(rq);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return reqs;
	}
	
	public static RequestSaving save(RequestSaving st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = RequestSaving.getInfo(st.getId() ==0? RequestSaving.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = RequestSaving.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = RequestSaving.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = RequestSaving.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static RequestSaving insertData(RequestSaving in, String type){
		String sql = "INSERT INTO requestsaving ("
				+ "rsid,"
				+ "datereq,"
				+ "reason,"
				+ "amountreq,"
				+ "amountapp,"
				+ "status,"
				+ "isactiverq,"
				+ "eid) " 
				+ " Values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table requestsaving");
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
		
		
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getReason());
		ps.setDouble(cnt++, in.getAmountReq());
		ps.setDouble(cnt++, in.getAmountApp());
		ps.setInt(cnt++, in.getStatus());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getEid());
		
		LogU.add(in.getDate());
		LogU.add(in.getReason());
		LogU.add(in.getAmountReq());
		LogU.add(in.getAmountApp());
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
			LogU.add("error inserting data to requestsaving : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static RequestSaving updateData(RequestSaving in){
		String sql = "UPDATE requestsaving SET "
				+ "datereq=?,"
				+ "reason=?,"
				+ "amountreq=?,"
				+ "amountapp=?,"
				+ "status=?,"
				+ "isactiverq=?,"
				+ "eid=? " 
				+ " WHERE rsid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table requestsaving");
		
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getReason());
		ps.setDouble(cnt++, in.getAmountReq());
		ps.setDouble(cnt++, in.getAmountApp());
		ps.setInt(cnt++, in.getStatus());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getEid());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDate());
		LogU.add(in.getReason());
		LogU.add(in.getAmountReq());
		LogU.add(in.getAmountApp());
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
			LogU.add("error updating data to requestsaving : " + s.getMessage());
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
		sql="SELECT rsid FROM requestsaving ORDER BY rsid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("rsid");
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
		ps = conn.prepareStatement("SELECT rsid FROM requestsaving WHERE rsid=?");
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
		String sql = "UPDATE requestsaving SET isactiverq=0 WHERE rsid=?";
		
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
		String sql = "UPDATE requestsaving SET isactiverq=0 WHERE rsid=" + idx;
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
