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

/**
 * @author Mark Italia
 * @since 08/03/2025
 * @version 1.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyInventory {

	private long id;
	private String dateTrans;
	private String name;
	private int isActive;
	
	public static List<PropertyInventory> retrieve(String sqlAdd, String[] params){
		List<PropertyInventory> props = new ArrayList<PropertyInventory>();
		
		
		String sql = "SELECT * FROM propertyinventory  WHERE isactivep=1 ";		
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
		System.out.println("propertyinventory sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			PropertyInventory prop = builder()
					.id(rs.getLong("pid"))
					.dateTrans(rs.getString("daterec"))
					.name(rs.getString("resname").toUpperCase())
					.isActive(rs.getInt("isactivep"))
					.build();
			
			props.add(prop);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return props;
	}
	
	public static PropertyInventory save(PropertyInventory vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = PropertyInventory.getInfo(vr.getId() ==0? PropertyInventory.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = PropertyInventory.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = PropertyInventory.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = PropertyInventory.insertData(vr, "3");
			}
			LogU.close();
		}
		return vr;
	}
	
	public void save() {
		save(this);
	}
	
	public static PropertyInventory insertData(PropertyInventory vr, String type){
		String sql = "INSERT INTO propertyinventory ("
				+ "pid,"
				+ "daterec,"
				+ "resname,"
				+ "isactivep)" 
				+ " VALUES(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table propertyinventory");
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
		ps.setString(cnt++, vr.getName());
		ps.setInt(cnt++, vr.getIsActive());
		
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getName());
		LogU.add(vr.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to propertyinventory : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static PropertyInventory updateData(PropertyInventory vr){
		String sql = "UPDATE propertyinventory SET "
				+ "daterec=?,"
				+ "resname=?,"
				+ "isactivep=?" 
				+ " WHERE pid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		LogU.add("updating data into table propertyinventory");
		
		ps.setString(cnt++, vr.getDateTrans());
		ps.setString(cnt++, vr.getName());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getId());
		
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getName());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to propertyinventory : " + s.getMessage());
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
		sql="SELECT pid FROM propertyinventory  ORDER BY pid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT pid FROM propertyinventory WHERE pid=?");
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
		String sql = "UPDATE propertyinventory set isactivep=0 WHERE pid=?";
		
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
