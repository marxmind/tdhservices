package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;

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
public class KitchenCook {

	
	private long kitchenId;
	private long orid;
	private String servingDate;
	private String customerName;
	private String tableName;
	private String notes;
	private String waiterName;
	private List<KitchenOrder> kitchens;
	
	public static List<KitchenCook> retrieve(String dateFrom, String dateTo){
		List<KitchenCook> cooks = new ArrayList<KitchenCook>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Map<Integer, Employee> employee = Employee.getMapAll();
		String sql = "SELECT * FROM kitchenorder k, foodorder o, food f WHERE k.isactivek=1 AND k.orid = o.orid AND k.fid = f.fid AND k.servingdate>='"+ dateFrom +"' AND k.servingdate<='"+ dateTo +"')";
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("form process sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			String empName = "";
			try { empName = employee.get(rs.getInt("waitereid")).getFullname(); }catch(Exception e) {}
			
			KitchenCook cok = KitchenCook.builder()
					.kitchenId(rs.getLong("kid"))
					.orid(rs.getLong("orid"))
					.servingDate(rs.getString("servingdate"))
					.customerName(rs.getString("customername"))
					.tableName(rs.getString("tablename"))
					.notes(rs.getString("notes"))
					.waiterName(empName)
					
					.build();
			
		}
			
		rs.close();
		ps.close(); 
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cooks;
		
	}
	
	
}
