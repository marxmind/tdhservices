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
public class Stocks {
	
	private int id;
	private String stockName;
	private double quantity;
	private int isActive;
	private int hasUpdate;
	private String barcode;
	
	public static List<Stocks> getStocks(){
		List<Stocks> stocks = new ArrayList<Stocks>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM stocks WHERE isactives=1 ORDER BY stocknames";
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
				stocks.add(stock);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return stocks;
	}
	
	public static Stocks getById(int id){
		Stocks stock = new Stocks();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM stocks WHERE isactives=1 AND sid=" + id;
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
				
				stock = Stocks.builder()
						.id(rs.getInt("sid"))
						.stockName(rs.getString("stocknames"))
						.quantity(rs.getDouble("qtys"))
						.isActive(rs.getInt("isactives"))
						.hasUpdate(rs.getInt("hasUpdates"))
						.barcode(rs.getString("barcode"))
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return stock;
	}
	
	public static Stocks getByName(String name){
		Stocks stock = new Stocks();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM stocks WHERE isactives=1 AND stocknames like '%"+ name +"%' ORDER BY stocknames";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			
			rs = ps.executeQuery();
			//System.out.println("getByName: " + ps.toString());
			while(rs.next()){
				
				stock = Stocks.builder()
						.id(rs.getInt("sid"))
						.stockName(rs.getString("stocknames"))
						.quantity(rs.getDouble("qtys"))
						.isActive(rs.getInt("isactives"))
						.hasUpdate(rs.getInt("hasUpdates"))
						.barcode(rs.getString("barcode"))
						.build();
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return stock;
	}
	
	public static Stocks save(Stocks st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Stocks.getInfo(st.getId() ==0? Stocks.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Stocks.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Stocks.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Stocks.insertData(st, "3");
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
			Stocks.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			Stocks.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			Stocks.insertData(this, "3");
		}
		LogU.close();
	}
	
	public static Stocks insertData(Stocks st, String type){
		String sql = "INSERT INTO stocks ("
				+ "sid,"
				+ "stocknames,"
				+ "qtys,"
				+ "isactives,"
				+ "hasUpdates,"
				+ "barcode)" 
				+ " VALUES(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table stocks");
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
		
		ps.setString(cnt++, st.getStockName());
		ps.setDouble(cnt++, st.getQuantity());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getHasUpdate());
		ps.setString(cnt, st.getBarcode());
		
		LogU.add(st.getStockName());
		LogU.add(st.getQuantity());
		LogU.add(st.getIsActive());
		LogU.add(st.getHasUpdate());
		LogU.add(st.getBarcode());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to stocks : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static Stocks updateData(Stocks st){
		String sql = "UPDATE stocks SET "
				+ "stocknames=?,"
				+ "qtys=?,"
				+ "hasUpdates=?,"
				+ "barcode=?" 
				+ " WHERE sid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table stocks");
		
		ps.setString(cnt++, st.getStockName());
		ps.setDouble(cnt++, st.getQuantity());
		ps.setInt(cnt++, 1);
		ps.setString(cnt++, st.getBarcode());
		ps.setInt(cnt++, st.getId());
		
		LogU.add(st.getStockName());
		LogU.add(st.getQuantity());
		LogU.add(st.getHasUpdate());
		LogU.add(st.getBarcode());
		LogU.add(st.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to stocks : " + s.getMessage());
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
		sql="SELECT sid FROM stocks ORDER BY sid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("sid");
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
		ps = conn.prepareStatement("SELECT sid FROM stocks WHERE sid=?");
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
		String sql = "UPDATE stocks SET isactives=0 WHERE sid=?";
		
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
		String sql = "UPDATE stocks SET isactives=0 WHERE sid=" + idx;
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