package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class Department {

	private int id;
	private String name;
	
	
	public static Map<Integer, Department> loadAllDepartment(){
		Map<Integer, Department> deps = new LinkedHashMap<Integer, Department>();
		
		String sql = "SELECT * FROM department ORDER BY departmentname";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Department dep = Department.builder()
					.id(rs.getInt("departmentid"))
					.name(rs.getString("departmentname"))
					.build();
			deps.put(dep.getId(), dep);
		}
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){}
		
		return deps;
	}
	
	public static List<Department> retrieve(String sql, String[] params){
		List<Department> deps = new ArrayList<Department>();
		String sq = "SELECT * FROM department ";
		sql = sq + sql;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
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
			Department dep = Department.builder()
					.id(rs.getInt("departmentid"))
					.name(rs.getString("departmentname"))
					.build();
			deps.add(dep);
		}
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){}
		
		return deps;
	}
	
	public static Department department(String departmentid){
		Department dep = new Department();
		String sql = " WHERE departmentid=?";
		String[] params = new String[1];
		params[0] = departmentid;
		try{
			dep = Department.retrieve(sql, params).get(0);
		}catch(Exception e){}
		return dep;
	}
	
}
