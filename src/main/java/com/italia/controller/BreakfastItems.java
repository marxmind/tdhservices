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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Mark Italia
 * @since 8/20/2025
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class BreakfastItems {

	private long id;
	private String dateTrans;
	private double qty;
	private int isActive;
	
	private Food food;
	private Breakfast breakfast;
	
	public static List<BreakfastItems> retriveByBreakfastId(long id){
		return retrive(" AND bf.bid=" + id, new String[0]);
	}
	
	public static double isExisting(int foodId, long breakfastId) {
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement("SELECT qty FROM breakfastitems WHERE bid=" + breakfastId + " AND fid=" + foodId);
			System.out.println("breakfastitems PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				return rs.getDouble("qty");
			}
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static List<BreakfastItems> retrive(String sql, String[] params){
		List<BreakfastItems> rsvs = new ArrayList<BreakfastItems>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String tableFood = "fd";
		String tableBf = "bf";
		String tableItem = "rv";
		String sqltmp = "SELECT * FROM breakfastitems "+ tableItem +", breakfast " + tableBf + ", food " + tableFood + " WHERE " + tableItem + ".isactivebt=1 AND " + tableItem + ".bid=" + tableBf + ".bid AND " + tableItem + ".fid=" + tableFood +".fid ";
		
		//sqltmp += " AND " + tableBf + ".bid= ";
		
		sqltmp += sql;
		
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sqltmp);
			System.out.println("breakfastitems PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Food food = Food.builder()
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
				
				
				Breakfast bf = Breakfast.builder()
						.id(rs.getLong("bid"))
						.dateRec(rs.getString("daterec"))
						.dateServing(rs.getString("dateserve"))
						.timeServing(rs.getString("timeserve"))
						.notes(rs.getString("notes"))
						.isActive(rs.getInt("isactiveb"))
						.reservation(Reservation.builder().id(rs.getLong("rid")).build())
						.build();
				
				BreakfastItems item = builder()
						.id(rs.getLong("btid"))
						.dateTrans(rs.getString("datetrans"))
						.qty(rs.getDouble("qty"))
						.isActive(rs.getInt("isactivebt"))
						.food(food)
						.breakfast(bf)
						.build();
				
				
				rsvs.add(item);
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		return rsvs;
	}
	
	public static List<BreakfastItems> getAll(int year, int month, String date){
		List<BreakfastItems> rsvs = new ArrayList<BreakfastItems>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String tableFood = "fd";
		String tableBf = "bf";
		String tableItem = "rv";
		String sql = "SELECT * FROM breakfastitems "+ tableItem +", breakfast " + tableBf + ", food " + tableFood + " WHERE " + tableItem + ".isactivebt=1 AND " + tableItem + ".bid=" + tableBf + ".bid AND " + tableItem + ".fid=" + tableFood +".fid";
		
		
		if(date==null) {
			sql += " AND year("+ tableBf + ".dateserve)="+ year +" AND month(" + tableBf + ".dateserve)=" + month;
			sql += " ORDER BY " + tableItem + ".btid";
		}else {
			sql += " AND "+ tableBf + ".dateserve='"+ date +"'";
			sql += " ORDER BY  "+ tableItem + ".btid";
		}
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("breakfastitems PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Food food = Food.builder()
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
				
				
				Breakfast bf = Breakfast.builder()
						.id(rs.getLong("bid"))
						.dateRec(rs.getString("daterec"))
						.dateServing(rs.getString("dateserve"))
						.timeServing(rs.getString("timeserve"))
						.notes(rs.getString("notes"))
						.isActive(rs.getInt("isactiveb"))
						.reservation(Reservation.builder().id(rs.getLong("rid")).build())
						.build();
				
				BreakfastItems item = builder()
						.id(rs.getLong("btid"))
						.dateTrans(rs.getString("datetrans"))
						.qty(rs.getDouble("qty"))
						.isActive(rs.getInt("isactivebt"))
						.food(food)
						.breakfast(bf)
						.build();
				
				
				rsvs.add(item);
			}
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		return rsvs;
	}
	
	public static BreakfastItems save(BreakfastItems st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = BreakfastItems.getInfo(st.getId() ==0? BreakfastItems.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = BreakfastItems.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = BreakfastItems.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = BreakfastItems.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static BreakfastItems insertData(BreakfastItems in, String type){
		String sql = "INSERT INTO breakfastitems ("
				+ "btid,"
				+ "datetrans,"
				+ "qty,"
				+ "fid,"
				+ "bid,"
				+ "isactivebt) " 
				+ " values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table breakfastitems");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			in.setId(Long.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			in.setId(Long.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setDouble(cnt++, in.getQty());
		ps.setInt(cnt++, in.getFood().getFid());
		ps.setLong(cnt++, in.getBreakfast().getId());
		ps.setInt(cnt++, in.getIsActive());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getQty());
		LogU.add(in.getFood().getFid());
		LogU.add(in.getBreakfast().getId());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to breakfastitems : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static BreakfastItems updateData(BreakfastItems in){
		String sql = "UPDATE breakfastitems SET "
				+ "datetrans=?,"
				+ "qty=?,"
				+ "fid=?,"
				+ "bid=?,"
				+ "isactivebt=? " 
				+ " WHERE btid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table breakfastitems");
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setDouble(cnt++, in.getQty());
		ps.setInt(cnt++, in.getFood().getFid());
		ps.setLong(cnt++, in.getBreakfast().getId());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getQty());
		LogU.add(in.getFood().getFid());
		LogU.add(in.getBreakfast().getId());
		LogU.add(in.getIsActive());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to breakfastitems : " + s.getMessage());
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
		sql="SELECT btid FROM breakfastitems ORDER BY btid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("btid");
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
		ps = conn.prepareStatement("SELECT btid FROM breakfastitems WHERE btid=?");
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
		String sql = "UPDATE breakfastitems SET isactivebt=0 WHERE btid=?";
		
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
		String sql = "UPDATE breakfastitems SET isactivebt=0 WHERE btid=" + idx;
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
