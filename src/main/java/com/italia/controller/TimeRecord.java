package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class TimeRecord {

	private long id;
	private String dateTrans;
	private String time1;
	private String time2;
	private String time3;
	private String time4;
	private String time5;
	private String time6;
	private long eid;
	private String fullname;
	private int iscompleted;
	private int hasupdate;
	
	public static String checkAndChangeStartDutyIfPossible(long employeeId, String date, String actualDutyStart) {
		
		String startTimeOfDuty=null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT timestart FROM staffsched WHERE isactivestaff=1 AND dayin='"+ date +"' AND eid=" + employeeId);
		rs = ps.executeQuery();
		while(rs.next()){
			startTimeOfDuty = rs.getString("timestart");
			System.out.println("Found duty: " + startTimeOfDuty);
		}
		
		if(startTimeOfDuty==null) {
			//get the last known duty
			rs.close();
			ps.close();
			DBConnect.close(conn);
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement("SELECT timestart FROM staffsched WHERE isactivestaff=1 AND  eid=" + employeeId + " ORDER BY sid DESC LIMIT 1");
			rs = ps.executeQuery();
			while(rs.next()){
				startTimeOfDuty = rs.getString("timestart");
				System.out.println("Last know time: " + startTimeOfDuty);
			}
		}
		
		if(startTimeOfDuty==null) {
			System.out.println("Still no know time");
			startTimeOfDuty=actualDutyStart;
		}
		
		double dutyHH = Double.valueOf(startTimeOfDuty.split("\\.")[0]);
		double dutyMM = Double.valueOf(startTimeOfDuty.split("\\.")[1]);
		
		System.out.println("dutyHH:"+dutyHH    +   "  :  dutyMM:"  + dutyMM);
		
		double actualHH = Double.valueOf(actualDutyStart.split(":")[0]);
		double actualMM = Double.valueOf(actualDutyStart.split(":")[1]);
		
		dutyHH = dutyHH * 60;
		dutyHH += dutyMM;
		
		actualHH = actualHH * 60;
		actualHH += actualMM;
		
		if(actualHH>dutyHH) {
			startTimeOfDuty = actualDutyStart;
		}else {
			Integer duty_HH = Integer.valueOf(startTimeOfDuty.split("\\.")[0]);
			Integer duty_MM = Integer.valueOf(startTimeOfDuty.split("\\.")[1]);
			String hh = "00";
			String mm = "00";
			if(duty_HH<10) {
				hh="0" +duty_HH; 
			}
			if(duty_MM<10) {
				mm="0" + duty_MM;
			}
			startTimeOfDuty = hh+":"+mm;
			
			
			System.out.println("Result: " + startTimeOfDuty);
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return startTimeOfDuty;
	}
	
	public static List<TimeRecord> dutyNow(String date){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<TimeRecord> times = new ArrayList<TimeRecord>();
		String sql = "SELECT tt.time1,ee.fullname, (SELECT dd.departmentname FROM department dd WHERE "
				+ "dd.departmentid=ee.departmentid) as department FROM timerecord tt, employees ee "
				+ "WHERE tt.isactivet=1 AND tt.eid=ee.eid AND tt.dateTrans='"+ date+"'"
						+ " ORDER BY department";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			System.out.println("times: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				TimeRecord serverTime = builder()
						.id(0)
						.dateTrans("")
						.time1(replaceAsteriskToColon(rs.getString("time1")))
						.time2(rs.getString("department"))
						.time3(rs.getString("fullname").toUpperCase())
						.time4("")
						.time5("")
						.time6("")
						.eid(0)
						.iscompleted(0)
						.hasupdate(0)
						.build();
				
				times.add(serverTime);
			}
			rs.close();
			ps.close();
			DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		return times;
	}
	
	
	public static String replaceAsteriskToColon(String val) {
		if(val==null) return "";
		return val.replace("*", ":");
	}
	
	public static TimeRecord checkAddedTime(TimeRecord client) {
		
		String currentTime = new SimpleDateFormat("HH:mm").format(new Date()); 
		
		System.out.println("checking for saving....");
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		TimeRecord serverTime = new TimeRecord();
		String sql = "SELECT * FROM timerecord WHERE isactivet=1 AND dateTrans='"+ client.getDateTrans() +"' AND eid=" + client.getEid();
		boolean found = false;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				 		serverTime = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(replaceAsteriskToColon(rs.getString("time1")))
						.time2(replaceAsteriskToColon(rs.getString("time2")))
						.time3(replaceAsteriskToColon(rs.getString("time3")))
						.time4(replaceAsteriskToColon(rs.getString("time4")))
						.time5(replaceAsteriskToColon(rs.getString("time5")))
						.time6(replaceAsteriskToColon(rs.getString("time6")))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.hasupdate(rs.getInt("hasupdate"))
						.build();
				found=true;
			}
			rs.close();
			ps.close();
			DBConnect.close(conn);
			
			
			if(found) {
				System.out.println("found record...");
				//step 1 checking if time to record is empty
				if(serverTime.getTime1().isEmpty()) {
					
					if(client.getTime1().isEmpty()) {
						serverTime.setTime1(currentTime);
					}else {
						serverTime.setTime1(client.getTime1());
					}
					
					
					System.out.println("time 1 is empty..." + client.getTime1());
				}else  if(serverTime.getTime2().isEmpty()) {
					//serverTime.setTime2(record.getTime2());
					if(client.getTime2().isEmpty()) {
						serverTime.setTime2(currentTime);
					}else {
						serverTime.setTime2(client.getTime2());
					}
					System.out.println("time 2 is empty..." + client.getTime2());
				}else if(serverTime.getTime3().isEmpty()) {
					if(client.getTime3().isEmpty()) {
						serverTime.setTime3(currentTime);
					}else {
						serverTime.setTime3(client.getTime3());
					}
					//serverTime.setTime3(record.getTime3());
					System.out.println("time 3 is empty..." + client.getTime3());
				}else  if(serverTime.getTime4().isEmpty()) {
					if(client.getTime4().isEmpty()) {
						serverTime.setTime4(currentTime);
					}else {
						serverTime.setTime4(client.getTime4());
					}
					//serverTime.setTime4(record.getTime4());
					System.out.println("time 4 is empty..." + client.getTime4());
				}else if(serverTime.getTime5().isEmpty()) {
					if(client.getTime5().isEmpty()) {
						serverTime.setTime5(currentTime);
					}else {
						serverTime.setTime5(client.getTime5());
					}
					//serverTime.setTime5(record.getTime5());
					System.out.println("time 5 is empty..." + client.getTime5());
				}else if(serverTime.getTime6().isEmpty()) {
					if(client.getTime6().isEmpty()) {
						serverTime.setTime6(currentTime);
					}else {
						serverTime.setTime6(client.getTime6());
					}
					//serverTime.setTime6(record.getTime6());
					System.out.println("time 6 is empty..." + client.getTime6());
				}else {
					System.out.println("Time 6 is not empty server: "+ serverTime.getTime6() +" client: " + client.getTime6());
					if(!client.getTime6().isEmpty()) {//override server time
						System.out.println("override time6: " + client.getTime6());
						if(client.getTime6().isEmpty()) {
							serverTime.setTime6(currentTime);
							System.out.println("server time: " + currentTime);
						}else {
							serverTime.setTime6(client.getTime6());
							System.out.println("client time: " + client.getTime6());
						}
						//serverTime.setTime6(record.getTime6());
					}
				}
				
				//step2 checking if inputted time same with previous time
				if(serverTime.getTime1().equalsIgnoreCase(client.getTime2())) {
					serverTime.setTime2("");
					System.out.println("time 1 and time 2 is the same...");
				}
				
				//step2 checking if inputted time same with previous time
				if(serverTime.getTime2().equalsIgnoreCase(client.getTime3())) {
					serverTime.setTime3("");
					System.out.println("time 2 and time 3 is the same...");
				}
				
				//step2 checking if inputted time same with previous time
				if(serverTime.getTime3().equalsIgnoreCase(client.getTime4())) {
					serverTime.setTime4("");
					System.out.println("time 3 and time 4 is the same...");
				}
				
				//step2 checking if inputted time same with previous time
				if(serverTime.getTime4().equalsIgnoreCase(client.getTime5())) {
					serverTime.setTime5("");
					System.out.println("time 4 and time 5 is the same...");
				}
				
				//step2 checking if inputted time same with previous time
				if(serverTime.getTime5().equalsIgnoreCase(client.getTime6())) {
					serverTime.setTime6("");
					System.out.println("time 5 and time 6 is the same...");
				}
				
				
				LogU.open(true, GlobalVar.LOG_FOLDER);
				updateData(serverTime);
				LogU.close();
			}else {
				serverTime = save(client);
			}
			
		}catch(Exception e){e.getMessage();}
		return serverTime;
	}
	
