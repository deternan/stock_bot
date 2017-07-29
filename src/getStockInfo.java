
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/*
 * https://github.com/Asoul/tsrtc
 * http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_2892.tw&_=1499173810344
 * 
 * Google API
 * http://kie0723.blogspot.tw/2010/11/google-stock-api.html
 * 
 * Taiwan Stock information
 * http://isin.twse.com.tw/isin/C_public.jsp?strMode=2
 *
 * Copyright (C) 2017 Phelps Ke, phelpske.dev at gmail dot com
 *  
 * Last revision: July 09, 2017 09:22 PM
 * 
 * JAR
 * json-simple-1.1.1.jar
 * httpclient-4.5.1.jar
 * jsoup-1.10.2.jar
 * 
 */

public class getStockInfo 
{
	private final String USER_AGENT = "Mozilla/5.0";	
	// Pattern expression
	private Pattern p;
	private Matcher m;
	private String space_pattern = "^[0-9]";	
	// Stock info
	private Vector code = new Vector();
	private Vector name = new Vector();
	// Output
	JSONParser parser_output = new JSONParser();
	JSONArray array_output;
	JSONObject output_json;
		// Tag 
		private String l_cur;		// 成交價格
		private String ltt;			// 時間
		private String lt;			// 日期時間
		private String c;			// 漲跌
		private String cp;			// 漲跌幅
	
	public getStockInfo(String input_str) throws Exception
	{
		// Get Stock ID
		Read_Taiwan_StockID();
		
		String get_stockname;
		String getstockcode;
		
		if(Pattern_expression_digital(input_str.trim())){
			System.out.println("Name	"+input_str);
			getstockcode = Return_code(input_str);			
			// Get Google finance data
			Google_data(getstockcode);
		}else{	
			System.out.println("code	"+input_str);
			get_stockname = Return_name(input_str.trim());			
			Google_data(input_str);			
		}
		Parser_Json();
	}
	
	private void Google_data(String code) throws Exception
	{
		String url = "http://finance.google.com/finance/info?client=ig&q=TPE:" + code;
		System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			//System.out.println(inputLine);
			response.append(inputLine);			
		}
		in.close();	
		String result_str = response.toString().substring(3, response.toString().length());
		//System.out.println(response.toString().substring(3, response.toString().length()));
		
		
		// String to Json
		String_to_Json(result_str);
	}
	
	private void String_to_Json(String input) throws Exception
	{
		// String to Json
//		JSONParser parser = new JSONParser();
//		JSONObject json = (JSONObject) parser.parse(input);		
//		System.out.println(json);		
		
		// String to JsonArray
		//JSONParser parser_output = new JSONParser();
		array_output = (JSONArray)parser_output.parse(input);
		//System.out.println(((JSONObject)array_output.get(0)));
		output_json = (JSONObject)array_output.get(0);
		//System.out.println(output_json);
	}
	
	private void Parser_Json()
	{
		l_cur = output_json.get("l_cur").toString();
		ltt = output_json.get("ltt").toString();
		lt = output_json.get("lt").toString();
		c = output_json.get("c").toString();
		cp = output_json.get("cp").toString();
		
		System.out.println(l_cur+"	"+ltt+"	"+lt+" "+c+" "+cp);		
	}
	
	private void Read_Taiwan_StockID() throws Exception
	{		
		String url = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
		// JSoup Example 2 - Reading HTML page from URL
		Document doc = Jsoup.connect(url).timeout(5000).get();
		doc.outputSettings().charset("UTF-8");
		
		// body
		Elements iframe_ele = doc.select("body");
		if((iframe_ele.size() > 0)){
			//System.out.println(iframe_ele);
		}
		
		String temp_str;
		
		// div embed
		Elements div_embed_ele = doc.select("tr");
		if ((div_embed_ele.size() > 0)) {
			//System.out.println(div_embed_ele);
		}
		//System.out.println(div_embed_ele.size());
		
		boolean dig_check;
		for(int i=0; i<div_embed_ele.size(); i++)
		{
			//System.out.println(i+"	"+div_embed_ele.get(i));
			//System.out.println(i+"	"+div_embed_ele.get(i).childNodeSize());
			// childNode()
			if(div_embed_ele.get(i).childNodeSize() == 7){
				//System.out.println(i+"	"+div_embed_ele.get(i).child(0));
				temp_str = div_embed_ele.get(i).child(0).toString().substring(22, div_embed_ele.get(i).child(0).toString().indexOf("</td>"));
				dig_check = Pattern_expression_digital(temp_str);
				if(dig_check == true){
//					System.out.println(temp_str);
					Separation(temp_str);
				}				
			}			
		}
	}
	
	private boolean Pattern_expression_digital(String input)
	{
		boolean check = false;
		String temp;
		if(input.trim().length() < 5){
			temp = input.trim();
		}else{
			temp = input.substring(4, 5).trim();
		}
		//System.out.println(temp);
        p = Pattern.compile(space_pattern);
        m = p.matcher(temp);
        if(m.find()){
        	//System.out.println("Not Digital");
        	check = false;
        }else{
        	check = true;
        }
		
		return check;
	}
	
	private void Separation(String input)
	{
		String code_temp = input.substring(0, 4);
		String name_temp = input.substring(5, input.length());
		
		code.add(code_temp);
		name.add(name_temp);
	}
	
	private String Return_name(String code_str)
	{
		String return_name = "";;
		for(int i=0; i<code.size(); i++)
		{
			if(code.get(i).toString().equalsIgnoreCase(code_str)){
				return_name = name.get(i).toString();
				break;
			}
		}
		
		return return_name;
	}
	
	private String Return_code(String name_str)
	{
		String return_code = "";
		for(int i=0; i<name.size(); i++)
		{
			if(name.get(i).toString().equalsIgnoreCase(name_str)){
				return_code = code.get(i).toString();
				break;
			}
		}
		
		return return_code;
	}
	
	public static void main(String args[])
	{
		String Stock_code = "2892";
		String Stock_name = "鴻海";
		
		try {
			//getStockInfo get = new getStockInfo(Stock_code);
			getStockInfo get = new getStockInfo(Stock_name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
