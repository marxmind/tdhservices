package com.italia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class SendSMS {
	
	
	private static String POST_DATA = "";		
	
	public static String[] sendSMS(String apiKey, String mobileNumber, String message) {
		URL url;
		String[] responseSMS = new String[2];
		responseSMS[0]="ERROR";
		POST_DATA = "apikey="+ apiKey;
		POST_DATA += "&number="+mobileNumber;
		POST_DATA += "&message="+message;
		try {
			url = new URL(GlobalVar.POST_URL);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", GlobalVar.USER_AGENT);
			
			//for post only start
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(POST_DATA.getBytes());
			os.flush();
			os.close();
			//post end
			
			int responseCode = con.getResponseCode();
			System.out.println("Post response code: " + responseCode);
			
			if (responseCode == HttpsURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(response.toString());
				responseSMS[0]="SUCCESS";
				responseSMS[1]=response.toString();
			} else {
				System.out.println("POST request not worked");
				responseSMS[0]="ERROR";
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseSMS[0]="ERROR";
		}
		return responseSMS;
	}
	
	private static void sendGET() throws IOException {
		URL obj = new URL(GlobalVar.GET_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", GlobalVar.USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("GET request not worked");
		}

	}
}