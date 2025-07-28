package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
@Path("/business")
public class Business {
	
	private String id;
	private String dateTrans;
	private String controlNumber;
	private String type;
	private String businessName;
	private String owner;
	private String location;
	private String category;
	private String remarks;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Business> list(){
		String sqlAdd = " AND bz.isactivebusiness=1 ORDER BY bz.businessname";
		return retrive(sqlAdd);
	}
	
	@GET
	@Path("/graph/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BusinessGraph> loadGraph(@PathParam("param") String param){
		String year=param.split(":")[0];
		String type=param.split(":")[1];
		List<BusinessGraph> graphs = new ArrayList<BusinessGraph>();
		String sql = " select  DATE_FORMAT(datetrans,'%m') as indexx,"+
		"DATE_FORMAT(datetrans,'%M') as month,DATE_FORMAT(datetrans,'%Y') as year,"+
				"typeof,count(*) as total from businesspermit where "+
		"isactivebusiness=1 and (datetrans>='"+year+"-01-01' and datetrans<='"+year+"-12-31') and typeof='"+ type +"'" +
				" group by DATE_FORMAT(datetrans,'%M') order by DATE_FORMAT(datetrans, '%m')";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		System.out.println("Business SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			int index = rs.getInt("indexx") - 1;
			BusinessGraph g = BusinessGraph.builder()
					.id(rs.getString("indexx"))
					.index(index+"")
					.month(rs.getString("month"))
					.type(rs.getString("typeof"))
					.total(rs.getDouble("total")+"")
					.year(rs.getString("year"))
					.build();
			System.out.println("index:"+index);
			graphs.add(g);
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		System.out.println("test calling graphs....");
		return graphs;
	}
	
	@GET
	@Path("{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Business> findAll(@PathParam("param") String param){
		String sqlAdd = " AND bz.isactivebusiness=1 ORDER BY bz.businessname limit 100";
		if(param!=null && !param.isEmpty()) {
			sqlAdd = " AND (bz.businessname like '%"+ param +"%' OR cuz.fullname like '%"+ param +"%' ) AND bz.isactivebusiness=1 ORDER BY bz.businessname LIMIT 1000";
		}
		System.out.println("param:"+param);
		return retrive(sqlAdd);
	}
	
	private static List<Business> retrive(String sqlAdd){
		List<Business> bz = new ArrayList<Business>();
		
		String tableBus="bz";
		String tableCus="cuz";
		
		String sql = "SELECT bz.bid,bz.datetrans,bz.controlno,bz.typeof,bz.businessname,cuz.fullname,bz.businessaddress,bz.businessengage,bz.memotype FROM businesspermit "+ tableBus + ", businesscustomer " + tableCus +" WHERE "
				+ tableBus + ".customerid=" + tableCus + ".customerid ";
				
		
		sql = sql + sqlAdd;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		//System.out.println("Business SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Business bus = Business.builder()
					.id(rs.getString("bid"))
					.dateTrans(rs.getString("datetrans"))
					.controlNumber(rs.getString("controlno"))
					.type(rs.getString("typeof"))
					.businessName(rs.getString("businessname").trim())
					.owner(rs.getString("fullname").trim())
					.location(rs.getString("businessaddress").trim())
					.category(rs.getString("businessengage"))
					.remarks(rs.getString("memotype"))
					.build();
			bz.add(bus);
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		return bz;
	}
	
}
