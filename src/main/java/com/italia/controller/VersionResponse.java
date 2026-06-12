package com.italia.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.PulseStatus;

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
public class VersionResponse {
	private long id;
	private String dateRelease;
	private int versionCode;
    private String versionSeries;
    private String downloadUrl;
    private String releaseNotes;
    private long fileSize;
    private int isActive;
    private boolean isRequired;
    
    
    public static VersionResponse retriveAppLatest(String platform){
    	VersionResponse ver = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String sql = "SELECT * FROM app_version WHERE isactivev=1 AND platform='"+ platform +"' ORDER BY apid DESC LIMIT 1";
		
		
		try{
			conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
			ps = conn.prepareStatement(sql);
	
			System.out.println("version: " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				ver = builder()
					.id(rs.getLong("apid"))
					.dateRelease(rs.getString("daterelease"))
					.versionCode(rs.getInt("versioncode"))
					.versionSeries(rs.getString("versionseries"))
					.releaseNotes(rs.getString("releasenotes"))
					.fileSize(rs.getLong("filesize"))
					.isActive(rs.getInt("isactivev"))
						.build();
				
			}
			
		
			rs.close();
			ps.close();
			DBConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return ver;
	}
    
}
