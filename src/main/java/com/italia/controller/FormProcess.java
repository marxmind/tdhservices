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
public class FormProcess {

	private int id;
	private String dateTrans;
	private double amount;
	private String purpose;
	private String employee;
	private int eid;
	private String statusName;
	
	public static List<FormProcess> getUnprocessedForms(){
		List<FormProcess> forms = new ArrayList<FormProcess>();
		
		String sql = "SELECT ff.formid,ff.datetrans,ff.formstatus,ff.amount,ff.purpose,ee.eid, ee.fullname FROM formprocess ff, employees ee WHERE ff.isactiveform=1 AND ff.formstatus=" + FormStatus.FOR_APPROVAL.getId() +
				" AND ff.eid=ee.eid ";
		
				Connection conn = null;
				ResultSet rs = null;
				PreparedStatement ps = null;
				try{
				conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
				ps = conn.prepareStatement(sql);
				
				
				System.out.println("form process sql: " + ps.toString());
				rs = ps.executeQuery();
				
				while(rs.next()){
					FormProcess form = builder()
							.id(rs.getInt("formid"))
							.dateTrans(rs.getString("datetrans"))
							.eid(rs.getInt("eid"))
							.employee(rs.getString("fullname"))
							.amount(rs.getDouble("amount"))
							.purpose(rs.getString("purpose"))
							.statusName(FormStatus.containId(rs.getInt("formstatus")).getName())
							.build();
					
					forms.add(form);
				}
				
				rs.close();
				ps.close();
				DBConnect.close(conn);
				}catch(Exception e){e.getMessage();}
		
		return forms;
	}
	
	public static List<FormProcess> getEmployeeForms(int eid){
		List<FormProcess> forms = new ArrayList<FormProcess>();
		
		String sql = "SELECT ff.formid,ff.datetrans,ff.formstatus,ff.amount,ff.purpose,ee.eid, ee.fullname FROM formprocess ff, employees ee WHERE ff.isactiveform=1 " +
				" AND ff.eid=ee.eid AND ff.eid=" + eid + " ORDER BY ff.datetrans DESC LIMIT 20";
		
				Connection conn = null;
				ResultSet rs = null;
				PreparedStatement ps = null;
				try{
				conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
				ps = conn.prepareStatement(sql);
				
				
				System.out.println("form process sql: " + ps.toString());
				rs = ps.executeQuery();
				
				while(rs.next()){
					FormProcess form = builder()
							.id(rs.getInt("formid"))
							.dateTrans(rs.getString("datetrans"))
							.eid(rs.getInt("eid"))
							.employee(rs.getString("fullname"))
							.amount(rs.getDouble("amount"))
							.statusName(FormStatus.containId(rs.getInt("formstatus")).getName())
							.purpose(rs.getString("purpose"))
							.build();
					
					forms.add(form);
				}
				
				rs.close();
				ps.close();
				DBConnect.close(conn);
				}catch(Exception e){e.getMessage();}
		
		return forms;
	}
	
