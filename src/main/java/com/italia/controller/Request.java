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
public class Request {
	private long id;
	private String stockName;
	private String dateTrans;
	private double qty;
	private int department;
	private int stockid;
	private int status;
	private int isActive;
	private int userid;
	private double appqty;
	private String barcode;
	
	public static List<Request> getAllRequests(){
		List<Request> reqs = new ArrayList<Request>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStock="st";
		String tableReq = "rq";
		String sql = "SELECT * FROM request "+ tableReq +", stocks "+ tableStock +" WHERE "+ tableReq +".isactiver=1 AND " +
				tableReq + ".sid=" + tableStock + ".sid " +
				"ORDER BY "+ tableStock +".stocknames";
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
				
				Request req = builder()
						.id(rs.getLong("rid"))
						.dateTrans(rs.getString("datetrans"))
						.department(rs.getInt("department"))
						.userid(rs.getInt("usid"))
						.isActive(rs.getInt("isactiver"))
						.stockid(rs.getInt("sid"))
						.qty(rs.getDouble("qty"))
						.status(rs.getInt("status"))
						.stockName(rs.getString("stocknames"))
						.appqty(rs.getDouble("qtyapp"))
						.barcode(rs.getString("barcode"))
						.build();
				
				reqs.add(req);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return reqs;
	}
	
	public static List<Request> getByDepartment(int department){
		List<Request> reqs = new ArrayList<Request>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStock="st";
		String tableReq = "rq";
		String sql = "SELECT * FROM request "+ tableReq +", stocks "+ tableStock +" WHERE "+ tableReq +".isactiver=1 AND " +
				tableReq + ".sid=" + tableStock + ".sid ";
			
			if(department>1) {
				sql += " AND "+ tableReq +".department=" + department;	
			}
				
			sql +=" ORDER BY "+ tableStock +".stocknames";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			
			System.out.println("getByDepartment: " + ps.toString());
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Request req = builder()
						.id(rs.getLong("rid"))
						.dateTrans(rs.getString("datetrans"))
						.department(rs.getInt("department"))
						.userid(rs.getInt("usid"))
						.isActive(rs.getInt("isactiver"))
						.stockid(rs.getInt("sid"))
						.qty(rs.getDouble("qty"))
						.status(rs.getInt("status"))
						.stockName(rs.getString("stocknames"))
						.appqty(rs.getDouble("qtyapp"))
						.barcode(rs.getString("barcode"))
						.build();
				reqs.add(req);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return reqs;
	}
	
	public static Request getById(int id){
		Request req = new Request();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStock="st";
		String tableReq = "rq";
		String sql = "SELECT * FROM request "+ tableReq +", stocks "+ tableStock +" WHERE "+ tableReq +".isactiver=1 AND " +
				tableReq + ".sid=" + tableStock + ".sid AND " + tableReq + ".rid=" + id;
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
				
				req = builder()
						.id(rs.getLong("rid"))
						.dateTrans(rs.getString("datetrans"))
						.department(rs.getInt("department"))
						.userid(rs.getInt("usid"))
						.isActive(rs.getInt("isactiver"))
						.stockid(rs.getInt("sid"))
						.qty(rs.getDouble("qty"))
						.status(rs.getInt("status"))
						.stockName(rs.getString("stocknames"))
						.appqty(rs.getDouble("qtyapp"))
						.barcode(rs.getString("barcode"))
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return req;
	}
	
	public static Request save(Request st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Request.getInfo(st.getId() ==0? Request.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Request.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Request.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Request.insertData(st, "3");
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
			Request.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			Request.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			Request.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static Request insertData(Request st, String type){
		String sql = "INSERT INTO request ("
				+ "rid,"
				+ "datetrans,"
				+ "department,"
				+ "usid,"
				+ "isactiver,"
				+ "sid,"
				+ "qty,"
				+ "status,"
				+ "qtyapp)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table request");
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
		ps.setInt(cnt++, st.getDepartment());
		ps.setInt(cnt++, st.getUserid());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getStockid());
		ps.setDouble(cnt++, st.getQty());
		ps.setInt(cnt++, st.getStatus());
		ps.setDouble(cnt++, st.getAppqty());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getDepartment());
		LogU.add(st.getUserid());
		LogU.add(st.getIsActive());
		LogU.add(st.getStockid());
		LogU.add(st.getQty());
		LogU.add(st.getStatus());
		LogU.add(st.getAppqty());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to request : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static Request updateData(Request st){
		String sql = "UPDATE request SET "
				+ "datetrans=?,"
				+ "department=?,"
				+ "usid=?,"
				+ "sid=?,"
				+ "qty=?,"
				+ "status=?,"
				+ "qtyapp=?" 
				+ " WHERE rid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table request");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getDepartment());
		ps.setInt(cnt++, st.getUserid());
		ps.setInt(cnt++, st.getStockid());
		ps.setDouble(cnt++, st.getQty());
		ps.setInt(cnt++, st.getStatus());
		ps.setDouble(cnt++, st.getAppqty());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getDepartment());
		LogU.add(st.getUserid());
		LogU.add(st.getStockid());
		LogU.add(st.getQty());
		LogU.add(st.getStatus());
		LogU.add(st.getAppqty());
		LogU.add(st.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to request : " + s.getMessage());
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
		sql="SELECT rid FROM request ORDER BY rid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT rid FROM request WHERE rid=?");
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
		String sql = "UPDATE request SET isactiver=0 WHERE rid=?";
		
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
		String sql = "UPDATE request SET isactiver=0 WHERE rid=" + idx;
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
