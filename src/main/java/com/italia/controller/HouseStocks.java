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
public class HouseStocks {
	
	private int id;
	private double quantity;
	private int isActive;
	private int hasUpdate;
	
	private Stocks stocksId;
	
	public static List<HouseStocks> getStocksAll(){
		List<HouseStocks> kitchens = new ArrayList<HouseStocks>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStocks="st";
		String tableHouse="kt";
		String sql = "SELECT * FROM housestocks "+ tableHouse +", stocks "+ tableStocks +" WHERE "+ tableHouse +".isactiveh=1 AND "+ 
				tableHouse + ".sid=" + tableStocks + ".sid"	
				+" ORDER BY "+ tableStocks +".stocknames";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("Kichen Stocks: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Stocks stock = Stocks.builder()
						.id(rs.getInt("sid"))
						.stockName(rs.getString("stocknames"))
						.quantity(rs.getDouble("qtys"))
						.isActive(rs.getInt("isactives"))
						.hasUpdate(rs.getInt("hasUpdates"))
						.barcode(rs.getString("barcode"))
						.build();
				
				HouseStocks kitchen = HouseStocks.builder()
						.id(rs.getInt("hid"))
						.quantity(rs.getDouble("qtyh"))
						.isActive(rs.getInt("isactiveh"))
						.hasUpdate(rs.getInt("hasUpdateh"))
						.stocksId(stock)
						.build();
				kitchens.add(kitchen);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return kitchens;
	}
	
	public static HouseStocks getById(int id){
		HouseStocks houses = new HouseStocks();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStocks="st";
		String tableHouse="kt";
		String sql = "SELECT * FROM housestocks "+ tableHouse +", stocks "+ tableStocks +" WHERE "+ tableHouse +".isactiveh=1 AND "+ 
				tableHouse + ".sid=" + tableStocks + ".sid AND " + tableHouse + ".hid=" + id;	
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("Get Id House: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Stocks stock = Stocks.builder()
						.id(rs.getInt("sid"))
						.stockName(rs.getString("stocknames"))
						.quantity(rs.getDouble("qtys"))
						.isActive(rs.getInt("isactives"))
						.hasUpdate(rs.getInt("hasUpdates"))
						.barcode(rs.getString("barcode"))
						.build();
				
				 houses = HouseStocks.builder()
						.id(rs.getInt("hid"))
						.quantity(rs.getDouble("qtyh"))
						.isActive(rs.getInt("isactiveh"))
						.hasUpdate(rs.getInt("hasUpdateh"))
						.stocksId(stock)
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return houses;
	}
	
	public static HouseStocks save(HouseStocks st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = HouseStocks.getInfo(st.getId() ==0? HouseStocks.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = HouseStocks.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = HouseStocks.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = HouseStocks.insertData(st, "3");
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
			HouseStocks.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			HouseStocks.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			HouseStocks.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static HouseStocks insertData(HouseStocks st, String type){
		String sql = "INSERT INTO housestocks ("
				+ "hid,"
				+ "qtyh,"
				+ "isactiveh,"
				+ "hasUpdateh,"
				+ "sid)" 
				+ " VALUES(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table housestocks");
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
		
		
		ps.setDouble(cnt++, st.getQuantity());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getHasUpdate());
		ps.setInt(cnt++, st.getStocksId().getId());
		
		LogU.add(st.getQuantity());
		LogU.add(st.getIsActive());
		LogU.add(st.getHasUpdate());
		LogU.add(st.getStocksId().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to housestocks : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static HouseStocks updateData(HouseStocks st){
		String sql = "UPDATE housestocks SET "
				+ "qtyh=?,"
				+ "hasUpdateh=?,"
				+ "sid=?" 
				+ " WHERE hid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table housestocks");
		
		ps.setDouble(cnt++, st.getQuantity());
		ps.setInt(cnt++, st.getHasUpdate());
		ps.setInt(cnt++, st.getStocksId().getId());
		ps.setInt(cnt++, st.getId());
		
		LogU.add(st.getQuantity());
		LogU.add(st.getHasUpdate());
		LogU.add(st.getStocksId().getId());
		LogU.add(st.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to housestocks : " + s.getMessage());
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
		sql="SELECT hid FROM housestocks ORDER BY hid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("hid");
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
		ps = conn.prepareStatement("SELECT hid FROM housestocks WHERE hid=?");
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
		String sql = "UPDATE housestocks SET isactiveh=0 WHERE hid=?";
		
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
		String sql = "UPDATE housestocks SET isactiveh=0 WHERE hid=" + idx;
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