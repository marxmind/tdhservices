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
public class FoodItem implements Comparable<FoodItem>{

	private long id;
	private String date;
	private double qty;
	private double price;
	private long foodOrderId;
	private int isActive;
	private int foodId;
	
	private String foodName;
	
	public static List<FoodItem> retrieve(String sqlAdd, String[] params){
		List<FoodItem> items = new ArrayList<FoodItem>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sql = "SELECT * FROM fooditem i, foodorder o, food f WHERE i.isactiverepft=1 AND i.fid=f.fid AND i.orid=o.orid ";
		sql = sql + sqlAdd;
		
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			FoodItem item = FoodItem.builder()
					.id(rs.getLong("ftid"))
					.date(rs.getString("dateitem"))
					.qty(rs.getDouble("qty"))
					.price(rs.getDouble("price"))
					.foodOrderId(rs.getLong("orid"))
					.isActive(rs.getInt("isactiverepft"))
					.foodId(rs.getInt("fid"))
					.foodName(rs.getString("foodname"))
					.build();
			
			items.add(item);
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
		
	}
	
	public static FoodItem save(FoodItem in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = FoodItem.getInfo(in.getId()==0? FoodItem.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = FoodItem.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = FoodItem.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = FoodItem.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		FoodItem.save(this);
	}
	
	public static FoodItem insertData(FoodItem in, String type){
		String sql = "INSERT INTO fooditem ("
				+ "ftid,"
				+ "dateitem,"
				+ "qty,"
				+ "price,"
				+ "orid,"
				+ "isactiverepft,"
				+ "fid) " 
				+ " VALUES(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table fooditem");
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
		
		ps.setString(cnt++, in.getDate());
		ps.setDouble(cnt++, in.getQty());
		ps.setDouble(cnt++, in.getPrice());
		ps.setLong(cnt++, in.getFoodOrderId());
		ps.setInt(cnt++, in.getIsActive());
		ps.setInt(cnt++, in.getFoodId());
		
		LogU.add(in.getDate());
		LogU.add(in.getQty());
		LogU.add(in.getPrice());
		LogU.add(in.getFoodOrderId());
		LogU.add(in.getIsActive());
		LogU.add(in.getFoodId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to fooditem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static FoodItem updateData(FoodItem in){
		String sql = "UPDATE fooditem SET "
				+ "dateitem=?,"
				+ "qty=?,"
				+ "price=?,"
				+ "orid=?,"
				+ "fid=?" 
				+ " WHERE ftid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table fooditem");
		
		ps.setString(cnt++, in.getDate());
		ps.setDouble(cnt++, in.getQty());
		ps.setDouble(cnt++, in.getPrice());
		ps.setLong(cnt++, in.getFoodOrderId());
		ps.setInt(cnt++, in.getFoodId());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDate());
		LogU.add(in.getQty());
		LogU.add(in.getPrice());
		LogU.add(in.getFoodOrderId());
		LogU.add(in.getFoodId());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to fooditem : " + s.getMessage());
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
		sql="SELECT ftid FROM fooditem  ORDER BY ftid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("ftid");
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
		ps = conn.prepareStatement("SELECT ftid FROM fooditem WHERE ftid=?");
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
	
	public static boolean delete(int idx){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE fooditem set isactiverepft=0 WHERE ftid=" + idx;
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
		String sql = "UPDATE fooditem set isactiverepft=0 WHERE ftid=?";
		
		if(!retain){
			sql = "DELETE FROM fooditem WHERE ftid=?";
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

	@Override
	public int compareTo(FoodItem o) {
		// TODO Auto-generated method stub
		return this.foodName.compareTo(o.foodName);
	}
	
	
}
