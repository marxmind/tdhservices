package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class Email {

	private long id;
	private String dateSend;
	private long memberSenderId;
	private long memberReceiverId;
	private String subject;
	private String senderName;
	private String receiverName;
	private String msg;
	private int isRead;
	private int isActive;
	
	public static List<Email> retrieve(String sqlAdd, String[] params){
		List<Email> vrs = new ArrayList<Email>();
		
		Map<Integer, Employee> memberData = Employee.getMapAll();
		
		String sql = "SELECT * FROM email WHERE isactiveem=1 ";
		sql = sql + sqlAdd;		
				
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
		System.out.println("email form sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			String sender = memberData.get(rs.getInt("memberSenderId")).getFullname();
			String receiver =memberData.get(rs.getInt("memberReceiverid")).getFullname();
			
			Email email = builder()
					.id(rs.getLong("emid"))
					.dateSend(rs.getString("dateSend"))
					.memberSenderId(rs.getLong("memberSenderId"))
					.memberReceiverId(rs.getLong("memberReceiverid"))
					.subject(rs.getString("subject"))
					.msg(rs.getString("msg"))
					.isRead(rs.getInt("isread"))
					.isActive(rs.getInt("isactiveem"))
					.senderName(sender)
					.receiverName(receiver)
					.build();
			
			System.out.println("sender: " + sender);
			
			vrs.add(email);
			
		}
		
		rs.close();
		ps.close();
		DBConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	
	public static Email save(Email in){
		if(in!=null){
			LogU.open(true, GlobalVar.LOG_FOLDER);
			long id = Email.getInfo(in.getId()==0? Email.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				in = Email.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				in = Email.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				in = Email.insertData(in, "3");
			}
			LogU.close();
		}
		return in;
	}
	
	public void save(){
		Email.save(this);
	}
	
	
	public static Email insertData(Email in, String type){
		String sql = "INSERT INTO email ("
				+ "emid,"
				+ "dateSend,"
				+ "memberSenderId,"
				+ "memberReceiverid,"
				+ "subject,"
				+ "msg,"
				+ "isread,"
				+ "isactiveem) " 
				+ " VALUES(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table email");
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
		
		
		ps.setString(cnt++, in.getDateSend());
		ps.setLong(cnt++, in.getMemberSenderId());
		ps.setLong(cnt++, in.getMemberReceiverId());
		ps.setString(cnt++, in.getSubject());
		ps.setString(cnt++, in.getMsg());
		ps.setInt(cnt++, in.getIsRead());
		ps.setInt(cnt++, in.getIsActive());
		
		LogU.add(in.getDateSend());
		LogU.add(in.getMemberSenderId());
		LogU.add(in.getMemberReceiverId());
		LogU.add(in.getSubject());
		LogU.add(in.getMsg());
		LogU.add(in.getIsRead());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to email : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public static Email updateData(Email in){
		String sql = "UPDATE email SET "
				+ "dateSend=?,"
				+ "memberSenderId=?,"
				+ "memberReceiverid=?,"
				+ "subject=?,"
				+ "msg=?,"
				+ "isread=?" 
				+ " WHERE emid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table email");
		
		
		ps.setString(cnt++, in.getDateSend());
		ps.setLong(cnt++, in.getMemberSenderId());
		ps.setLong(cnt++, in.getMemberReceiverId());
		ps.setString(cnt++, in.getSubject());
		ps.setString(cnt++, in.getMsg());
		ps.setInt(cnt++, in.getIsRead());
		ps.setLong(cnt++, in.getId());
		
		LogU.add(in.getDateSend());
		LogU.add(in.getMemberSenderId());
		LogU.add(in.getMemberReceiverId());
		LogU.add(in.getSubject());
		LogU.add(in.getMsg());
		LogU.add(in.getIsRead());
		LogU.add(in.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		DBConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to email : " + s.getMessage());
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
		sql="SELECT emid FROM email  ORDER BY emid DESC LIMIT 1";	
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("emid");
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
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("SELECT emid FROM email WHERE emid=?");
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE email set isactiveem=0 WHERE emid=?";
		
		if(!retain){
			sql = "DELETE FROM email WHERE emid=?";
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
