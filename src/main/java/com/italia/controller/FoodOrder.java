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
public class FoodOrder {

	private long id;
	private String receiptno;
	private String date;
	private String customerName;
	private String tableName;
	private String notes;
	private long cashierId;
	private long waiterId;
	private double subtotal;
	private double tax;
	private double discount;
	private double grandTotal;
	private double cash;
	private double change;
	private int paymentType;
	private int isCompleted;
	private int isActive;
	private String time;
	private List<FoodItem> items;
	
	
	public static String getNewReceiptNo() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String reciptNo = "0000000000";
		//String sql = "SELECT recieptno FROM foodorder WHERE isactiverepor=1 ORDER BY orid DESC LIMIT 1";
		String sql = "SELECT recieptno FROM foodorder ORDER BY orid DESC LIMIT 1";
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			reciptNo = rs.getString("recieptno");
		}
		
		rs.close();
		ps.close(); 
		DBConnect.close(conn);
		
		long num = Long.valueOf(reciptNo);
		long newNum = num + 1;
		String tmp = newNum + ""; //.length();
		int len = tmp.length();
		
		switch(len) {
			case 1 : reciptNo="000000000" + newNum; break;
			case 2 : reciptNo="00000000" + newNum; break;
			case 3 : reciptNo="0000000" + newNum; break;
			case 4 : reciptNo="000000" + newNum; break;
			case 5 : reciptNo="00000" + newNum; break;
			case 6 : reciptNo="0000" + newNum; break;
			case 7 : reciptNo="000" + newNum; break;
			case 8 : reciptNo="00" + newNum; break;
			case 9 : reciptNo="0" + newNum; break;
			case 10 : reciptNo="" + newNum; break;
		}
		
		
		
		}catch(Exception e){e.getMessage();}
		
		return reciptNo;
	}
	
	public static FoodOrder retrieveOrder(long id){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		FoodOrder order = null;
		//String sql = "SELECT * FROM foodorder WHERE isactiverepor=1 AND orid=" + id;
		String sql = "SELECT * FROM foodorder WHERE  orid=" + id;
		
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			 order = FoodOrder.builder()
					.id(rs.getLong("orid"))
					.receiptno(rs.getString("recieptno"))
					.date(rs.getString("ordate"))
					.customerName(rs.getString("customername"))
					.cashierId(rs.getLong("cashiereid"))
					.waiterId(rs.getLong("waitereid"))
					.subtotal(rs.getDouble("subtotal"))
					.tax(rs.getDouble("tax"))
					.discount(rs.getDouble("discount"))
					.grandTotal(rs.getDouble("grandtotal"))
					.cash(rs.getDouble("cash"))
					.change(rs.getDouble("cashchange"))
					.paymentType(rs.getInt("paymenttype"))
					.isCompleted(rs.getInt("iscompleted"))
					.isActive(rs.getInt("isactiverepor"))
					.tableName(rs.getString("tablename"))
					.notes(rs.getString("notes"))
					.time(rs.getString("timerec"))
					.items(FoodItem.retrieve(" AND i.orid=" + rs.getLong("orid"), new String[0]))
					.build();
		}
		
		rs.close();
		ps.close(); 
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return order;
		
	}
	
	public static List<FoodOrder> retrieve(String sqlAdd, String[] params){
		List<FoodOrder> items = new ArrayList<FoodOrder>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		//String sql = "SELECT * FROM foodorder WHERE isactiverepor=1 ";
		String sql = "SELECT * FROM foodorder WHERE (isactiverepor=0 OR isactiverepor=1) ";
		sql = sql + sqlAdd;
		
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			FoodOrder item = FoodOrder.builder()
					.id(rs.getLong("orid"))
					.receiptno(rs.getString("recieptno"))
					.date(rs.getString("ordate"))
					.customerName(rs.getString("customername"))
					.cashierId(rs.getLong("cashiereid"))
					.waiterId(rs.getLong("waitereid"))
					.subtotal(rs.getDouble("subtotal"))
					.tax(rs.getDouble("tax"))
					.discount(rs.getDouble("discount"))
					.grandTotal(rs.getDouble("grandtotal"))
					.cash(rs.getDouble("cash"))
					.change(rs.getDouble("cashchange"))
					.paymentType(rs.getInt("paymenttype"))
					.isCompleted(rs.getInt("iscompleted"))
					.isActive(rs.getInt("isactiverepor"))
					.tableName(rs.getString("tablename"))
					.notes(rs.getString("notes"))
					.time(rs.getString("timerec"))
					.items(FoodItem.retrieve(" AND i.orid=" + rs.getLong("orid"), params))
					.build();
			
			items.add(item);
		}
		
		rs.close();
		ps.close(); 
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
		
	}
	
	public static List<FoodOrder> retrieveHistory(String sqlAdd, String[] params){
		List<FoodOrder> items = new ArrayList<FoodOrder>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sql = "SELECT * FROM foodorder WHERE isactiverepor=1 ";
		sql = sql + sqlAdd;
		
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			FoodOrder item = FoodOrder.builder()
					.id(rs.getLong("orid"))
					.receiptno(rs.getString("recieptno"))
					.date(rs.getString("ordate"))
					.customerName(rs.getString("customername"))
					.cashierId(rs.getLong("cashiereid"))
					.waiterId(rs.getLong("waitereid"))
					.subtotal(rs.getDouble("subtotal"))
					.tax(rs.getDouble("tax"))
					.discount(rs.getDouble("discount"))
					.grandTotal(rs.getDouble("grandtotal"))
					.cash(rs.getDouble("cash"))
					.change(rs.getDouble("cashchange"))
					.paymentType(rs.getInt("paymenttype"))
					.isCompleted(rs.getInt("iscompleted"))
					.isActive(rs.getInt("isactiverepor"))
					.tableName(rs.getString("tablename"))
					.notes(rs.getString("notes"))
					.time(rs.getString("timerec"))
					.items(FoodItem.retrieve(" AND i.orid=" + rs.getLong("orid"), params))
					.build();
			
			items.add(item);
		}
		
		rs.close();
		ps.close(); 
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
		
	}
	
	public static FoodOrder save(FoodOrder in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = FoodOrder.getInfo(in.getId()==0? FoodOrder.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = FoodOrder.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = FoodOrder.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = FoodOrder.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		FoodOrder.save(this);
	}
	
	public static FoodOrder insertData(FoodOrder in, String type){
		String sql = "INSERT INTO foodorder ("
				+ "orid,"
				+ "recieptno,"
				+ "ordate,"
				+ "customername,"
				+ "tablename,"
				+ "notes,"
				+ "cashiereid,"
				+ "waitereid,"
				+ "subtotal,"
				+ "tax,"
				+ "discount,"
				+ "grandtotal,"
				+ "cash,"
				+ "cashchange,"
				+ "paymenttype,"
				+ "iscompleted,"
				+ "isactiverepor,"
				+ "timerec) " 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table foodorder");
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
		
		String recNo = getNewReceiptNo();
		
		ps.setString(cnt++, recNo);
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getCustomerName());
		ps.setString(cnt++, in.getTableName());
		ps.setString(cnt++, in.getNotes());
		ps.setLong(cnt++, in.getCashierId());
		ps.setLong(cnt++, in.getWaiterId());
		ps.setDouble(cnt++, in.getSubtotal());
		ps.setDouble(cnt++, in.getTax());
		ps.setDouble(cnt++, in.getDiscount());
		ps.setDouble(cnt++, in.getGrandTotal());
		ps.setDouble(cnt++, in.getCash());
		ps.setDouble(cnt++, in.getChange());
		ps.setInt(cnt++, in.getPaymentType());
		ps.setInt(cnt++, in.getIsCompleted());
		ps.setInt(cnt++, in.getIsActive());
		ps.setString(cnt++, in.getTime());
		
		LogU.add(recNo);
		LogU.add(in.getDate());
		LogU.add(in.getCustomerName());
		LogU.add(in.getTableName());
		LogU.add(in.getNotes());
		LogU.add(in.getCashierId());
		LogU.add(in.getWaiterId());
		LogU.add(in.getSubtotal());
		LogU.add(in.getTax());
		LogU.add(in.getDiscount());
		LogU.add(in.getGrandTotal());
		LogU.add(in.getCash());
		LogU.add(in.getChange());
		LogU.add(in.getPaymentType());
		LogU.add(in.getIsCompleted());
		LogU.add(in.getIsActive());
		LogU.add(in.getTime());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to foodorder : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static FoodOrder updateData(FoodOrder in){
		String sql = "UPDATE foodorder SET "
				+ "recieptno=?,"
				+ "ordate=?,"
				+ "customername=?,"
				+ "tablename=?,"
				+ "notes=?,"
				+ "cashiereid=?,"
				+ "waitereid=?,"
				+ "subtotal=?,"
				+ "tax=?,"
				+ "discount=?,"
				+ "grandtotal=?,"
				+ "cash=?,"
				+ "cashchange=?,"
				+ "paymenttype=?,"
				+ "iscompleted=?,"
				+ "timerec=?" 
				+ " WHERE orid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table foodorder");
		
		ps.setString(cnt++, in.getReceiptno());
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getCustomerName());
		ps.setString(cnt++, in.getTableName());
		ps.setString(cnt++, in.getNotes());
		ps.setLong(cnt++, in.getCashierId());
		ps.setLong(cnt++, in.getWaiterId());
		ps.setDouble(cnt++, in.getSubtotal());
		ps.setDouble(cnt++, in.getTax());
		ps.setDouble(cnt++, in.getDiscount());
		ps.setDouble(cnt++, in.getGrandTotal());
		ps.setDouble(cnt++, in.getCash());
		ps.setDouble(cnt++, in.getChange());
		ps.setInt(cnt++, in.getPaymentType());
		ps.setInt(cnt++, in.getIsCompleted());
		ps.setString(cnt++, in.getTime());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getReceiptno());
		LogU.add(in.getDate());
		LogU.add(in.getCustomerName());
		LogU.add(in.getTableName());
		LogU.add(in.getNotes());
		LogU.add(in.getCashierId());
		LogU.add(in.getWaiterId());
		LogU.add(in.getSubtotal());
		LogU.add(in.getTax());
		LogU.add(in.getDiscount());
		LogU.add(in.getGrandTotal());
		LogU.add(in.getCash());
		LogU.add(in.getChange());
		LogU.add(in.getPaymentType());
		LogU.add(in.getIsCompleted());
		LogU.add(in.getTime());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to foodorder : " + s.getMessage());
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
		sql="SELECT orid FROM foodorder  ORDER BY orid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("orid");
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
		ps = conn.prepareStatement("SELECT orid FROM foodorder WHERE orid=?");
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
		String sql = "UPDATE foodorder set isactiverepor=0 WHERE orid=" + idx;
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
		String sql = "UPDATE foodorder set isactiverepor=0 WHERE orid=?";
		
		if(!retain){
			sql = "DELETE FROM foodorder WHERE orid=?";
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
	
}
