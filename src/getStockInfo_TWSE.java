
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * http://mis.tse.com.tw/stock/api/getStock.jsp?ch=1597.tw&json=1&_=
 *
 * Copyright (C) 2018 Phelps Ke, phelpske.dev at gmail dot com
 *  
 * Last revision: March 22, 2018 01:04 AM
 */

public class getStockInfo_TWSE 
{
	private final String USER_AGENT = "Mozilla/5.0";
	
	public getStockInfo_TWSE(String code) throws Exception

	{
		
		TWSE_data(code);
		
	}
	
	private void TWSE_data(String code) throws Exception
	{
		String url = "http://mis.tse.com.tw/stock/api/getStock.jsp?ch=" + code +".tw&json=1&_=";		
		System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) 
		{
			System.out.println(inputLine);
			response.append(inputLine);			
		}
		in.close();	
		String result_str = response.toString().substring(3, response.toString().length());
	}
	
	public static void main(String args[])
	{
		String Stock_code = "1597";
		String Stock_name = "直德";
		
		try {
			//getStockInfo get = new getStockInfo(Stock_code);
			getStockInfo_TWSE get_twse = new getStockInfo_TWSE(Stock_code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
