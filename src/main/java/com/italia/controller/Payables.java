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
public class Payables {

	private long id;
	private String dateRec;
	private String payee;
	private String referenceNo;
	private String description;
	private double originalAmount;
	private double paidAmount;
	private double unpaidAmount;
	private String remarks;
	private int isActive;
	private String contactNo;
	
	public static List<Payables> retrive(String sql, String[] params){
		List<Payables> recs = new ArrayList<Payables>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sqlTmp = "SELECT * FROM payables WHERE isactivepy=1 ";
		
		sqlTmp = sqlTmp + sql;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqlTmp);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("payables: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Payables rr = builder()
						.id(rs.getLong("pyid"))
						.dateRec(rs.getString("daterec"))
						.payee(rs.getString("payee"))
						.referenceNo(rs.getString("referenceno"))
						.originalAmount(rs.getDouble("originalamount"))
						.paidAmount(rs.getDouble("paidamount"))
						.unpaidAmount(rs.getDouble("unpaidamount"))
						.description(rs.getString("description"))
						.remarks(rs.getString("remarks"))
						.isActive(rs.getInt("isactivepy"))
						.contactNo(rs.getString("contactno"))
						.build();
				
				recs.add(rr);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return recs;
	}
	
	public static Payables save(Payables st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Payables.getInfo(st.getId() ==0? Payables.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Payables.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Payables.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Payables.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static Payables insertData(Payables in, String type){
		String sql = "INSERT INTO payables ("
				+ "pyid,"
				+ "daterec,"
				+ "payee,"
				+ "referenceno,"
				+ "originalamount,"
				+ "paidamount,"
				+ "unpaidamount,"
				+ "description,"
				+ "remarks,"
				+ "isactivepy,"
				+ "contactno)" 
				+ " values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table payables");
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
		
		ps.setString(cnt++, in.getDateRec());
		ps.setString(cnt++, in.getPayee());
		ps.setString(cnt++, in.getReferenceNo());
		ps.setDouble(cnt++, in.getOriginalAmount());
		ps.setDouble(cnt++, in.getPaidAmount());
		ps.setDouble(cnt++, in.getUnpaidAmount());
		ps.setString(cnt++, in.getDescription());
		ps.setString(cnt++, in.getRemarks());
		ps.setInt(cnt++, in.getIsActive());
		ps.setString(cnt++, in.getContactNo());
		
		LogU.add(in.getDateRec());
		LogU.add(in.getPayee());
		LogU.add(in.getReferenceNo());
		LogU.add(in.getOriginalAmount());
		LogU.add(in.getPaidAmount());
		LogU.add(in.getUnpaidAmount());
		LogU.add(in.getDescription());
		LogU.add(in.getRemarks());
		LogU.add(in.getIsActive());
		LogU.add(in.getContactNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to payables : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static Payables updateData(Payables in){
		String sql = "UPDATE payables SET "
				+ "daterec=?,"
				+ "payee=?,"
				+ "referenceno=?,"
				+ "originalamount=?,"
				+ "paidamount=?,"
				+ "unpaidamount=?,"
				+ "description=?,"
				+ "remarks=?,"
				+ "isactivepy=?,"
				+ "contactno=?" 
				+ " WHERE pyid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);

		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table payables");
		
		ps.setString(cnt++, in.getDateRec());
		ps.setString(cnt++, in.getPayee());
		ps.setString(cnt++, in.getReferenceNo());
		ps.setDouble(cnt++, in.getOriginalAmount());
		ps.setDouble(cnt++, in.getPaidAmount());
		ps.setDouble(cnt++, in.getUnpaidAmount());
		ps.setString(cnt++, in.getDescription());
		ps.setString(cnt++, in.getRemarks());
		ps.setInt(cnt++, in.getIsActive());
		ps.setString(cnt++, in.getContactNo());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateRec());
		LogU.add(in.getPayee());
		LogU.add(in.getReferenceNo());
		LogU.add(in.getOriginalAmount());
		LogU.add(in.getPaidAmount());
		LogU.add(in.getUnpaidAmount());
		LogU.add(in.getDescription());
		LogU.add(in.getRemarks());
		LogU.add(in.getIsActive());
		LogU.add(in.getContactNo());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to payables : " + s.getMessage());
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
		sql="SELECT pyid FROM payables ORDER BY pyid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("pyid");
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
		ps = conn.prepareStatement("SELECT pyid FROM payables WHERE pyid=?");
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
		String sql = "UPDATE payables SET isactivepy=0 WHERE pyid=?";
		
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
		String sql = "UPDATE payables SET isactivepy=0 WHERE pyid=" + idx;
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