	public static List<FormProcess> getEmployeeByName(String name){
		List<FormProcess> forms = new ArrayList<FormProcess>();
		
		String sql = "SELECT ff.formid,ff.datetrans,ff.formstatus,ff.amount,ff.purpose,ee.eid, ee.fullname FROM formprocess ff, employees ee WHERE ff.isactiveform=1 " +
				" AND ff.eid=ee.eid AND ee.fullname like '%" + name + "%' ORDER BY ff.datetrans DESC LIMIT 20";
		
				Connection conn = null;
				ResultSet rs = null;
				PreparedStatement ps = null;
				try{
				conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
				ps = conn.prepareStatement(sql);
				
				
				System.out.println("form process sql: " + ps.toString());
				rs = ps.executeQuery();
				
				while(rs.next()){
					FormProcess form = builder()
							.id(rs.getInt("formid"))
							.dateTrans(rs.getString("datetrans"))
							.eid(rs.getInt("eid"))
							.employee(rs.getString("fullname"))
							.amount(rs.getDouble("amount"))
							.statusName(FormStatus.containId(rs.getInt("formstatus")).getName())
							.purpose(rs.getString("purpose"))
							.build();
					
					forms.add(form);
				}
				
				rs.close();
				ps.close();
				DBConnect.close(conn);
				}catch(Exception e){e.getMessage();}
		
		return forms;
	}
	
	
	public static FormProcess save(FormProcess in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = FormProcess.getInfo(in.getId()==0? FormProcess.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = FormProcess.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = FormProcess.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = FormProcess.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		FormProcess.save(this);
	}
	
	public static FormProcess insertData(FormProcess in, String type){
		String sql = "INSERT INTO formprocess ("
				+ "formid,"
				+ "datetrans,"
				+ "formtype,"
				+ "amount,"
				+ "purpose,"
				+ "isactiveform,"
				+ "isfinanceapproved,"
				+ "ismanagerapproved,"
				+ "isadminapproved,"
				+ "eid,"
				+ "departmentid,"
				+ "userdtlsid,"
				+ "formstatus) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table formprocess");
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
		
		int department = Employee.getEmployeeDepartment(in.getEid());
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setInt(cnt++, FormType.CASH_ADVANCE.getId());
		ps.setDouble(cnt++, in.getAmount());
		ps.setString(cnt++, in.getPurpose());
		ps.setInt(cnt++, 1);
		ps.setInt(cnt++, 0);
		ps.setInt(cnt++, 0);
		ps.setInt(cnt++, 0);
		ps.setLong(cnt++, in.getEid());
		ps.setInt(cnt++, department);
		ps.setLong(cnt++, 1);
		ps.setInt(cnt++, FormStatus.FOR_APPROVAL.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(FormType.CASH_ADVANCE.getId());
		LogU.add(in.getAmount());
		LogU.add(in.getPurpose());
		LogU.add(1);
		LogU.add(0);
		LogU.add(0);
		LogU.add(0);
		LogU.add(in.getEid());
		LogU.add(department);
		LogU.add(1);
		LogU.add(FormStatus.FOR_APPROVAL.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to formprocess : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	
	public static FormProcess updateData(FormProcess in){
		String sql = "UPDATE formprocess SET "
				+ "datetrans=?,"
				+ "formtype=?,"
				+ "amount=?,"
				+ "purpose=?,"
				+ "isfinanceapproved=?,"
				+ "ismanagerapproved=?,"
				+ "isadminapproved=?,"
				+ "eid=?,"
				+ "departmentid=?,"
				+ "userdtlsid=?,"
				+ "formstatus=?" 
				+ " WHERE formid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table formprocess");
		
		int department = Employee.getEmployeeDepartment(in.getEid());
		int status = FormStatus.FOR_APPROVAL.getId();
		if("Denied".equalsIgnoreCase(in.getStatusName())) {
			status = FormStatus.DENIED.getId();
		}
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setInt(cnt++, FormType.CASH_ADVANCE.getId());
		ps.setDouble(cnt++, in.getAmount());
		ps.setString(cnt++, in.getPurpose());
		if("Finance Approved".equalsIgnoreCase(in.getStatusName())) {
			ps.setInt(cnt++, 1);
			status = FormStatus.APPROVED_BY_FINANCE.getId();
		}else {ps.setInt(cnt++, 0);}
		ps.setInt(cnt++, 0);
		if("Admin Approved".equalsIgnoreCase(in.getStatusName())) {
			ps.setInt(cnt++, 1);
			status = FormStatus.APPROVED_BY_ADMIN_OFFICER.getId();
		}else {ps.setInt(cnt++, 0);}
		ps.setLong(cnt++, in.getEid());
		ps.setInt(cnt++, department);
		ps.setLong(cnt++, 1);
		ps.setInt(cnt++, status);
		ps.setInt(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(FormType.CASH_ADVANCE.getId());
		LogU.add(in.getAmount());
		LogU.add(in.getPurpose());
		LogU.add(0);
		LogU.add(0);
		LogU.add(0);
		LogU.add(in.getEid());
		LogU.add(department);
		LogU.add(1);
		LogU.add(FormStatus.FOR_APPROVAL.getId());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to formprocess : " + s.getMessage());
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
		sql="SELECT formid FROM formprocess  ORDER BY formid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("formid");
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static int getInfo(int id){
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT formid FROM formprocess WHERE formid=?");
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
		String sql = "UPDATE formprocess set isactiveform=0 WHERE formid=?";
		
		if(!retain){
			sql = "DELETE FROM formprocess WHERE formid=?";
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