public static TimeRecord recordTime(int eid, String date, String time) {
		
		TimeRecord client = TimeRecord.builder()
				.dateTrans(date)
				.eid(eid)
				.time1(time)
				.iscompleted(0)
				.hasupdate(0)
				.build();
	
		System.out.println("Record time saving....");
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		TimeRecord serverTime = new TimeRecord();
		String sql = "SELECT * FROM timerecord WHERE isactivet=1 AND dateTrans='"+ date +"' AND eid=" + eid;
		boolean found = false;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				 		serverTime = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(rs.getString("time1"))
						.time2(rs.getString("time2"))
						.time3(rs.getString("time3"))
						.time4(rs.getString("time4"))
						.time5(rs.getString("time5"))
						.time6(rs.getString("time6"))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.hasupdate(rs.getInt("hasupdate"))
						.build();
				found=true;
			}
			rs.close();
			ps.close();
			DBConnect.close(conn);
			
			
			if(found) {
				
				boolean existing = false;
				if(serverTime.getTime1()!=null && serverTime.getTime1().equalsIgnoreCase(time)) {
					existing = true;
				}else if(serverTime.getTime2()!=null && serverTime.getTime2().equalsIgnoreCase(time)) {
					existing = true;
				}else if(serverTime.getTime3()!=null && serverTime.getTime3().equalsIgnoreCase(time)) {
					existing = true;
				}else if(serverTime.getTime4() !=null && serverTime.getTime4().equalsIgnoreCase(time)) {
					existing = true;
				}else if(serverTime.getTime5()!=null && serverTime.getTime5().equalsIgnoreCase(time)) {
					existing = true;
				}
				
				
				System.out.println("found record...");
				if(!existing) {
					if(serverTime.getTime1()==null || serverTime.getTime1().isEmpty()) {
						serverTime.setTime1(time);
					}else  if(serverTime.getTime2()==null || serverTime.getTime2().isEmpty()) {
						serverTime.setTime2(time);
					}else if(serverTime.getTime3()==null || serverTime.getTime3().isEmpty()) {
						serverTime.setTime3(time);
					}else  if(serverTime.getTime4()==null || serverTime.getTime4().isEmpty()) {
						serverTime.setTime4(time);
					}else if(serverTime.getTime5()==null || serverTime.getTime5().isEmpty()) {
						serverTime.setTime5(time);
					}else {
						serverTime.setTime6(time);
					}
					LogU.open(true, GlobalVar.LOG_FOLDER);
					updateData(serverTime);
					LogU.close();
				}
			}else {
				serverTime = save(client);
			}
			
		}catch(Exception e){e.getMessage();}
		return serverTime;
	}
	
	
	public static List<TimeRecord> getAllTime(){
		List<TimeRecord> times = new ArrayList<TimeRecord>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM timerecord WHERE isactivet=1";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				TimeRecord e = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(TimeRecord.replaceAsteriskToColon(rs.getString("time1")))
						.time2(TimeRecord.replaceAsteriskToColon(rs.getString("time2")))
						.time3(TimeRecord.replaceAsteriskToColon(rs.getString("time3")))
						.time4(TimeRecord.replaceAsteriskToColon(rs.getString("time4")))
						.time5(TimeRecord.replaceAsteriskToColon(rs.getString("time5")))
						.time6(TimeRecord.replaceAsteriskToColon(rs.getString("time6")))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.build();
				
				times.add(e);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return times;
	}
	
	public static List<TimeRecord> getUnprocessedDTR(){
		List<TimeRecord> times = new ArrayList<TimeRecord>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM timerecord t, employees e WHERE t.isactivet=1 AND t.iscompleted=0 AND t.eid=e.eid ORDER BY e.fullname";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				TimeRecord e = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(TimeRecord.replaceAsteriskToColon(rs.getString("time1")))
						.time2(TimeRecord.replaceAsteriskToColon(rs.getString("time2")))
						.time3(TimeRecord.replaceAsteriskToColon(rs.getString("time3")))
						.time4(TimeRecord.replaceAsteriskToColon(rs.getString("time4")))
						.time5(TimeRecord.replaceAsteriskToColon(rs.getString("time5")))
						.time6(TimeRecord.replaceAsteriskToColon(rs.getString("time6")))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.fullname(rs.getString("fullname"))
						.build();
				
				times.add(e);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return times;
	}
	
	public static List<TimeRecord> getByDate(String date){
		List<TimeRecord> times = new ArrayList<TimeRecord>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM timerecord t, employees e WHERE t.isactivet=1 AND t.iscompleted=0 AND t.eid=e.eid AND t.dateTrans='"+ date +"'";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				TimeRecord e = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(TimeRecord.replaceAsteriskToColon(rs.getString("time1")))
						.time2(TimeRecord.replaceAsteriskToColon(rs.getString("time2")))
						.time3(TimeRecord.replaceAsteriskToColon(rs.getString("time3")))
						.time4(TimeRecord.replaceAsteriskToColon(rs.getString("time4")))
						.time5(TimeRecord.replaceAsteriskToColon(rs.getString("time5")))
						.time6(TimeRecord.replaceAsteriskToColon(rs.getString("time6")))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.fullname(rs.getString("fullname"))
						.build();
				
				times.add(e);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return times;
	}
	
	public static List<TimeRecord> getUnprocessedDTREmployee(long eid){
		List<TimeRecord> times = new ArrayList<TimeRecord>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM timerecord t, employees e WHERE t.isactivet=1 AND t.iscompleted=0 AND t.eid=e.eid AND e.eid=" + eid;
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				TimeRecord e = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(TimeRecord.replaceAsteriskToColon(rs.getString("time1")))
						.time2(TimeRecord.replaceAsteriskToColon(rs.getString("time2")))
						.time3(TimeRecord.replaceAsteriskToColon(rs.getString("time3")))
						.time4(TimeRecord.replaceAsteriskToColon(rs.getString("time4")))
						.time5(TimeRecord.replaceAsteriskToColon(rs.getString("time5")))
						.time6(TimeRecord.replaceAsteriskToColon(rs.getString("time6")))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.fullname(rs.getString("fullname"))
						.build();
				
				times.add(e);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return times;
	}
	
	public static List<TimeRecord> getUnprocessedDTREmployeePerDay(long eid, String date){
		List<TimeRecord> times = new ArrayList<TimeRecord>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String[] params = new String[0];
		String sql = "SELECT * FROM timerecord t, employees e WHERE t.isactivet=1 AND t.iscompleted=0 AND t.eid=e.eid AND e.eid=" + eid + " AND t.dateTrans='"+ date +"'";
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("timerecord: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				TimeRecord e = builder()
						.id(rs.getLong("id"))
						.dateTrans(rs.getString("dateTrans"))
						.time1(TimeRecord.replaceAsteriskToColon(rs.getString("time1")))
						.time2(TimeRecord.replaceAsteriskToColon(rs.getString("time2")))
						.time3(TimeRecord.replaceAsteriskToColon(rs.getString("time3")))
						.time4(TimeRecord.replaceAsteriskToColon(rs.getString("time4")))
						.time5(TimeRecord.replaceAsteriskToColon(rs.getString("time5")))
						.time6(TimeRecord.replaceAsteriskToColon(rs.getString("time6")))
						.eid(rs.getLong("eid"))
						.iscompleted(rs.getInt("iscompleted"))
						.fullname(rs.getString("fullname"))
						.build();
				
				times.add(e);
			}
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return times;
	}
	
	public static TimeRecord save(TimeRecord in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = TimeRecord.getInfo(in.getId()==0? TimeRecord.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = TimeRecord.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = TimeRecord.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = TimeRecord.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = getInfo(getId()==0? getLatestId()+1 : getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				TimeRecord.insertData(this,"1");
			}else if(id==2){
				LogU.add("update Data ");
				TimeRecord.updateData(this);
			}else if(id==3){
				LogU.add("added new Data ");
				TimeRecord.insertData(this,"3");
			}
			LogU.close();
	}
	
	public static TimeRecord insertData(TimeRecord in, String type){
		String sql = "INSERT INTO timerecord ("
				+ "id,"
				+ "dateTrans,"
				+ "time1,"
				+ "time2,"
				+ "time3,"
				+ "time4,"
				+ "time5,"
				+ "time6,"
				+ "eid,"
				+ "iscompleted,"
				+ "isactivet,"
				+ "hasupdate)" 
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table timerecord");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			in.setId(id);
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			in.setId(id);
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setString(cnt++, in.getTime1());
		ps.setString(cnt++, in.getTime2());
		ps.setString(cnt++, in.getTime3());
		ps.setString(cnt++, in.getTime4());
		ps.setString(cnt++, in.getTime5());
		ps.setString(cnt++, in.getTime6());
		ps.setLong(cnt++, in.getEid());
		ps.setInt(cnt++, in.getIscompleted());
		ps.setInt(cnt++, 1);
		ps.setInt(cnt++, 0);
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getTime1());
		LogU.add(in.getTime2());
		LogU.add(in.getTime3());
		LogU.add(in.getTime4());
		LogU.add(in.getTime5());
		LogU.add(in.getTime6());
		LogU.add(in.getEid());
		LogU.add(in.getIscompleted());
		LogU.add(1);
		LogU.add(0);
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to timerecord : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	
	
	public static TimeRecord updateData(TimeRecord in){
		String sql = "UPDATE timerecord SET "
				+ "dateTrans=?,"
				+ "time1=?,"
				+ "time2=?,"
				+ "time3=?,"
				+ "time4=?,"
				+ "time5=?,"
				+ "time6=?,"
				+ "eid=?,"
				+ "iscompleted=?,"
				+ "hasupdate=?"
				+ " WHERE id=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table timerecord");
		
		
		ps.setString(cnt++, in.getDateTrans());
		ps.setString(cnt++, in.getTime1());
		ps.setString(cnt++, in.getTime2());
		ps.setString(cnt++, in.getTime3());
		ps.setString(cnt++, in.getTime4());
		ps.setString(cnt++, in.getTime5());
		ps.setString(cnt++, in.getTime6());
		ps.setLong(cnt++, in.getEid());
		ps.setInt(cnt++, in.getIscompleted());
		ps.setInt(cnt++, 0);
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateTrans());
		LogU.add(in.getTime1());
		LogU.add(in.getTime2());
		LogU.add(in.getTime3());
		LogU.add(in.getTime4());
		LogU.add(in.getTime5());
		LogU.add(in.getTime6());
		LogU.add(in.getEid());
		LogU.add(in.getIscompleted());
		LogU.add(0);
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to timerecord : " + s.getMessage());
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
		sql="SELECT id FROM timerecord ORDER BY id DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("id");
		}
		
		rs.close();
		prep.close();
		DBConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static int getInfo(long id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestId();	
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
		ps = conn.prepareStatement("SELECT id FROM timerecord WHERE id=?");
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
	
	public static boolean delete(int idx){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE timerecord set isactivet=0 WHERE id=" + idx;
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE timerecord set isactivet=0 WHERE id=?";
		
		if(!retain){
			sql = "DELETE FROM food WHERE fid=?";
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
