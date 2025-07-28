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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EmployeePayable {

	private long id;
	private double newAddedAmount;
	private double balance;
	private double deductedAmount;
	private Employee employee;
	
	public static void rollBackAmount(double amount, int eid,boolean rollBackAdded) {
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		EmployeePayable em = null;
			try{
				conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
				ps = conn.prepareStatement("SELECT * FROM employeepayable WHERE eid=" +eid);
				
				rs = ps.executeQuery();
				System.out.println("retrieve rollback SQL: " + ps.toString());
			while(rs.next()){
				em = new EmployeePayable();
				em.setId(rs.getLong("pid"));
				em.setNewAddedAmount(rs.getDouble("newadded"));
				em.setBalance(rs.getDouble("balance"));
				em.setDeductedAmount(rs.getDouble("deducted"));
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			
			if(em!=null) {
				
				double bal = em.getBalance() + amount;
				if(rollBackAdded) {//this is for formprocess cash advance  rollback = true for deletion
					bal = em.getBalance()<=0? 0 : (em.getBalance()<amount? 0 : (em.getBalance() - amount));
					em.setNewAddedAmount(0);
				}else {//this is for payslip deletion
					em.setDeductedAmount(0);
				}
				em.setBalance(bal);
				em.setEmployee(Employee.builder().eid(eid).build());
				em.save();
			}
			
			}catch(Exception e){e.getMessage();}
	}
	
	
	public static void updateEmployeePayable(int eid, double amount, String type) {
		
		System.out.println("check amount: " + amount);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		EmployeePayable em = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT * FROM employeepayable WHERE eid=" +eid);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			em = new EmployeePayable();
			em.setId(rs.getLong("pid"));
			em.setNewAddedAmount(rs.getDouble("newadded"));
			em.setBalance(rs.getDouble("balance"));
			em.setDeductedAmount(rs.getDouble("deducted"));
			em.setEmployee(Employee.builder().eid(rs.getInt("eid")).build());
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		
		if(em==null) {
			em = new EmployeePayable();
			em.setNewAddedAmount(amount);
			em.setBalance(amount);
			em.setDeductedAmount(0);
			em.setEmployee(Employee.builder().eid(eid).build());
			em.save();
		}else {
			System.out.println("Old balance: " + em.getBalance());
			double newBal = 0d;
			//update
			if("ADD".equalsIgnoreCase(type)) {
				em.setNewAddedAmount(amount);
				newBal = em.getBalance()<=0? amount : amount + em.getBalance();
				em.setBalance(newBal);
				//em.setBalance(amount + em.getBalance()); //this prev formula change with above condition
			}else {
				em.setDeductedAmount(amount);
				newBal = em.getBalance()>amount? em.getBalance()-amount : 0;
				//prev condition em.setBalance(em.getBalance()-amount);//change with above condition
				em.setBalance(newBal);
			}
			System.out.println("new balance: " + newBal);
			
			em.save();
		}
		
		}catch(Exception e){e.getMessage();}
	}
	
	
	
	public static double getAllUnpaidPayable(long eid) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		EmployeePayable em = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT balance FROM employeepayable WHERE eid=" +eid);
		rs = ps.executeQuery();
		
		while(rs.next()){	
			return rs.getDouble("balance");
		}

		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		return 0;
	}
	
	public static List<EmployeePayable> retrieve(String sqlAdd, String[] params){
		List<EmployeePayable> vrs = new ArrayList<EmployeePayable>();
		
		String tabPy = "py";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM employeepayable "+ tabPy +",employees "+ tabEmp +"  WHERE " +
				tabPy + ".eid=" + tabEmp + ".eid ";
		
				
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
		System.out.println("employeepayable sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			EmployeePayable s = EmployeePayable.builder()
					.id(rs.getLong("pid"))
					.newAddedAmount(rs.getDouble("newadded"))
					.balance(rs.getDouble("balance"))
					.deductedAmount(rs.getDouble("deducted"))
					.build();
			
			Employee e =Employee.builder()
					.eid(rs.getInt("eid"))
					.fullname(rs.getString("fullname"))
					.build();
			s.setEmployee(e);
			
			vrs.add(s);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static EmployeePayable save(EmployeePayable vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = EmployeePayable.getInfo(vr.getId() ==0? EmployeePayable.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = EmployeePayable.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = EmployeePayable.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = EmployeePayable.insertData(vr, "3");
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
	
	public static EmployeePayable insertData(EmployeePayable vr, String type){
		String sql = "INSERT INTO employeepayable ("
				+ "pid,"
				+ "newadded,"
				+ "balance,"
				+ "deducted,"
				+ "eid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table employeepayable");
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
		
		ps.setDouble(cnt++, vr.getNewAddedAmount());
		ps.setDouble(cnt++, vr.getBalance());
		ps.setDouble(cnt++, vr.getDeductedAmount());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getEid());
		
		LogU.add(vr.getNewAddedAmount());
		LogU.add(vr.getBalance());
		LogU.add(vr.getDeductedAmount());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getEid());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeepayable : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO employeepayable ("
				+ "pid,"
				+ "newadded,"
				+ "balance,"
				+ "deducted,"
				+ "eid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table employeepayable");
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
		
		ps.setDouble(cnt++, getNewAddedAmount());
		ps.setDouble(cnt++, getBalance());
		ps.setDouble(cnt++, getDeductedAmount());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getEid());
		
		LogU.add(getNewAddedAmount());
		LogU.add(getBalance());
		LogU.add(getDeductedAmount());
		LogU.add(getEmployee()==null? 0 : getEmployee().getEid());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeepayable : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static EmployeePayable updateData(EmployeePayable vr){
		String sql = "UPDATE employeepayable SET "
				+ "newadded=?,"
				+ "balance=?,"
				+ "deducted=?,"
				+ "eid=?" 
				+ "  WHERE pid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		
		
		ps.setDouble(cnt++, vr.getNewAddedAmount());
		ps.setDouble(cnt++, vr.getBalance());
		ps.setDouble(cnt++, vr.getDeductedAmount());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getEid());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getNewAddedAmount());
		LogU.add(vr.getBalance());
		LogU.add(vr.getDeductedAmount());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getEid());
		LogU.add(vr.getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employeepayable : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void updateData(){
		String sql = "UPDATE employeepayable SET "
				+ "newadded=?,"
				+ "balance=?,"
				+ "deducted=?,"
				+ "eid=?" 
				+ "  WHERE pid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		
		
		ps.setDouble(cnt++, getNewAddedAmount());
		ps.setDouble(cnt++, getBalance());
		ps.setDouble(cnt++, getDeductedAmount());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getEid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getNewAddedAmount());
		LogU.add(getBalance());
		LogU.add(getDeductedAmount());
		LogU.add(getEmployee()==null? 0 : getEmployee().getEid());
		LogU.add(getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employeepayable : " + s.getMessage());
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
		sql="SELECT pid FROM employeepayable  ORDER BY pid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("pid");
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
		ps = conn.prepareStatement("SELECT pid FROM employeepayable WHERE pid=?");
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
		String sql = "DELETE employeepayable WHERE pid=?";
		
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
