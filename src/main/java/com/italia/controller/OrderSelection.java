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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderSelection {

	private long id;
	private String name;
	private double qty;
	private double price;
	private int isActive;
	
	public static List<OrderSelection> getAll(){
		String sql = "SELECT * FROM osselections WHERE isactiveos=1 ORDER BY nameos ";
		List<OrderSelection> os = new ArrayList<OrderSelection>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("OrderSelection PS: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				OrderSelection o = builder()
						.id(rs.getLong("osid"))
						.name(rs.getString("nameos"))
						.qty(rs.getDouble("qty"))
						.price(rs.getDouble("price"))
						.isActive(rs.getInt("isactiveos"))
						.build();
				os.add(o);
				
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}	
		
		return os;
	}	
	
	public static OrderSelection save(OrderSelection st){
		if(st!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = OrderSelection.getInfo(st.getId() ==0? OrderSelection.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = OrderSelection.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = OrderSelection.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = OrderSelection.insertData(st, "3");
			}
			LogU.close();
		}
		return st;
	}
	
	public void save() {
		save(this);
	}
	
	public static OrderSelection insertData(OrderSelection in, String type){
		String sql = "INSERT INTO osselections ("
				+ "osid,"
				+ "nameos,"
				+ "qty,"
				+ "price,"
				+ "isactiveos) " 
				+ " values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table osselections");
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
		
		ps.setString(cnt++, in.getName());
		ps.setDouble(cnt++, in.getQty());
		ps.setDouble(cnt++, in.getPrice());
		ps.setInt(cnt++, in.getIsActive());
		
		LogU.add(in.getName());
		LogU.add(in.getQty());
		LogU.add(in.getPrice());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to osselections : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static OrderSelection updateData(OrderSelection in){
		String sql = "UPDATE osselections SET "
				+ "nameos=?,"
				+ "qty=?,"
				+ "price=?,"
				+ "isactiveos=? " 
				+ " WHERE osid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table osselections");
		
		ps.setString(cnt++, in.getName());
		ps.setDouble(cnt++, in.getQty());
		ps.setDouble(cnt++, in.getPrice());
		ps.setInt(cnt++, in.getIsActive());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getName());
		LogU.add(in.getQty());
		LogU.add(in.getPrice());
		LogU.add(in.getIsActive());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to osselections : " + s.getMessage());
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
		sql="SELECT osid FROM osselections ORDER BY osid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("osid");
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
		ps = conn.prepareStatement("SELECT osid FROM osselections WHERE osid=?");
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
		String sql = "UPDATE osselections SET isactiveos=0 WHERE osid=?";
		
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
		String sql = "UPDATE osselections SET isactiveos=0 WHERE osid=" + idx;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			DBConnect.close(conn);
			System.out.println("Executing deletion....");
			return true;
			}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
}
