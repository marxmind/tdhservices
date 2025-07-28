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
public class KitchenOrder {

	private long id;
	private String date;
	private String time;
	private String orderNumber;
	private String servingDate;
	private int orderType;
	private double qty;
	private double progressIndicator;
	private int orderStatus;
	private int isActive;
	private FoodOrder order;
	private FoodItem item;
	private Food food;
	
	public static KitchenOrder retrieveOrderExisting(long orid, long itemId, int foodId) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		KitchenOrder kitchen = null;
		String sql = "SELECT * FROM kitchenorder WHERE isactivek=1 AND orid=" + orid + " AND ftid=" + itemId + " AND fid=" + foodId;
		
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
		
			rs = ps.executeQuery();
			
			while(rs.next()){
			kitchen = KitchenOrder.builder()
					.id(rs.getLong("kid"))
					.date(rs.getString("datekit"))
					.time(rs.getString("timekit"))
					.orderNumber(rs.getString("ordernumber"))
					.servingDate(rs.getString("servingdate"))
					.orderType(rs.getInt("ordertype"))
					.qty(rs.getDouble("kqty"))
					.progressIndicator(rs.getDouble("progressindicator"))
					.orderStatus(rs.getInt("orderstatus"))
					.isActive(rs.getInt("isactivek"))
					.order(FoodOrder.builder().id(rs.getLong("orid")).build())
					.item(FoodItem.builder().id(rs.getLong("ftid")).build())
					.food(Food.builder().fid(rs.getInt("fid")).build())
					.build();
			
			
			System.out.println("============================FOUND=========================== orid=" + orid + " ftid=" + itemId + " fid=" + foodId);
			
			}
			
			rs.close();
			ps.close(); 
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
			
			return kitchen;
	}
	
	public static List<KitchenOrder> retrieve(String sqlAdd, String[] params){
		List<KitchenOrder> kitchens = new ArrayList<KitchenOrder>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		
		String sql = "SELECT * FROM kitchenorder k, foodorder o, fooditem i, food f WHERE k.isactivek=1 AND k.orid=o.orid AND k.ftid=i.ftid AND k.fid=f.fid ";
		sql = sql + sqlAdd;
		
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			FoodOrder order = FoodOrder.builder()
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
					.build();
			
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
			
			Food food = Food.builder()
					.fid(rs.getInt("fid"))
					.foodName(rs.getString("foodname"))
					.description(rs.getString("description"))
					.price(rs.getDouble("price"))
					.isActive(rs.getInt("isactivefood"))
					.foodType(rs.getInt("foodtype"))
					.quantity(rs.getDouble("qty"))
					.picture(rs.getString("pic")==null? "" : rs.getString("pic"))
					.build();
			
			KitchenOrder kitchen = KitchenOrder.builder()
					.id(rs.getLong("kid"))
					.date(rs.getString("datekit"))
					.time(rs.getString("timekit"))
					.orderNumber(rs.getString("ordernumber"))
					.servingDate(rs.getString("servingdate"))
					.orderType(rs.getInt("ordertype"))
					.qty(rs.getDouble("kqty"))
					.progressIndicator(rs.getDouble("progressindicator"))
					.orderStatus(rs.getInt("orderstatus"))
					.isActive(rs.getInt("isactivek"))
					.order(order)
					.item(item)
					.food(food)
					.build();
			
			
			kitchens.add(kitchen);
		}
		
		rs.close();
		ps.close(); 
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return kitchens;
		
	}
	
	public static KitchenOrder save(KitchenOrder in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = KitchenOrder.getInfo(in.getId()==0? KitchenOrder.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = KitchenOrder.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = KitchenOrder.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = KitchenOrder.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		KitchenOrder.save(this);
	}
	
	public static KitchenOrder insertData(KitchenOrder in, String type){
		String sql = "INSERT INTO kitchenorder ("
				+ "kid,"
				+ "datekit,"
				+ "timekit,"
				+ "ordernumber,"
				+ "servingdate,"
				+ "ordertype,"
				+ "orid,"
				+ "ftid,"
				+ "fid,"
				+ "kqty,"
				+ "progressindicator,"
				+ "orderstatus,"
				+ "isactivek) " 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table kitchenorder");
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
		ps.setString(cnt++, in.getTime());
		ps.setString(cnt++, in.getOrderNumber());
		ps.setString(cnt++, in.getServingDate());
		ps.setInt(cnt++, in.getOrderType());
		ps.setLong(cnt++, in.getOrder().getId());
		ps.setLong(cnt++, in.getItem().getId());
		ps.setInt(cnt++, in.getFood().getFid());
		ps.setDouble(cnt++, in.getQty());;
		ps.setDouble(cnt++, in.getProgressIndicator());
		ps.setInt(cnt++, in.getOrderStatus());
		ps.setInt(cnt++, in.getIsActive());
		
		
		LogU.add(in.getDate());
		LogU.add(in.getTime());
		LogU.add(in.getOrderNumber());
		LogU.add(in.getServingDate());
		LogU.add(in.getOrderType());
		LogU.add(in.getOrder().getId());
		LogU.add(in.getItem().getId());
		LogU.add(in.getFood().getFid());
		LogU.add(in.getQty());;
		LogU.add(in.getProgressIndicator());
		LogU.add(in.getOrderStatus());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to kitchenorder : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static KitchenOrder updateData(KitchenOrder in){
		String sql = "UPDATE kitchenorder SET "
				+ "datekit=?,"
				+ "timekit=?,"
				+ "ordernumber=?,"
				+ "servingdate=?,"
				+ "ordertype=?,"
				+ "orid=?,"
				+ "ftid=?,"
				+ "fid=?,"
				+ "kqty=?,"
				+ "progressindicator=?,"
				+ "orderstatus=? "
				+ " WHERE kid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table kitchenorder");
		
		
		ps.setString(cnt++, in.getDate());
		ps.setString(cnt++, in.getTime());
		ps.setString(cnt++, in.getOrderNumber());
		ps.setString(cnt++, in.getServingDate());
		ps.setInt(cnt++, in.getOrderType());
		ps.setLong(cnt++, in.getOrder().getId());
		ps.setLong(cnt++, in.getItem().getId());
		ps.setInt(cnt++, in.getFood().getFid());
		ps.setDouble(cnt++, in.getQty());;
		ps.setDouble(cnt++, in.getProgressIndicator());
		ps.setInt(cnt++, in.getOrderStatus());
		ps.setLong(cnt++, in.getId());
		
		
		LogU.add(in.getDate());
		LogU.add(in.getTime());
		LogU.add(in.getOrderNumber());
		LogU.add(in.getServingDate());
		LogU.add(in.getOrderType());
		LogU.add(in.getOrder().getId());
		LogU.add(in.getItem().getId());
		LogU.add(in.getFood().getFid());
		LogU.add(in.getQty());;
		LogU.add(in.getProgressIndicator());
		LogU.add(in.getOrderStatus());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to kitchenorder : " + s.getMessage());
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
		sql="SELECT kid FROM kitchenorder  ORDER BY kid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT kid FROM kitchenorder WHERE kid=?");
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
		String sql = "UPDATE kitchenorder set isactivek=0 WHERE kid=" + idx;
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
		String sql = "UPDATE kitchenorder set isactivek=0 WHERE kid=?";
		
		if(!retain){
			sql = "DELETE FROM kitchenorder WHERE kid=?";
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
