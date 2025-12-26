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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee {
	private int eid;
	private int jobid;
	private String fullname;
	private int isActive;
	private String dateReg;
	private String dateResigned;
	private String address;
	private String birthdate;
	private String contactNo;
	private double dailySalary;
	private String department;
	private String qrcode;
	private int gender;
	private int age;
	private String imgurl;
	
	
	public static Map<Integer, Employee> getMapAll(){
		Map<Integer, Employee> ems = new LinkedHashMap<Integer, Employee>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		//String sql = "SELECT * FROM employees WHERE isactiveem=1 AND dateresigned is null ORDER BY fullname";
		String sql = "SELECT * FROM employees e, department d WHERE e.isactiveem=1 AND e.departmentid=d.departmentid ORDER BY fullname";
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
				
				Employee em = builder()
						.eid(rs.getInt("eid"))
						.fullname(rs.getString("fullname"))
						.jobid(rs.getInt("jobid"))
						.dateReg(rs.getString("dateReg"))
						.dateResigned(rs.getString("dateresigned"))
						.address(rs.getString("address"))
						.birthdate(rs.getString("birthdate"))
						.contactNo(rs.getString("contactno"))
						.dailySalary(rs.getDouble("dailysalary"))
						.department(rs.getString("departmentname"))
						.qrcode(rs.getString("qrcode"))
						.isActive(rs.getInt("isactiveem"))
						.age(rs.getInt("age"))
						.gender(rs.getInt("gender"))
						.imgurl(rs.getString("imgurl"))
						.build();
			
				ems.put(em.getEid(), em);
				
				//System.out.println("id: " + em.getEid() + "\tName: " + em.getFullname() + "\tjobid: " + em.getJobid());
				
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return ems;
	}
	
	public static List<Employee> getAll(){
		List<Employee> ems = new ArrayList<Employee>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		//String sql = "SELECT * FROM employees WHERE isactiveem=1 AND dateresigned is null ORDER BY fullname";
		String sql = "SELECT * FROM employees e, department d WHERE e.isactiveem=1 AND e.departmentid=d.departmentid ORDER BY fullname";
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
				
				Employee em = builder()
						.eid(rs.getInt("eid"))
						.fullname(rs.getString("fullname"))
						.jobid(rs.getInt("jobid"))
						.dateReg(rs.getString("dateReg"))
						.dateResigned(rs.getString("dateresigned"))
						.address(rs.getString("address"))
						.birthdate(rs.getString("birthdate"))
						.contactNo(rs.getString("contactno"))
						.dailySalary(rs.getDouble("dailysalary"))
						.department(rs.getString("departmentname"))
						.qrcode(rs.getString("qrcode"))
						.isActive(rs.getInt("isactiveem"))
						.age(rs.getInt("age"))
						.gender(rs.getInt("gender"))
						.imgurl(rs.getString("imgurl"))
						.build();
				ems.add(em);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return ems;
	}
	
	public static boolean isEmployeePresent(int eid){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT dateresigned FROM employees WHERE isactiveem=1 AND eid=" + eid;
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
				if(rs.getString("dateresigned")==null) {
					return true;
				}
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static int getEmployeeDepartment(int eid){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT departmentid FROM employees WHERE isactiveem=1 AND eid=" + eid;
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
				return rs.getInt("departmentid");
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static String getEmployeeName(int eid){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT fullname FROM employees WHERE dateresigned is NULL AND  isactiveem=1 AND eid=" + eid;
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
				return rs.getString("fullname");
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return null;
	}
	
	
	public static Employee  getEmployeePasscode(String val){
		Employee em = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		//String sql = "SELECT * FROM employees WHERE dateresigned is NULL AND  isactiveem=1 AND eid=" + eid;
		String sql = "SELECT * FROM employees e, department d WHERE e.isactiveem=1 AND e.departmentid=d.departmentid AND e.dateresigned is NULL  AND ( e.eid=" + Integer.valueOf(val) + " OR e.qrcode='"+ val +"' )";
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
				 em = builder()
						.eid(rs.getInt("eid"))
						.fullname(rs.getString("fullname"))
						.jobid(rs.getInt("jobid"))
						.dateReg(rs.getString("dateReg"))
						.dateResigned(rs.getString("dateresigned"))
						.address(rs.getString("address"))
						.birthdate(rs.getString("birthdate"))
						.contactNo(rs.getString("contactno"))
						.dailySalary(rs.getDouble("dailysalary"))
						.department(rs.getString("departmentname"))
						.qrcode(rs.getString("qrcode"))
						.isActive(rs.getInt("isactiveem"))
						.age(rs.getInt("age"))
						.gender(rs.getInt("gender"))
						.imgurl(rs.getString("imgurl"))
						.build();
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return em;
	}
	
	@Deprecated
	public static Employee  getEmployee(int eid){
		Employee em = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		//String sql = "SELECT * FROM employees WHERE dateresigned is NULL AND  isactiveem=1 AND eid=" + eid;
		String sql = "SELECT * FROM employees e, department d WHERE e.isactiveem=1 AND e.departmentid=d.departmentid AND e.dateresigned is NULL  AND e.eid=" + eid;
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
				 em = builder()
						.eid(rs.getInt("eid"))
						.fullname(rs.getString("fullname"))
						.jobid(rs.getInt("jobid"))
						.dateReg(rs.getString("dateReg"))
						.dateResigned(rs.getString("dateresigned"))
						.address(rs.getString("address"))
						.birthdate(rs.getString("birthdate"))
						.contactNo(rs.getString("contactno"))
						.dailySalary(rs.getDouble("dailysalary"))
						.department(rs.getString("departmentname"))
						.qrcode(rs.getString("qrcode"))
						.isActive(rs.getInt("isactiveem"))
						.age(rs.getInt("age"))
						.gender(rs.getInt("gender"))
						.imgurl(rs.getString("imgurl"))
						.build();
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return em;
	}
	
	public static Employee save(Employee st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Employee.getInfo(st.getEid() ==0? Employee.getLatestId()+1 : st.getEid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Employee.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Employee.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Employee.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static Employee insertData(Employee in, String type){
		
		int depId = getDepartmentId(in.getDepartment());
	
		String sql = "INSERT INTO employees ("
				+ "eid,"
				+ "dateReg,"
				+ "dateresigned,"
				+ "fullname,"
				+ "address,"
				+ "age,"
				+ "gender,"
				+ "contactno,"
				+ "isactiveem,"
				+ "dailysalary,"
				+ "jobid,"
				+ "birthdate,"
				+ "departmentid"
				+ "qrcode,"
				+ "imgurl) " 
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employees");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			in.setEid(Integer.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			in.setEid(Integer.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, in.getDateReg());
		ps.setString(cnt++, in.getDateResigned());
		ps.setString(cnt++, in.getFullname());
		ps.setString(cnt++, in.getAddress());
		ps.setInt(cnt++, in.getAge());
		ps.setInt(cnt++, in.getGender());
		ps.setString(cnt++, in.getContactNo());
		ps.setInt(cnt++, in.getIsActive());
		ps.setDouble(cnt++, in.getDailySalary());
		ps.setInt(cnt++, in.getJobid());
		ps.setString(cnt++, in.getBirthdate());
		ps.setInt(cnt++, depId);
		ps.setString(cnt++, in.getQrcode());
		ps.setString(cnt++, in.getImgurl());
		
		
		LogU.add(in.getDateReg());
		LogU.add(in.getDateResigned());
		LogU.add(in.getFullname());
		LogU.add(in.getAddress());
		LogU.add(in.getAge());
		LogU.add(in.getGender());
		LogU.add(in.getContactNo());
		LogU.add(in.getIsActive());
		LogU.add(in.getDailySalary());
		LogU.add(in.getJobid());
		LogU.add(in.getBirthdate());
		LogU.add(in.getDepartment());
		LogU.add(in.getQrcode());
		LogU.add(in.getImgurl());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employees : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static Employee updateData(Employee in){
		
		int depId = getDepartmentId(in.getDepartment());
		
		String sql = "UPDATE employees SET "
				+ "dateReg=?,"
				+ "dateresigned=?,"
				+ "fullname=?,"
				+ "address=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "contactno=?,"
				+ "isactiveem=?,"
				+ "dailysalary=?,"
				+ "jobid=?,"
				+ "birthdate=?,"
				+ "departmentid=?"
				+ "qrcode=?,"
				+ "imgurl=? " 
				+ " WHERE eid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employees");
		
		ps.setString(cnt++, in.getDateReg());
		ps.setString(cnt++, in.getDateResigned());
		ps.setString(cnt++, in.getFullname());
		ps.setString(cnt++, in.getAddress());
		ps.setInt(cnt++, in.getAge());
		ps.setInt(cnt++, in.getGender());
		ps.setString(cnt++, in.getContactNo());
		ps.setInt(cnt++, in.getIsActive());
		ps.setDouble(cnt++, in.getDailySalary());
		ps.setInt(cnt++, in.getJobid());
		ps.setString(cnt++, in.getBirthdate());
		ps.setInt(cnt++, depId);
		ps.setString(cnt++, in.getQrcode());
		ps.setString(cnt++, in.getImgurl());
		ps.setLong(cnt++, in.getEid());
		
		
		LogU.add(in.getDateReg());
		LogU.add(in.getDateResigned());
		LogU.add(in.getFullname());
		LogU.add(in.getAddress());
		LogU.add(in.getAge());
		LogU.add(in.getGender());
		LogU.add(in.getContactNo());
		LogU.add(in.getIsActive());
		LogU.add(in.getDailySalary());
		LogU.add(in.getJobid());
		LogU.add(in.getBirthdate());
		LogU.add(in.getDepartment());
		LogU.add(in.getQrcode());
		LogU.add(in.getImgurl());
		LogU.add(in.getEid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employees : " + s.getMessage());
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
		sql="SELECT eid FROM employees ORDER BY eid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("eid");
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
		ps = conn.prepareStatement("SELECT eid FROM employees WHERE eid=?");
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
		String sql = "UPDATE employees SET isactiveem=0 WHERE eid=?";
		
		String[] params = new String[1];
		params[0] = getEid()+"";
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
		String sql = "UPDATE employees SET isactivereem=0 WHERE eid=" + idx;
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
	
private static int getDepartmentId(String dep) {
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	String[] params = new String[0];
	String sql = "SELECT departmentid FROM department WHERE departmentname='"+ dep +"'";
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
			return rs.getInt("departmentid");
		}
	
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
	return 0;
}
	
	
}
