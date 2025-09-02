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
public class ResortInventoryTransaction {

	private long id;
	private String dateTrans;
	private double actual;
	private double additional;
	private double missing;
	private double damaged;
	private double updatedQty;
	private String checkBy;
	private String remarks;
	private int isActive;
	private PropertyInventory property;
	private int groupId;
	private int locationId;
	
	public static List<ResortInventoryTransaction> retrieve(String sqlAdd, String[] params){
		List<ResortInventoryTransaction> props = new ArrayList<ResortInventoryTransaction>();
		
		String tableTran = "tr";
		String tableProp = "prop";
		String sql = "SELECT * FROM resortinventorytrans "+ tableTran +", propertyinventory "+ tableProp +" WHERE "+  tableTran +".isactivet=1  AND " + tableTran + ".pid=" + tableProp + ".pid";		
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
		System.out.println("resortinventorytrans sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			PropertyInventory prop = PropertyInventory.builder()
					.id(rs.getLong("pid"))
					.dateTrans(rs.getString("daterec"))
					.name(rs.getString("resname").toUpperCase())
					.isActive(rs.getInt("isactivep"))
					.build();
			
			ResortInventoryTransaction resort = builder()
					.id(rs.getLong("tid"))
					.dateTrans(rs.getString("datetrans"))
					.actual(rs.getDouble("actual"))
					.additional(rs.getDouble("additional"))
					.missing(rs.getDouble("missing"))
					.damaged(rs.getDouble("damaged"))
					.updatedQty(rs.getDouble("updatedqty"))
					.checkBy(rs.getString("checkby"))
					.remarks(rs.getString("remarks"))
					.groupId(rs.getInt("groupid"))
					.locationId(rs.getInt("locationid"))
					.isActive(rs.getInt("isactivet"))
					.property(prop)
					.build();
			
			
			props.add(resort);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return props;
	}
	
	public static ResortInventoryTransaction save(ResortInventoryTransaction vr){
		if(vr!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = ResortInventoryTransaction.getInfo(vr.getId() ==0? ResortInventoryTransaction.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = ResortInventoryTransaction.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = ResortInventoryTransaction.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = ResortInventoryTransaction.insertData(vr, "3");
			}
			LogU.close();
		}
		return vr;
	}
	
	public void save() {
		save(this);
	}
	
	public static ResortInventoryTransaction insertData(ResortInventoryTransaction vr, String type){
		String sql = "INSERT INTO resortinventorytrans ("
				+ "tid,"
				+ "datetrans,"
				+ "actual,"
				+ "additional,"
				+ "missing,"
				+ "damaged,"
				+ "updatedqty,"
				+ "checkby,"
				+ "remarks,"
				+ "groupid,"
				+ "locationid,"
				+ "pid,"
				+ "isactivet)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table resortinventorytrans");
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
		ps.setDouble(cnt++, vr.getActual());
		ps.setDouble(cnt++, vr.getAdditional());
		ps.setDouble(cnt++, vr.getMissing());
		ps.setDouble(cnt++, vr.getDamaged());
		ps.setDouble(cnt++, vr.getUpdatedQty());
		ps.setString(cnt++, vr.getCheckBy());
		ps.setString(cnt++, vr.getRemarks());
		ps.setInt(cnt++, vr.getGroupId());
		ps.setInt(cnt++, vr.getLocationId());
		ps.setLong(cnt++, vr.getProperty().getId());
		ps.setInt(cnt++, vr.getIsActive());
		
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getActual());
		LogU.add(vr.getAdditional());
		LogU.add(vr.getMissing());
		LogU.add(vr.getDamaged());
		LogU.add(vr.getUpdatedQty());
		LogU.add(vr.getCheckBy());
		LogU.add(vr.getRemarks());
		LogU.add(vr.getGroupId());
		LogU.add(vr.getLocationId());
		LogU.add(vr.getProperty().getId());
		LogU.add(vr.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to resortinventorytrans : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static ResortInventoryTransaction updateData(ResortInventoryTransaction vr){
		String sql = "UPDATE resortinventorytrans SET "
				+ "datetrans=?,"
				+ "actual=?,"
				+ "additional=?,"
				+ "missing=?,"
				+ "damaged=?,"
				+ "updatedqty=?,"
				+ "checkby=?,"
				+ "remarks=?,"
				+ "groupid=?,"
				+ "locationid=?,"
				+ "pid=?,"
				+ "isactivet=?" 
				+ " WHERE tid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		LogU.add("updating data into table resortinventorytrans");
		
		
		ps.setString(cnt++, vr.getDateTrans());
		ps.setDouble(cnt++, vr.getActual());
		ps.setDouble(cnt++, vr.getAdditional());
		ps.setDouble(cnt++, vr.getMissing());
		ps.setDouble(cnt++, vr.getDamaged());
		ps.setDouble(cnt++, vr.getUpdatedQty());
		ps.setString(cnt++, vr.getCheckBy());
		ps.setString(cnt++, vr.getRemarks());
		ps.setInt(cnt++, vr.getGroupId());
		ps.setInt(cnt++, vr.getLocationId());
		ps.setLong(cnt++, vr.getProperty().getId());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getId());
		
		
		LogU.add(vr.getDateTrans());
		LogU.add(vr.getActual());
		LogU.add(vr.getAdditional());
		LogU.add(vr.getMissing());
		LogU.add(vr.getDamaged());
		LogU.add(vr.getUpdatedQty());
		LogU.add(vr.getCheckBy());
		LogU.add(vr.getRemarks());
		LogU.add(vr.getGroupId());
		LogU.add(vr.getLocationId());
		LogU.add(vr.getProperty().getId());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to resortinventorytrans : " + s.getMessage());
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
		sql="SELECT tid FROM resortinventorytrans  ORDER BY tid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tid");
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
		ps = conn.prepareStatement("SELECT tid FROM resortinventorytrans WHERE tid=?");
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
		String sql = "UPDATE resortinventorytrans set isactivet=0 WHERE tid=?";
		
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
