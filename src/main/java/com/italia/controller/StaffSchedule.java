package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.utils.DateUtils;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StaffSchedule {

	private String name;
	private int eid;
	private int month;
	private int year;
	private String f1;
	private String f2;
	private String f3;
	private String f4;
	private String f5;
	private String f6;
	private String f7;
	private String f8;
	private String f9;
	private String f10;
	private String f11;
	private String f12;
	private String f13;
	private String f14;
	private String f15;
	private String f16;
	private String f17;
	private String f18;
	private String f19;
	private String f20;
	private String f21;
	private String f22;
	private String f23;
	private String f24;
	private String f25;
	private String f26;
	private String f27;
	private String f28;
	private String f29;
	private String f30;
	private String f31;
	
	public static boolean checkExistingData(int eid, int year, int month, int  day) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement("SELECT * FROM staffsched WHERE isactivestaff=1 AND year(dayin)=" + year + " AND month(dayin)=" + month + " AND day(dayin)=" + day + " AND eid=" + eid);
			System.out.println("times: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				return true;
			}
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		return false;
	}
	
	public static List<StaffSchedule> getDuty(int year,int month){
		List<StaffSchedule> dutys = new ArrayList<StaffSchedule>();
		
		String names[] = {"Mark Italia", "Matt Alvin", "Marilyn Cachuela"};
		
		
		String sql = "SELECT day(aa.dayin) as day,aa.eid,  a.fullname as employee,aa.timestart FROM staffsched aa, employees a WHERE aa.isactivestaff=1 AND year(aa.dayin)=" + year + " AND month(aa.dayin)=" + month + " AND aa.eid=a.eid ORDER BY a.fullname";
		
		Map<Integer, Map<Integer, String>> mapStaff = new LinkedHashMap<Integer, Map<Integer, String>>();
		Map<Integer, String> mapDay = new LinkedHashMap<Integer, String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Map<Integer, String> mapNames = new LinkedHashMap<Integer, String>();
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				int day = rs.getInt("day");
				int eid = rs.getInt("eid");
				String val = rs.getString("timestart");
				mapNames.put(eid, rs.getString("employee"));
				if(mapStaff!=null && mapStaff.size()>0) {
					
					if(mapStaff.containsKey(eid)) {
						mapStaff.get(eid).put(day, val);
					}else {
						mapDay = new LinkedHashMap<Integer, String>();
						mapDay.put(day, val);
						mapStaff.put(eid, mapDay);
					}
					
				}else {
					mapDay.put(day, val);
					mapStaff.put(eid, mapDay);
				}
				
			}
				
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		if(mapStaff!=null && mapStaff.size()>0) {
			for(int eid : mapStaff.keySet()) {
				StaffSchedule st = new StaffSchedule();
				String name = mapNames.get(eid);
				st.setName(name.toUpperCase());
				st.setMonth(month);
				st.setYear(year);
				st.setEid(eid);
				int num = 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF1(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF1("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF2(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF2("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF3(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF3("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF4(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF4("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF5(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF5("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF6(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF6("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF7(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF7("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF8(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF8("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF9(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF9("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF10(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF10("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF11(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF11("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF12(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF12("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF13(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF13("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF14(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF14("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF15(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF15("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF16(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF16("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF17(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF17("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF18(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF18("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF19(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF19("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF20(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF20("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF21(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF21("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF22(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF22("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF23(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF23("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF24(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF24("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF25(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF25("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF26(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF26("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF27(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF27("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF28(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF28("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF29(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF29("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF30(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF30("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF31(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF31("");
				}
				
				dutys.add(st);
			}
		}
		
		
		
		/*
		 * for(String name : names) { StaffSchedule st = builder() .name(name)
		 * .month(month) .year(year) .f1("1*10:00") .f2("2*09:00") .f3("3*07:30")
		 * .f4("4*10:00") .f5("5*10:00") .f6("") .f7("7*10:00") .f8("8*12:00")
		 * .f9("9*10:00") .f10("11*10:00") .f11("") .f12("12*10:00") .f13("13*08:00")
		 * .f14("14*09:00") .f15("15*10:00") .f16("16*10:00") .f17("17*10:00")
		 * .f18("18*10:00") .f19("") .f20("") .f21("") .f22("22*10:00") .f23("23*10:00")
		 * .f24("24*10:00") .f25("25*10:00") .f26("") .f27("27*10:00") .f28("28*10:00")
		 * .f29("29*10:00") .f30("") .f31("31*10:00") .build();
		 * 
		 * dutys.add(st); }
		 */
		
		return dutys;
	}
	
	public static List<StaffSchedule> getDuty(String date,int year, int month, int employeeId){
		List<StaffSchedule> dutys = new ArrayList<StaffSchedule>();
		
		String names[] = {"Mark Italia", "Matt Alvin", "Marilyn Cachuela"};
		
		
		String sql = "SELECT day(aa.dayin) as day,aa.eid,  a.fullname as employee,aa.timestart FROM staffsched aa, employees a WHERE aa.isactivestaff=1 AND year(aa.dayin)=" + year + " AND month(aa.dayin)="+ month +" AND aa.eid=" + employeeId + " AND aa.eid=a.eid ORDER BY a.fullname";
		
		Map<Integer, Map<Integer, String>> mapStaff = new LinkedHashMap<Integer, Map<Integer, String>>();
		Map<Integer, String> mapDay = new LinkedHashMap<Integer, String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Map<Integer, String> mapNames = new LinkedHashMap<Integer, String>();
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("Get Duty recorded: " + ps.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				int day = rs.getInt("day");
				int eid = rs.getInt("eid");
				String val = rs.getString("timestart");
				mapNames.put(eid, rs.getString("employee"));
				if(mapStaff!=null && mapStaff.size()>0) {
					
					if(mapStaff.containsKey(eid)) {
						mapStaff.get(eid).put(day, val);
					}else {
						mapDay = new LinkedHashMap<Integer, String>();
						mapDay.put(day, val);
						mapStaff.put(eid, mapDay);
					}
					
				}else {
					mapDay.put(day, val);
					mapStaff.put(eid, mapDay);
				}
				
			}
				
			
			rs.close();
			ps.close();
			DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		if(mapStaff!=null && mapStaff.size()>0) {
			for(int eid : mapStaff.keySet()) {
				StaffSchedule st = new StaffSchedule();
				String name = mapNames.get(eid);
				st.setName(name.toUpperCase());
				st.setMonth(month);
				st.setYear(year);
				st.setEid(eid);
				int num = 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF1(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF1("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF2(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF2("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF3(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF3("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF4(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF4("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF5(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF5("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF6(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF6("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF7(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF7("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF8(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF8("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF9(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF9("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF10(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF10("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF11(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF11("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF12(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF12("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF13(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF13("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF14(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF14("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF15(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF15("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF16(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF16("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF17(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF17("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF18(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF18("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF19(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF19("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF20(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF20("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF21(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF21("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF22(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF22("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF23(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF23("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF24(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF24("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF25(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF25("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF26(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF26("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF27(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF27("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF28(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF28("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF29(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF29("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF30(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF30("");
				}
				
				num += 1;
				
				if(mapStaff.get(eid).containsKey(num)) {
					st.setF31(num+"*"+mapStaff.get(eid).get(num));
				}else {
					st.setF31("");
				}
				
				dutys.add(st);
			}
		}
		
		
		return dutys;
	}
	/**
	 * 
	 * 
	 * @param day = year-month-day*timestart*timeend*department e.g 2024-09-14*10.0*19.0*1
	 */
	@Deprecated
	public static StaffSchedule save(StaffSchedule in, String day, double start, double end, int department){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = StaffSchedule.getInfo(StaffSchedule.getLatestId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = StaffSchedule.insertData(in, "1", day, start, end, department);
			}else if(id==2){
				LogU.add("update Data ");
				in = StaffSchedule.updateData(in, day, start, end, department,  id);
			}else if(id==3){
				LogU.add("added new Data ");
				in = StaffSchedule.insertData(in, "3", day, start, end, department);
			}
			LogU.close();
		}
		return in;
	}
	@Deprecated
	public void save(String day, double start, double end, int department){
		StaffSchedule.save(this, day, start, end, department);
	}
	
	public static StaffSchedule saveAndUpdate(long id, StaffSchedule staff, String type, String day,double start, double end, int department) {
		LogU.open(true, GlobalVar.LOG_FOLDER);
		if("new".equalsIgnoreCase(type)) {
			staff = StaffSchedule.insertData(staff, "3", day, start, end, department);
		}else {
			staff = StaffSchedule.updateData(staff, day, start, end, department, id);
		}
		LogU.close();
		return staff;
	}
	
	public static StaffSchedule insertData(StaffSchedule vr, String type, String day,double start, double end, int department){
		
		String sql = "INSERT INTO staffsched ("
				+ "sid,"
				+ "controlNo,"
				+ "datecreated,"
				+ "dayin,"
				+ "timestart,"
				+ "timeend,"
				+ "staffremarks,"
				+ "isactivestaff,"
				+ "eid,"
				+ "assignment,"
				+ "dayofweek)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table staffsched");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			LogU.add("id: " + id);
		}
		String control = vr.getYear() + "-" + vr.getMonth() + "-" + vr.getEid();
		String created = DateUtils.getCurrentDateYYYYMMDD();
		ps.setString(cnt++, control);
		ps.setString(cnt++, created);
		ps.setString(cnt++, day);
		ps.setDouble(cnt++, start);
		ps.setDouble(cnt++, end);
		ps.setString(cnt++, "");
		ps.setInt(cnt++, 1);
		ps.setLong(cnt++, vr.getEid());
		ps.setInt(cnt++, department);
		ps.setInt(cnt++, 1);
		
		LogU.add(control);
		LogU.add(created);
		LogU.add(day);
		LogU.add(start);
		LogU.add(end);
		LogU.add("");
		LogU.add(1);
		LogU.add(vr.getEid());
		LogU.add(department);		
		LogU.add(1);
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to staffsched : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static StaffSchedule updateData(StaffSchedule vr, String day,double start, double end, int department, long id){
		
		String sql = "UPDATE staffsched SET "
				+ "controlNo=?,"
				+ "datecreated=?,"
				+ "dayin=?,"
				+ "timestart=?,"
				+ "timeend=?,"
				+ "staffremarks=?,"
				+ "eid=?,"
				+ "assignment=?,"
				+ "dayofweek=?,"
				+ "isactivestaff=?" 
				+ " WHERE sid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		
		LogU.add("updating data into table staffsched");
		
		String control = vr.getYear() + "-" + vr.getMonth() + "-" + vr.getEid();
		String created = DateUtils.getCurrentDateYYYYMMDD();
		ps.setString(cnt++, control);
		ps.setString(cnt++, created);
		ps.setString(cnt++, day);
		ps.setDouble(cnt++, start);
		ps.setDouble(cnt++, end);
		ps.setString(cnt++, "");
		ps.setLong(cnt++, vr.getEid());
		ps.setInt(cnt++, department);
		ps.setInt(cnt++, 1);
		ps.setInt(cnt++, 1);
		ps.setLong(cnt++, id);
		
		LogU.add(control);
		LogU.add(created);
		LogU.add(day);
		LogU.add(start);
		LogU.add(end);
		LogU.add("");
		LogU.add(vr.getEid());
		LogU.add(department);		
		LogU.add(1);
		LogU.add(1);
		LogU.add(id);
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updatingg data to staffsched : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public static int getLatestId(String date, int eid){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT sid FROM staffsched WHERE dayin='"+ date +"' AND eid="+ eid;	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		System.out.println("Checking if new : " + sql);
		while(rs.next()){
			id = rs.getInt("sid");
			System.out.println("Found: " + date);
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT sid FROM staffsched  ORDER BY sid DESC LIMIT 1";	
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
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT sid FROM staffsched WHERE sid=?");
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
	
	public void delete(boolean retain, int id){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE staffsched set isactivestaff=0 WHERE sid=?";
		
		if(!retain){
			sql = "DELETE FROM staffsched WHERE sid=?";
		}
		
		String[] params = new String[1];
		params[0] = id+"";
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
