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
import com.italia.enm.GatePassType;
import com.italia.utils.DateUtils;
import com.italia.utils.GlobalVar;
import com.italia.utils.LogU;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author Mark Italia
 * @since 10/29/2021
 * @version 1.0
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GatePass {
	
	private int id;
	private String dateTrans;
	private String remarks;
	private int modeType;
	private double amount;
	private int isActive;
	private int eid;
	private String fullName;
	private String issuedcheckby;
	private String timecheck;
	
	
	public static String getEid(int eid) {
		String sql = "SELECT fullname FROM employees WHERE isactiveem=1 AND eid=" + eid;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while(rs.next()){
			System.out.println("verified name: " + rs.getString("fullname"));
			return rs.getString("fullname");
		}
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		return "";
	}
	
	public static List<GatePass> retrieveAll(){
		return GatePass.retrieve("", new String[0]);
	}
	
	public static List<GatePass> getCurrentDayGatePass() {
		return retrieve(" AND paz.datetrans='"+ DateUtils.getCurrentDateYYYYMMDD() +"'", new String[0]);
	}
	
	public static List<GatePass> getDate(String date, String userType) {
		System.out.println("checking mode: " + userType);
		if("KITCHEN".equalsIgnoreCase(userType)) {
			return retrieve(" AND paz.datetrans='"+ date +"' AND paz.modetype=" + GatePassType.EMPLOYEE_OS.getId(), new String[0]);
		}else {
			return retrieve(" AND paz.datetrans='"+ date +"'", new String[0]);
		}
	}

	
	public static List<GatePass> retrieve(String sqlAdd, String[] params){
		List<GatePass> vrs = new ArrayList<GatePass>();
		
		String tabDtr = "paz";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM gatepass "+ tabDtr +",employees "+ tabEmp +"  WHERE " + tabDtr + ".isactive=1 AND " +
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
		System.out.println("gatepass sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			GatePass s = GatePass.builder()
					.id(rs.getInt("gid"))
					.dateTrans(rs.getString("datetrans"))
					.remarks(rs.getString("remarks"))
					.isActive(rs.getInt("isactive"))
					.modeType(rs.getInt("modetype"))
					.amount(rs.getDouble("amount"))
					.eid(rs.getInt("eid"))
					.fullName(rs.getString("fullname"))
					.issuedcheckby(rs.getString("checkissuedby")==null? "" : rs.getString("checkissuedby"))
					.timecheck(rs.getString("timecheck")==null? "" : rs.getString("timecheck"))
					.build();
			
			
			
			vrs.add(s);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static GatePass save(GatePass vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = GatePass.getInfo(vr.getId() ==0? GatePass.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = GatePass.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = GatePass.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = GatePass.insertData(vr, "3");
			}
			LogU.close();
		}
		return vr;
	}
	
	
	public void save(){
		GatePass.save(this);
	}
	
	public static GatePass insertData(GatePass vr, String type){
		String sql = "INSERT INTO gatepass ("
				+ "gid,"
				+ "datetrans,"
				+ "remarks,"
				+ "modetype,"
				+ "amount,"
				+ "isactive,"
				+ "eid,"
				+ "checkissuedby,"
				+ "timecheck)" 
				+ " values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table gatepass");
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
		
		
		ps.setString(cnt++, vr.getDateTrans());
		ps.setString(cnt++, vr.getRemarks());
		ps.setInt(cnt++, vr.getModeType());
		ps.setDouble(cnt++, vr.getAmount());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getEid());
		ps.setString(cnt++, vr.getIssuedcheckby());
		ps.setString(cnt++, vr.getTimecheck());
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getRemarks());
		LogU.add(vr.getModeType());
		LogU.add(vr.getAmount());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEid());
		LogU.add(vr.getIssuedcheckby());
		LogU.add(vr.getTimecheck());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to gatepass : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static GatePass updateData(GatePass vr){
		String sql = "UPDATE gatepass SET "
				+ "datetrans=?,"
				+ "remarks=?,"
				+ "modetype=?,"
				+ "amount=?,"
				+ "eid=?,"
				+ "checkissuedby=?,"
				+ "timecheck=?" 
				+ " WHERE gid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		ps.setString(cnt++, vr.getDateTrans());
		ps.setString(cnt++, vr.getRemarks());
		ps.setInt(cnt++, vr.getModeType());
		ps.setDouble(cnt++, vr.getAmount());
		ps.setLong(cnt++, vr.getEid());
		ps.setString(cnt++, vr.getIssuedcheckby());
		ps.setString(cnt++, vr.getTimecheck());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getRemarks());
		LogU.add(vr.getModeType());
		LogU.add(vr.getAmount());
		LogU.add(vr.getEid());
		LogU.add(vr.getIssuedcheckby());
		LogU.add(vr.getTimecheck());
		LogU.add(vr.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to gatepass : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT gid FROM gatepass ORDER BY gid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("gid");
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
		ps = conn.prepareStatement("SELECT gid FROM gatepass WHERE gid=?");
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
		String sql = "UPDATE gatepass set isactive=0 WHERE gid=?";
		
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
	
	public static void updateEmployeeCredit(GatePass pass, FormType type) {
		int department = Employee.getEmployeeDepartment(pass.getEid());
		long id = FormProcess.getLatestId()==0? 1 : FormProcess.getLatestId() + 1;
		
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
				+ "values("+ id +",'"+ pass.getDateTrans() +"',"+ type.getId() +","+ pass.getAmount() +",'"+ pass.getRemarks() +"',1,0,0,1,"+ pass.getEid() +","+ department +",1,"+ FormStatus.APPROVED_BY_ADMIN_OFFICER.getId() +")";
		
		
		System.out.println("SQL SAVE: " + sql);
		FormProcess.opensql(sql, null);
		EmployeePayable.updateEmployeePayable(pass.getEid(), pass.getAmount(), "ADD");
		
		
	}
	
	
}
