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
import com.italia.enm.CashType;
import com.italia.utils.Currency;
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
public class CashProcess {

	private int id;
	private String dateTrans;
	private String description;
	private String cashType;
	private double amount;
	private int userid;
	private int employeeid;
	
	
	
	
	public static double entranceAmount(String dateFrom, String dateTo, long userid, boolean enableUserId) {
		
		String sql = "SELECT sum(totalamount) as amount FROM entrance WHERE isactiveent=1" + 
				" AND (dateTrans>='"+ dateFrom +"' AND dateTrans<='"+ dateTo +"')";
		
		if(enableUserId) {
			 sql += " AND userid=" + userid;
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		System.out.println("Entrance In retrieve: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getDouble("amount");
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){}
		
		return 0;
	}
	
	public static Map<Integer,Double> entranceAmountAllUser(String dateFrom, String dateTo) {
		Map<Integer,Double> mapData = new LinkedHashMap<Integer, Double>();
		String sql = "SELECT userid,sum(totalamount) as amount FROM entrance WHERE isactiveent=1" + 
				" AND (dateTrans>='"+ dateFrom +"' AND dateTrans<='"+ dateTo +"') GROUP BY userid";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		System.out.println("Entrance In retrieve: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			mapData.put(rs.getInt("userid"), rs.getDouble("amount"));
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){}
		
		return mapData;
	}
	
	public static List<CashProcess> getAll(){
		List<CashProcess> czs = new ArrayList<CashProcess>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "SELECT cid,dateTrans,description,amount,cashinamount,cashoutamount,cashtype,userid,employeeid FROM cashprocess WHERE isactivecash=1 ORDER BY dateTrans DESC LIMIT 500";
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("cshprocess PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				//System.out.println("rs.getDouble(amount)" + rs.getDouble("amount"));
				double amount = 0d;
				if(rs.getDouble("amount")>0) {
					amount = rs.getDouble("amount");
				}else if(rs.getDouble("cashinamount")>0) {
					amount = rs.getDouble("cashinamount");
				}else if(rs.getDouble("cashoutamount")>0) {
					amount = rs.getDouble("cashoutamount");
				}
				
				String type = CashType.containId(rs.getInt("cashtype")).getName();
				
				CashProcess ca = builder()
						.id(rs.getInt("cid"))
						.dateTrans(rs.getString("dateTrans"))
						.description(rs.getString("description"))
						.amount(amount)
						.cashType(type)
						.userid(rs.getInt("userid"))
						.employeeid(rs.getInt("employeeid"))
						.build();
				
				czs.add(ca);
				
				//System.out.println("type: " + ca.getCashType());
			}
			//System.out.println("SQL Loaded "+ czs.size() +" cashprocess...");
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		
		return czs;
	}
	
	public static List<CashProcess> getByDate(String dateSelected, int userid){
		List<CashProcess> czs = new ArrayList<CashProcess>();
		boolean isEnableUserId = true;
		
		if(userid==0) {
			Map<Integer, Double> mapData = entranceAmountAllUser(dateSelected, dateSelected);
			for(int user : mapData.keySet()) {
				CashProcess cax = builder()
						.id(0)
						.dateTrans(dateSelected)
						.description("Entrance")
						.amount(mapData.get(user))
						.cashType(CashType.ENTRANCE_TICKET.getName())
						.userid(user)
						.employeeid(1)
						.build();
				
				czs.add(cax);
			}
		}else {
			if(userid<=3) {
				isEnableUserId = false;
			}
			
			double entrance = entranceAmount(dateSelected, dateSelected, userid, isEnableUserId);
			CashProcess cax = builder()
					.id(0)
					.dateTrans(dateSelected)
					.description("Entrance")
					.amount(entrance)
					.cashType(CashType.ENTRANCE_TICKET.getName())
					.userid(userid)
					.employeeid(1)
					.build();
			
			czs.add(cax);
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		if(userid<=3) {
			isEnableUserId=false;
		}
		String sql = "SELECT cid,dateTrans,description,amount,cashinamount,cashoutamount,cashtype,userid,employeeid FROM cashprocess WHERE isactivecash=1 AND dateTrans='"+ dateSelected+"'";
		
		if(userid>=3) {
			sql +=" AND userid="+ userid;
		}
		
		sql += " ORDER BY cashtype";
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("cshprocess PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				//System.out.println("rs.getDouble(amount)" + rs.getDouble("amount"));
				double amount = 0d;
				if(rs.getDouble("amount")>0) {
					amount = rs.getDouble("amount");
				}else if(rs.getDouble("cashinamount")>0) {
					amount = rs.getDouble("cashinamount");
				}else if(rs.getDouble("cashoutamount")>0) {
					amount = rs.getDouble("cashoutamount");
				}
				
				String type = CashType.containId(rs.getInt("cashtype")).getName();
				
				CashProcess ca = builder()
						.id(rs.getInt("cid"))
						.dateTrans(rs.getString("dateTrans"))
						.description(rs.getString("description"))
						.amount(amount)
						.cashType(type)
						.userid(rs.getInt("userid"))
						.employeeid(rs.getInt("employeeid"))
						.build();
				
				czs.add(ca);
				
				//System.out.println("type: " + ca.getCashType());
			}
			//System.out.println("SQL Loaded "+ czs.size() +" cashprocess...");
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		
		return czs;
	}
	
	public static CashProcess save(CashProcess st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = CashProcess.getInfo(st.getId() ==0? CashProcess.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = CashProcess.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = CashProcess.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = CashProcess.insertData(st, "3");
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
			CashProcess.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			CashProcess.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			CashProcess.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static CashProcess insertData(CashProcess st, String type){
		String sql = "INSERT INTO cashprocess ( "
				+ "cid,"
				+ "dateTrans,"
				+ "cashtype,"
				+ "description,"
				+ "isactivecash,"
				+ "userid,"
				+ "employeeid,"
				+ "isnotified,"
				+ "amount,"
				+ "cashinamount,"
				+ "cashoutamount)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table cashprocess");
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
		ps.setInt(cnt++, CashType.containName(st.getCashType()).getId());
		ps.setString(cnt++, st.getDescription());
		ps.setInt(cnt++, 1);
		ps.setLong(cnt++, st.getUserid());
		ps.setLong(cnt++, st.getEmployeeid());
		ps.setInt(cnt++, 0);
		ps = correctingDataField(st, ps, cnt);
		//ps.setDouble(cnt++, st.getAmount());
		//ps.setDouble(cnt++, 0.00);
		//ps.setDouble(cnt++, 0.00);
		
		LogU.add(st.getDateTrans());
		LogU.add(CashType.containName(st.getCashType()).getId());
		LogU.add(st.getDescription());
		LogU.add(1);
		LogU.add(st.getUserid());
		LogU.add(st.getEmployeeid());
		LogU.add(0);
		//LogU.add(st.getAmount());
		//LogU.add(0.00);;
		//LogU.add(0.00);
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to cashprocess : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static CashProcess updateData(CashProcess st){
		String sql = "UPDATE cashprocess SET "
				+ "dateTrans=?,"
				+ "cashtype=?,"
				+ "description=?,"
				+ "userid=?,"
				+ "employeeid=?,"
				+ "isnotified=?,"
				+ "amount=?,"
				+ "cashinamount=?,"
				+ "cashoutamount=?"
				+ " WHERE cid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table cashprocess");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, CashType.containName(st.getCashType()).getId());
		ps.setString(cnt++, st.getDescription());
		ps.setLong(cnt++, st.getUserid());
		ps.setLong(cnt++, st.getEmployeeid());
		ps.setInt(cnt++, 0);
		//ps.setDouble(cnt++, st.getAmount());
		//ps.setDouble(cnt++, 0.00);
		//ps.setDouble(cnt++, 0.00);
		ps = correctingDataField(st, ps, cnt);
		cnt += 3;
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDateTrans());
		LogU.add(CashType.containName(st.getCashType()).getId());
		LogU.add(st.getDescription());
		LogU.add(st.getUserid());
		LogU.add(st.getEmployeeid());
		LogU.add(0);
		LogU.add(st.getAmount());
		LogU.add(0.00);
		LogU.add(0.00);
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to cashprocess : " + s.getMessage());
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
		sql="SELECT cid FROM cashprocess ORDER BY cid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("cid");
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
		ps = conn.prepareStatement("SELECT cid FROM cashprocess WHERE cid=?");
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
		String sql = "UPDATE cashprocess SET isactivecash=0 WHERE cid=?";
		
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
		String sql = "UPDATE cashprocess SET isactivecash=0 WHERE cid=" + idx;
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

private static PreparedStatement correctingDataField(CashProcess cs, PreparedStatement ps, int cnt) {
	
	try {
	//amount
	//in
	//out
	
		int type = CashType.containName(cs.getCashType()).getId();
	
		if(type==CashType.CASH_IN_TODAY.getId()) {
			ps.setDouble(cnt++, 0.00);
			ps.setDouble(cnt++, cs.getAmount());
			ps.setDouble(cnt++, 0.00);
			
			LogU.add("SATRT----CashType-----");
			LogU.add(0.00);
			LogU.add(cs.getAmount());
			LogU.add(0.00);
			LogU.add("END----CashType-----");
		}else if(type==CashType.CASH_OUT.getId()) {
			ps.setDouble(cnt++, 0.00);
			ps.setDouble(cnt++, 0.00);
			ps.setDouble(cnt++, cs.getAmount());
			LogU.add("SATRT----CashType-----");
			LogU.add(0.00);
			LogU.add(0.00);
			LogU.add(cs.getAmount());
			LogU.add("END----CashType-----");
		}else {
			
			ps.setDouble(cnt++, cs.getAmount());
			ps.setDouble(cnt++, 0.00);
			ps.setDouble(cnt++, 0.00);
			
			LogU.add("SATRT----CashType-----");
			LogU.add(cs.getAmount());
			LogU.add(0.00);
			LogU.add(0.00);
			LogU.add("END----CashType-----");
		}
	
	}catch(SQLException sql) {}	
	
	return ps;
}


	
}
