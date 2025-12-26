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
import com.italia.enm.SavingType;
import com.italia.utils.Currency;
import com.italia.utils.DateUtils;
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
public class EmployeeSaving {

	private long id;
	private String dateTrans;
	private int saveType;
	private double amount;
	private int isActive;
	private long paySlipId;
	private String remarks;
	private Employee employee;
	private int withdraw;
	private String saveTypeName;
	
	public static Map<String, Map<String, Double>> getAllEmployee13thMonthIncentives(int year){
		Map<String, Map<String, Double>> data1 = new LinkedHashMap<String, Map<String,Double>>();
		Map<String, Double> data2 = new LinkedHashMap<String,Double>();
		String tabDtr = "siv";
		String tabEmp = "emp";
		
		String sql = "SELECT emp.fullname,siv.remarks,siv.amount FROM employeesaving "+ tabDtr +",employees "+ tabEmp +"  WHERE " + tabDtr + ".isactives=1 AND " +
		tabDtr + ".eid=" + tabEmp + ".eid  AND "+ tabDtr +".savetype=" + SavingType.MONTHLY_THIRTEEN_MONTH_DEDUCTION.getId() + " AND year(datetrans)=" + year;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("getAllEmployee13thMonthIncentives: " + ps.toString());
		rs = ps.executeQuery();
		String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
		while(rs.next()){
			String month = rs.getString("remarks");
			String monthName = "January";
			for(String m : months) {
				if(month.contains(m)) {
					monthName = m;
					break;
				}
			}
			
			String fullname = rs.getString("fullname");
			double amount = rs.getDouble("amount");
			
			if(data1!=null && data1.size()>0) {
				if(data1.containsKey(fullname)) {
					if(data1.get(fullname).containsKey(monthName)) {
						data1.get(fullname).put(monthName, amount);//override
					}else {
						data1.get(fullname).put(monthName, amount);
					}
				}else {
					data2 = new LinkedHashMap<String,Double>();
					data2.put(monthName, amount);
					data1.put(fullname, data2);
				}
			}else {
				data2.put(monthName, amount);
				data1.put(fullname, data2);
			}
			
		}
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return data1;
	}
	
	
	public static List<EmployeeSaving> retrieveEmployeeSavingType(long eid, int type){
		List<EmployeeSaving> vrs = new ArrayList<EmployeeSaving>();
		
		//String sql = "SELECT * FROM employeesaving   WHERE isactives=1 AND iswithdraw=0 AND eid=" + eid + " AND savetype=" + type;
		
		String tabDtr = "siv";
		String tabEmp = "emp";
		
		
		String sql = "SELECT * FROM employeesaving "+ tabDtr +",employees "+ tabEmp +"  WHERE " + tabDtr + ".isactives=1 AND " +
		tabDtr + ".eid=" + tabEmp + ".eid  AND "+ tabDtr +".iswithdraw=0 AND "+ tabDtr +".eid=" + eid;
		
		if(type==4) {//both direct and deducted from salary
			sql += " AND ("+ tabDtr +".savetype=" + SavingType.DEDUCTED_FROM_SALARY.getId() + " OR "+ tabDtr +".savetype="+ SavingType.DIRECT_DEDUCTION.getId() +" )";
		}else if(type<=3) {
			sql += " AND "+ tabDtr +".savetype=" + type;
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("load employee saving sql: " + ps.toString());
		rs = ps.executeQuery();
		String contactNo = "";
		while(rs.next()){
			
			contactNo = rs.getString("contactno");
			
			EmployeeSaving sa = EmployeeSaving.builder()
					.id(rs.getLong("sid"))
					.dateTrans(rs.getString("datetrans"))
					.saveType(rs.getInt("savetype"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactives"))
					.saveTypeName(SavingType.containId(rs.getInt("savetype")).getName())
					.paySlipId(rs.getLong("dtid"))
					//.remarks(rs.getString("remarks"))
					.remarks(contactNo)
					.withdraw(rs.getInt("iswithdraw"))
					.build();
			
			Employee e =Employee.builder()
					.eid(rs.getInt("eid"))
					.fullname(rs.getString("fullname").toUpperCase())
					.build();
			sa.setEmployee(e);
			
			vrs.add(sa);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	
	public static List<Fields> getAllWithdrawable(long eid){
		List<Fields> saves = new ArrayList<Fields>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT savetype, sum(amount) as total, eid FROM employeesaving WHERE isactives=1 AND iswithdraw=0 AND eid=" + eid +" GROUP BY savetype";
		
		
			try{
				conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			double total=0d;
			while(rs.next()){
				double amount = rs.getDouble("total");
				Fields f = Fields.builder()
						.f1(SavingType.containId(rs.getInt("savetype")).getName())
						.f2(Currency.formatAmount(rs.getDouble("total")))
						.f3(rs.getString("eid"))
						.f4(rs.getString("savetype"))
						.build();
				saves.add(f);
				total += amount;
			}
			
			Fields f = Fields.builder()
					.f1("Total")
					.f2(Currency.formatAmount(total))
					.f3("")
					.f4("")
					.build();
			saves.add(f);
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(SQLException s){}
		
		return saves;
	}
	
	public static void withDrawSavingUpdate(List<EmployeeSaving> savings, long eid) {
		String sids = "";
		
		if(savings!=null && savings.size()>0) {
			sids = " AND (";
			int cnt = 1;
			for(EmployeeSaving s : savings) {
				
				if(cnt==1) {
					sids += " sid= " + s.getId();
				}else {
					sids += " OR sid= " + s.getId();
				}
				
				cnt++;
			}
			sids += " )";
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		
			//if(isIndividual) {
				sql = "UPDATE employeesaving set iswithdraw=1 WHERE eid="+ eid +" AND isactives=1 AND iswithdraw=0 " + sids;
			//}else {
				//sql = "UPDATE employeesaving set iswithdraw=1 WHERE eid="+ eid +" AND isactives=1 AND iswithdraw=0";
			//}
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		ps.executeUpdate();
		ps.close();
		DBConnect.close(conn);
		}catch(SQLException s){}
	}
	
	public static EmployeeSaving retrieveByPayId(long id, long eid) {
		List<EmployeeSaving> data = EmployeeSaving.retrieve(" AND siv.dtid="+id + " AND siv.eid="+eid, new String[0]);
		if(data!=null && data.size()>0) {
			return data.get(0);
		}else {
			return null;
		}
	}
	
	public static List<EmployeeSaving> retrieve(String sqlAdd, String[] params){
		List<EmployeeSaving> vrs = new ArrayList<EmployeeSaving>();
		
		String tabDtr = "siv";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM employeesaving "+ tabDtr +",employees "+ tabEmp +"  WHERE " + tabDtr + ".isactives=1 AND " +
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
			
			EmployeeSaving sa = EmployeeSaving.builder()
					.id(rs.getLong("sid"))
					.dateTrans(rs.getString("datetrans"))
					.saveType(rs.getInt("savetype"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactives"))
					.saveTypeName(SavingType.containId(rs.getInt("savetype")).getName())
					.paySlipId(rs.getLong("dtid"))
					.remarks(rs.getString("remarks"))
					.withdraw(rs.getInt("iswithdraw"))
					.build();
			
			Employee e =Employee.builder()
					.eid(rs.getInt("eid"))
					.fullname(rs.getString("fullname").toUpperCase())
					.build();
			sa.setEmployee(e);
			
			vrs.add(sa);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static Object[] retrieveWithTotalAmount(String sqlAdd, String[] params){
		Object[] obj = new Object[2];
		List<EmployeeSaving> vrs = new ArrayList<EmployeeSaving>();
		double totalAmount = 0d;
		String tabDtr = "siv";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM employeesaving "+ tabDtr +",employees "+ tabEmp +"  WHERE " + tabDtr + ".isactives=1 AND " +
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
			totalAmount += rs.getDouble("amount");
			EmployeeSaving sa = EmployeeSaving.builder()
					.id(rs.getLong("sid"))
					.dateTrans(rs.getString("datetrans"))
					.saveType(rs.getInt("savetype"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactives"))
					.saveTypeName(SavingType.containId(rs.getInt("savetype")).getName())
					.paySlipId(rs.getLong("dtid"))
					.remarks(rs.getString("remarks"))
					.withdraw(rs.getInt("iswithdraw"))
					.build();
			
			Employee e =Employee.builder()
					.eid(rs.getInt("eid"))
					.fullname(rs.getString("fullname").toUpperCase())
					.build();
			sa.setEmployee(e);
			
			vrs.add(sa);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		obj[0] = vrs;
		obj[1] = totalAmount;
		
		return obj;
	}
	
	public static void saveSavingFromPayroll(double amount, int eid, long payrollId, SavingType type, String remarks) {
		EmployeeSaving data = retrieveByPayId(payrollId, eid);
		if(data!=null) {
			data.setAmount(amount);
			data.setSaveType(type.getId());
			data.setRemarks(remarks);
			data.setWithdraw(0);
			data.save();
		}else {
			EmployeeSaving.builder()
			.dateTrans(DateUtils.getCurrentDateYYYYMMDD())
			.amount(amount)
			.isActive(1)
			.saveType(type.getId())
			.remarks(remarks)
			.paySlipId(payrollId)
			.employee(Employee.builder().eid(eid).build())
			.withdraw(0)
			.build()
			.save();
		}
	}
	
	public static EmployeeSaving save(EmployeeSaving st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = EmployeeSaving.getInfo(st.getId() ==0? EmployeeSaving.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = EmployeeSaving.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = EmployeeSaving.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = EmployeeSaving.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save(){
		EmployeeSaving.save(this);
 }
	
	public static EmployeeSaving insertData(EmployeeSaving st, String type){
		String sql = "INSERT INTO employeesaving ("
				+ "sid,"
				+ "datetrans,"
				+ "savetype,"
				+ "amount,"
				+ "isactives,"
				+ "eid,"
				+ "dtid,"
				+ "remarks,"
				+ "iswithdraw)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employeesaving");
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
		ps.setInt(cnt++, st.getSaveType());
		ps.setDouble(cnt++, st.getAmount());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getEmployee().getEid());
		ps.setLong(cnt++, st.getPaySlipId());
		ps.setString(cnt++, st.getRemarks());
		ps.setInt(cnt++,st.getWithdraw());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getSaveType());
		LogU.add(st.getAmount());
		LogU.add(st.getIsActive());
		LogU.add(st.getEmployee().getEid());
		LogU.add(st.getPaySlipId());
		LogU.add(st.getRemarks());
		LogU.add(st.getWithdraw());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeesaving : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static EmployeeSaving updateData(EmployeeSaving st){
		String sql = "UPDATE employeesaving SET "
				+ "datetrans=?,"
				+ "savetype=?,"
				+ "amount=?,"
				+ "eid=?,"
				+ "dtid=?,"
				+ "remarks=?,"
				+ "iswithdraw=?" 
				+ " WHERE sid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employeesaving");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getSaveType());
		ps.setDouble(cnt++, st.getAmount());
		ps.setLong(cnt++, st.getEmployee().getEid());
		ps.setLong(cnt++, st.getPaySlipId());
		ps.setString(cnt++, st.getRemarks());
		ps.setInt(cnt++,st.getWithdraw());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getSaveType());
		LogU.add(st.getAmount());;
		LogU.add(st.getEmployee().getEid());
		LogU.add(st.getPaySlipId());
		LogU.add(st.getRemarks());
		LogU.add(st.getWithdraw());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employeesaving : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT sid FROM employeesaving  ORDER BY sid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("sid");
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
		ps = conn.prepareStatement("SELECT sid FROM employeesaving WHERE sid=?");
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
	
	/**
	 * 
	 * @param id - EmployeeSaving Id
	 */
	public static void delete(int id) {
		EmployeeSaving.builder().id(id).build().delete();
	}
	
	/**
	 * 
	 * @param eid employee id
	 * @param payId payroll id
	 */
	public static void delete(long eid, long payId, String remarks) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE employeesaving set isactives=0, remarks='"+ remarks +"' WHERE eid=? AND dtid=?";
		
		String[] params = new String[2];
		params[0] = eid+"";
		params[1] = payId+"";
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
		String sql = "UPDATE employeesaving set isactives=0 WHERE sid=?";
		
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
