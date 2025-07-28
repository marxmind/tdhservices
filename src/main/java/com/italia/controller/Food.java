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
public class Food {
	private int fid;
	private String foodName;
	private String description;
	private double price;
	private int isActive;
	private int foodType;
	private double quantity;
	private String picture;
	private int location;
	
	public static List<Food> getAllFood(){
		List<Food> foods = new ArrayList<Food>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM food WHERE isactivefood=1 ORDER BY foodname";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("food: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Food e = Food.builder()
						.fid(rs.getInt("fid"))
						.foodName(rs.getString("foodname"))
						.description(rs.getString("description"))
						.price(rs.getDouble("price"))
						.isActive(rs.getInt("isactivefood"))
						.foodType(rs.getInt("foodtype"))
						.quantity(rs.getDouble("qty"))
						.picture(rs.getString("pic")==null? "" : rs.getString("pic"))
						.location(rs.getInt("location"))
						.build();
				
				//System.out.println("food qty" +rs.getDouble("qty"));
				
				foods.add(e);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return foods;
	}
	
	public static Food getByFoodId(int id){
		Food food = new Food();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM food WHERE isactivefood=1 AND fid=" + id;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("food: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				food = Food.builder()
						.fid(rs.getInt("fid"))
						.foodName(rs.getString("foodname"))
						.description(rs.getString("description"))
						.price(rs.getDouble("price"))
						.isActive(rs.getInt("isactivefood"))
						.foodType(rs.getInt("foodtype"))
						.quantity(rs.getDouble("qty"))
						.picture(rs.getString("pic")==null? "" : rs.getString("pic"))
						.build();
				
			
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return food;
	}
	
	public static Food save(Food in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Food.getInfo(in.getFid()==0? Food.getLatestId()+1 : in.getFid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = Food.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = Food.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = Food.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = getInfo(getFid()==0? getLatestId()+1 : getFid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Food.insertData(this,"1");
			}else if(id==2){
				LogU.add("update Data ");
				Food.updateData(this);
			}else if(id==3){
				LogU.add("added new Data ");
				Food.insertData(this,"3");
			}
			LogU.close();
	}
	
	public static Food insertData(Food in, String type){
		String sql = "INSERT INTO food ("
				+ "fid,"
				+ "foodname,"
				+ "price,"
				+ "description,"
				+ "isactivefood,"
				+ "foodtype,"
				+ "qty,"
				+ "pic,"
				+ "location)" 
				+ " values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table food");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			in.setFid(id);
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			in.setFid(id);
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, in.getFoodName());
		ps.setDouble(cnt++, in.getPrice());
		ps.setString(cnt++, in.getDescription());
		ps.setInt(cnt++, in.getIsActive());
		ps.setInt(cnt++, in.getFoodType());
		ps.setDouble(cnt++, in.getQuantity());
		ps.setString(cnt++, in.getPicture());
		ps.setInt(cnt++, in.getLocation());
		
		LogU.add(in.getFoodName());
		LogU.add(in.getPrice());
		LogU.add(in.getDescription());
		LogU.add(in.getIsActive());
		LogU.add(in.getFoodType());
		LogU.add(in.getQuantity());
		LogU.add(in.getPicture());
		LogU.add(in.getLocation());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to food : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	
	
	public static Food updateData(Food in){
		String sql = "UPDATE food SET "
				+ "foodname=?,"
				+ "price=?,"
				+ "description=?,"
				+ "foodtype=?,"
				+ "qty=?,"
				+ "pic=?,"
				+ "location=?" 
				+ " WHERE fid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table food");
		
		ps.setString(cnt++, in.getFoodName());
		ps.setDouble(cnt++, in.getPrice());
		ps.setString(cnt++, in.getDescription());
		ps.setInt(cnt++, in.getFoodType());
		ps.setDouble(cnt++, in.getQuantity());
		ps.setString(cnt++, in.getPicture());
		ps.setInt(cnt++, in.getLocation());
		ps.setInt(cnt++, in.getFid());
		
		LogU.add(in.getFoodName());
		LogU.add(in.getPrice());
		LogU.add(in.getDescription());
		LogU.add(in.getFoodType());
		LogU.add(in.getQuantity());
		LogU.add(in.getPicture());
		LogU.add(in.getLocation());
		LogU.add(in.getFid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to food : " + s.getMessage());
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
		sql="SELECT fid FROM food ORDER BY fid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("fid");
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static int getInfo(long id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestId();	
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
		ps = conn.prepareStatement("SELECT fid FROM food WHERE fid=?");
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
	
	public static boolean delete(int idx){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE food set isactivefood=0 WHERE fid=" + idx;
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE food set isactivefood=0 WHERE fid=?";
		
		if(!retain){
			sql = "DELETE FROM food WHERE fid=?";
		}
		
		String[] params = new String[1];
		params[0] = getFid()+"";
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
