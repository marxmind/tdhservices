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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 05/13/2021
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Dtr {

	private long id;
	private int line;
	private String dateTrans;
	private String dtrIn;
	private String dtrOut;
	private int type;
	private String remarks;
	private int isActive;
	private double cashAdvance;
	private int employee;
	private int isCompleted;
	private double otherAddAmount;
	
	public static List<Dtr> retrieve(String sqlAdd, String[] params){
		List<Dtr> vrs = new ArrayList<Dtr>();
		
		String tabDtr = "dt";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM dtr "+ tabDtr +",employees "+ tabEmp +"  WHERE " + tabDtr + ".isactivedtr=1 AND " +
		tabDtr + ".eid=" + tabEmp + ".eid ";
		
				
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
		System.out.println("DTR sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Dtr s = Dtr.builder()
					.id(rs.getLong("did"))
					.line(rs.getInt("dtrline"))
					.dateTrans(rs.getString("dateTrans"))
					.dtrIn(rs.getString("dtrin"))
					.dtrOut(rs.getString("dtrout"))
					.type(rs.getInt("dtrtype"))
					.remarks(rs.getString("dtrremarks"))
					.isActive(rs.getInt("isactivedtr"))
					.cashAdvance(rs.getDouble("ca"))
					.isCompleted(rs.getInt("iscompleted"))
					.otherAddAmount(rs.getDouble("otherAddAmount"))
					.employee(rs.getInt("eid"))
					.build();
			
			vrs.add(s);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static Dtr save(Dtr vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Dtr.getInfo(vr.getId() ==0? Dtr.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = Dtr.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = Dtr.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = Dtr.insertData(vr, "3");
			}
			LogU.close();
		}
		return vr;
	}
	
	
	public void save(){
		LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			LogU.close();
	}
	
	public static Dtr insertData(Dtr vr, String type){
		String sql = "INSERT INTO dtr ("
				+ "did,"
				+ "dtrline,"
				+ "dateTrans,"
				+ "dtrin,"
				+ "dtrout,"
				+ "dtrtype,"
				+ "dtrremarks,"
				+ "isactivedtr,"
				+ "eid,"
				+ "ca,"
				+ "iscompleted,"
				+ "otherAddAmount)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table dtr");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			vr.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			vr.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, vr.getLine());
		ps.setString(cnt++, vr.getDateTrans());
		ps.setString(cnt++, vr.getDtrIn());
		ps.setString(cnt++, vr.getDtrOut());
		ps.setInt(cnt++, vr.getType());
		ps.setString(cnt++, vr.getRemarks());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getEmployee());
		ps.setDouble(cnt++, vr.getCashAdvance());
		ps.setInt(cnt++, vr.getIsCompleted());
		ps.setDouble(cnt++, vr.getOtherAddAmount());
		
		LogU.add(vr.getLine());
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getDtrIn());
		LogU.add(vr.getDtrOut());
		LogU.add(vr.getType());
		LogU.add(vr.getRemarks());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEmployee());
		LogU.add(vr.getCashAdvance());
		LogU.add(vr.getIsCompleted());
		LogU.add(vr.getOtherAddAmount());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to dtr : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO dtr ("
				+ "did,"
				+ "dtrline,"
				+ "dateTrans,"
				+ "dtrin,"
				+ "dtrout,"
				+ "dtrtype,"
				+ "dtrremarks,"
				+ "isactivedtr,"
				+ "eid,"
				+ "ca,"
				+ "iscompleted,"
				+ "otherAddAmount)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table dtr");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, getLine());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getDtrIn());
		ps.setString(cnt++, getDtrOut());
		ps.setInt(cnt++, getType());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getEmployee());
		ps.setDouble(cnt++, getCashAdvance());
		ps.setDouble(cnt++, getIsCompleted());
		ps.setDouble(cnt++, getOtherAddAmount());
		
		LogU.add(getLine());
		LogU.add(getDateTrans());
		LogU.add(getDtrIn());
		LogU.add(getDtrOut());
		LogU.add(getType());
		LogU.add(getRemarks());
		LogU.add(getIsActive());
		LogU.add(getEmployee());
		LogU.add(getCashAdvance());
		LogU.add(getIsCompleted());
		LogU.add(getOtherAddAmount());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to dtr : " + s.getMessage());
		}
		LogU.close();
		
	}
	
	public static Dtr updateData(Dtr vr){
		String sql = "UPDATE dtr SET "
				+ "dtrline=?,"
				+ "dateTrans=?,"
				+ "dtrin=?,"
				+ "dtrout=?,"
				+ "dtrtype=?,"
				+ "dtrremarks=?,"
				+ "eid=?,"
				+ "ca=?,"
				+ "iscompleted=?,"
				+ "otherAddAmount=? " 
				+ " WHERE did=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		ps.setInt(cnt++, vr.getLine());
		ps.setString(cnt++, vr.getDateTrans());
		ps.setString(cnt++, vr.getDtrIn());
		ps.setString(cnt++, vr.getDtrOut());
		ps.setInt(cnt++, vr.getType());
		ps.setString(cnt++, vr.getRemarks());
		ps.setLong(cnt++, vr.getEmployee());
		ps.setDouble(cnt++, vr.getCashAdvance());
		ps.setInt(cnt++, vr.getIsCompleted());
		ps.setDouble(cnt++, vr.getOtherAddAmount());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getLine());
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getDtrIn());
		LogU.add(vr.getDtrOut());
		LogU.add(vr.getType());
		LogU.add(vr.getRemarks());
		LogU.add(vr.getEmployee());
		LogU.add(vr.getCashAdvance());
		LogU.add(vr.getIsCompleted());
		LogU.add(vr.getOtherAddAmount());
		LogU.add(vr.getId());		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to dtr : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void updateData(){
		String sql = "UPDATE dtr SET "
				+ "dtrline=?,"
				+ "dateTrans=?,"
				+ "dtrin=?,"
				+ "dtrout=?,"
				+ "dtrtype=?,"
				+ "dtrremarks=?,"
				+ "eid=?,"
				+ "ca=?,"
				+ "iscompleted=?,"
				+ "otherAddAmount=? " 
				+ " WHERE did=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		ps.setInt(cnt++, getLine());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getDtrIn());
		ps.setString(cnt++, getDtrOut());
		ps.setInt(cnt++, getType());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getEmployee());
		ps.setDouble(cnt++, getCashAdvance());
		ps.setInt(cnt++, getIsCompleted());
		ps.setDouble(cnt++, getOtherAddAmount());
		ps.setLong(cnt++, getId());
		
		LogU.add(getLine());
		LogU.add(getDateTrans());
		LogU.add(getDtrIn());
		LogU.add(getDtrOut());
		LogU.add(getType());
		LogU.add(getRemarks());
		LogU.add(getEmployee());
		LogU.add(getCashAdvance());
		LogU.add(getIsCompleted());
		LogU.add(getOtherAddAmount());
		LogU.add(getId());		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to dtr : " + s.getMessage());
		}
		LogU.close();
	
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT did FROM dtr  ORDER BY did DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("did");
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT did FROM dtr WHERE did=?");
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
		String sql = "UPDATE dtr set isactivedtr=0 WHERE did=?";
		
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