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
public class KitchenStocks {
	
	private int id;
	private double quantity;
	private int isActive;
	private int hasUpdate;
	private Stocks stocksId;
	
	public static List<KitchenStocks> getStocks(){
		List<KitchenStocks> kitchens = new ArrayList<KitchenStocks>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStocks="st";
		String tableKitchen="kt";
		String sql = "SELECT * FROM kitchenstocks "+ tableKitchen +", stocks "+ tableStocks +" WHERE "+ tableKitchen +".isactivek=1 AND "+ 
				tableKitchen + ".sid=" + tableStocks + ".sid"	
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
				
				KitchenStocks kitchen = KitchenStocks.builder()
						.id(rs.getInt("kid"))
						.quantity(rs.getDouble("qtyk"))
						.isActive(rs.getInt("isactivek"))
						.hasUpdate(rs.getInt("hasUpdatek"))
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
	
	public static KitchenStocks getById(int id){
		KitchenStocks kitchen = new KitchenStocks();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String tableStocks="st";
		String tableKitchen="kt";
		String sql = "SELECT * FROM kitchenstocks "+ tableKitchen +", stocks "+ tableStocks +" WHERE "+ tableKitchen +".isactivek=1 AND "+ 
				tableKitchen + ".sid=" + tableStocks + ".sid AND " + tableKitchen + ".kid=" + id;	
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			
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
				
				 kitchen = KitchenStocks.builder()
						.id(rs.getInt("kid"))
						.quantity(rs.getDouble("qtyk"))
						.isActive(rs.getInt("isactivek"))
						.hasUpdate(rs.getInt("hasUpdatek"))
						.stocksId(stock)
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return kitchen;
	}
	
	public static KitchenStocks save(KitchenStocks st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = KitchenStocks.getInfo(st.getId() ==0? KitchenStocks.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = KitchenStocks.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = KitchenStocks.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = KitchenStocks.insertData(st, "3");
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
			KitchenStocks.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			KitchenStocks.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			KitchenStocks.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static KitchenStocks insertData(KitchenStocks st, String type){
		String sql = "INSERT INTO kitchenstocks ("
				+ "kid,"
				+ "qtyk,"
				+ "isactivek,"
				+ "hasUpdatek,"
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
		LogU.add("inserting data into table kitchenstocks");
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
			LogU.add("error inserting data to kitchenstocks : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static KitchenStocks updateData(KitchenStocks st){
		String sql = "UPDATE kitchenstocks SET "
				+ "qtyk=?,"
				+ "hasUpdatek=?,"
				+ "sid=?" 
				+ " WHERE kid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table kitchenstocks");
		
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
			LogU.add("error inserting data to kitchenstocks : " + s.getMessage());
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
		sql="SELECT kid FROM kitchenstocks ORDER BY kid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("kid");
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
		ps = conn.prepareStatement("SELECT kid FROM kitchenstocks WHERE kid=?");
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
		String sql = "UPDATE kitchenstocks SET isactivek=0 WHERE kid=?";
		
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
		String sql = "UPDATE kitchenstocks SET isactivek=0 WHERE kid=" + idx;
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