package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
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
/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 06/12/2022
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerProfile {

	private long id;
	private String dateTrans;
	private String idNumber;
	private String fullname;
	private String address;
	private String contactNo;
	private int isActive;
	
	public static String getLatestCustomerNo() {
		String customerNo = null;
		int year = DateUtils.getCurrentYear();
		int mnth = DateUtils.getCurrentMonth();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT idno FROM customerprofile WHERE isactivec=1 ORDER BY cid DESC LIMIT 1");
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			customerNo = rs.getString("idno");
		}
		
		String month = mnth<10? "0"+mnth : mnth+""; 
		if(customerNo==null) {
			customerNo = year +"-"+ month +"-000000000001";
		}else {
			
			String[] vals = customerNo.split("-");
			int tmpYear = Integer.valueOf(vals[0]);
			//if(year.equalsIgnoreCase(vals[1])) {
			if(year == tmpYear) {	
				int number = Integer.valueOf(vals[2]);
				number += 1; //add 1
				
				String newSeries = number + "";
				int len = newSeries.length();
				
				switch(len) {
					case 1 : customerNo = year +"-"+ month +"-00000000000" + newSeries;  break;
					case 2 : customerNo = year +"-"+ month +"-0000000000" + newSeries;  break;
					case 3 : customerNo = year +"-"+ month +"-000000000" + newSeries;  break;
					case 4 : customerNo = year +"-"+ month +"-00000000" + newSeries;  break;
					case 5 : customerNo = year +"-"+ month +"-0000000" + newSeries;  break;
					case 6 : customerNo = year +"-"+ month +"-000000" + newSeries;  break;
					case 7 : customerNo = year +"-"+ month +"-00000" + newSeries;  break;
					case 8 : customerNo = year +"-"+ month +"-0000" + newSeries;  break;
					case 9 : customerNo = year +"-"+ month +"-000" + newSeries;  break;
					case 10 : customerNo = year +"-"+ month +"-00" + newSeries;  break;
					case 11 : customerNo = year +"-"+ month +"-0" + newSeries;  break;
					case 12 : customerNo = year +"-"+ month +"-" + newSeries;  break;
					
				}
				
				
			}else {//not equal to current year
				customerNo = year +"-"+ month +"-000000000001";
			}
			
			
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		
		}catch(Exception e){e.getMessage();}
		
		return customerNo;
	}
	
	public static List<CustomerProfile> getAll(String sql){
		return retrieve(sql, new String[0]);
	}
	
	public static List<CustomerProfile> retrieve(String sqlAdd, String[] params){
		List<CustomerProfile> vrs = new ArrayList<CustomerProfile>();
		
		
		String sql = "SELECT * FROM customerprofile  WHERE isactivec=1 ";
		sql += sqlAdd;		
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
		System.out.println("customerprofile sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			String address = rs.getString("address")==null? "" : rs.getString("address");
			address = address.isEmpty()? "" : rs.getString("address");
			
			String contact = rs.getString("contactno")==null? "" : rs.getString("contactno");
			contact = contact.isEmpty()? "" : rs.getString("contactno");
			
			CustomerProfile cp = CustomerProfile.builder()
					.id(rs.getLong("cid"))
					.dateTrans(rs.getString("datetrans"))
					.idNumber(rs.getString("idno"))
					.fullname(rs.getString("fullname"))
					.address(address)
					.contactNo(contact)
					.isActive(rs.getInt("isactivec"))
					.build();
			vrs.add(cp);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static CustomerProfile save(CustomerProfile vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = CustomerProfile.getInfo(vr.getId() ==0? CustomerProfile.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = CustomerProfile.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = CustomerProfile.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = CustomerProfile.insertData(vr, "3");
			}
			LogU.close();
		}
		return vr;
	}
	
	public CustomerProfile save(){
			CustomerProfile vr = new CustomerProfile();
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = CustomerProfile.getInfo(getId() ==0? getLatestId()+1 : getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = insertData(this, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = updateData(this);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = insertData(this, "3");
			}
			LogU.close();
		
		return vr;
	}
	
	
	public static CustomerProfile insertData(CustomerProfile vr, String type){
		String sql = "INSERT INTO customerprofile ("
				+ "cid,"
				+ "datetrans,"
				+ "idno,"
				+ "fullname,"
				+ "address,"
				+ "contactno,"
				+ "isactivec)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table customerprofile");
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
		ps.setString(cnt++, vr.getIdNumber());
		ps.setString(cnt++, vr.getFullname());
		ps.setString(cnt++, vr.getAddress());
		ps.setString(cnt++, vr.getContactNo());
		ps.setInt(cnt++, vr.getIsActive());
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getIdNumber());
		LogU.add(vr.getFullname());
		LogU.add(vr.getAddress());
		LogU.add(vr.getContactNo());
		LogU.add(vr.getIsActive());
		
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
	
	public static CustomerProfile updateData(CustomerProfile vr){
		String sql = "UPDATE customerprofile SET "
				+ "datetrans=?,"
				+ "idno=?,"
				+ "fullname=?,"
				+ "address=?,"
				+ "contactno=?"
				+ " WHERE cid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		LogU.add("updating data into table customerprofile");
		
		ps.setString(cnt++, vr.getDateTrans());
		ps.setString(cnt++, vr.getIdNumber());
		ps.setString(cnt++, vr.getFullname());
		ps.setString(cnt++, vr.getAddress());
		ps.setString(cnt++, vr.getContactNo());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getIdNumber());
		LogU.add(vr.getFullname());
		LogU.add(vr.getAddress());
		LogU.add(vr.getContactNo());
		LogU.add(vr.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerprofile : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT cid FROM customerprofile  ORDER BY cid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("cid");
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
		ps = conn.prepareStatement("SELECT cid FROM customerprofile WHERE cid=?");
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
		String sql = "UPDATE customerprofile set isactivec=0 WHERE cid=?";
		
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
