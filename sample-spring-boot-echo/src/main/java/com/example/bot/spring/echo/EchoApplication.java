/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


@SpringBootApplication
@LineMessageHandler
public class EchoApplication
{	
	// Google data
	private static final String USER_AGENT = "Mozilla/5.0";
	private boolean digital_check;
	private boolean character_check;
	// Pattern expression
	private static Pattern p;
	private static Matcher m;
	private static String space_pattern = "^[0-9]";	
	
	static ArrayList<String> code = new ArrayList<String>();
	static ArrayList<String> name = new ArrayList<String>();
	
    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception{
        
    	code.clear();
    	name.clear();
    	Read_Taiwan_StockID();
    	
//    	Read_Taiwan_StockID stockid = new Read_Taiwan_StockID();
//    	code = stockid.Return_code();
//    	name = stockid.Return_name();
    	
    	System.out.println("event: " + event);
//    	return new TextMessage(event.getMessage().getText()+"	"+code.size()+"	"+name.size());
//      return new TextMessage(event.getMessage().getText());
    	
    	    	
    	String get_return = "";
    	String get_stockname = "";
		String getstockcode = "";
		// Digital check        
        digital_check = Regular_Expression_Digital(event.getMessage().getText());
        if(digital_check == true){
        	if(event.getMessage().getText().length() == 4){
        		//get_return = "4 digital";        		
        		get_stockname = "";        	
            	get_stockname = Return_name(event.getMessage().getText());
            	if(get_stockname.length() > 0){
            		
            		// Google 
//            		Google_data(event.getMessage().getText());
            		
            		return new TextMessage(get_stockname);
            	}else{
            		get_return = "illegal";        	
            		return new TextMessage(get_return);
            	}        		
            	
        	}else{
        		get_return = "Please input 4 digital code or Stock name";
        		return new TextMessage(get_return);
        	}        	
        }else{
        	getstockcode = "";
        	if(event.getMessage().getText().length() > 0){
        		getstockcode = Return_code(event.getMessage().getText());
        		
        		// Google
//        		Google_data(getstockcode);
        		
        		return new TextMessage(getstockcode);
        	}else{
        		get_return = "illegal"; 
        		return new TextMessage(get_return);
        	}
        	
        	//Google_data(event.getMessage().getText());
        }
        
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
    
    private static void Read_Taiwan_StockID() throws Exception
	{		
		String url = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
		// JSoup Example 2 - Reading HTML page from URL
		Document doc = Jsoup.connect(url).timeout(5000).get();
		doc.outputSettings().charset("UTF-8");
		
		String temp_str;	
		// div embed
		Elements div_embed_ele = doc.select("tr");
		
		boolean dig_check;
		for(int i=0; i<div_embed_ele.size(); i++)
		{
			// childNode()
			if(div_embed_ele.get(i).childNodeSize() == 7){
				//System.out.println(i+"	"+div_embed_ele.get(i).child(0));
				temp_str = div_embed_ele.get(i).child(0).toString().substring(22, div_embed_ele.get(i).child(0).toString().indexOf("</td>"));
				dig_check = Pattern_expression_digital(temp_str);
				if(dig_check == true){
					Separation(temp_str);
				}			
			}	
		}
	}
    
    private static boolean Pattern_expression_digital(String input)
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
    
    private static void Separation(String input)
	{
		String code_temp = input.substring(0, 4);
		String name_temp = input.substring(5, input.length());
		
		code.add(code_temp);
		name.add(name_temp);
	}
    
    private static boolean Regular_Expression_Digital(String input_str)
    {
    	//Regular_Expression
    	String num_pattern = "[0-9]{4}";    	

    	// Number check
    	Pattern p = Pattern.compile(num_pattern);
    	Matcher  m = p.matcher(input_str);
        
    	boolean digital_temp;
    	if(m.find()){        	
    		digital_temp = true;
        }else{
        	digital_temp = false;
        }
    	
    	return digital_temp;
    }
    
    private static String Return_name(String code_str)
	{
		String return_name = "";
		boolean check = false;
		for(int i=0; i<code.size(); i++)
		{
			if(code.get(i).toString().equalsIgnoreCase(code_str)){
				return_name = name.get(i).toString();
				check = true;
				break;
			}
		}
		
		if(check == true){
			return return_name;
		}else{
			return "No name";
		}		
	}

    private static String Return_code(String name_str)
	{
		String return_code = "";
		boolean check = false;
		for(int i=0; i<name.size(); i++)
		{
			if(name.get(i).toString().equalsIgnoreCase(name_str)){
				return_code = code.get(i).toString();
				check = true;
				break;
			}
		}
		
		if(check == true){
			return return_code;
		}else{
			return "No code";
		}		
	}

    private void Google_data(String code)
	{
    	//String url = "http://finance.google.com/finance/info?client=ig&q=TPE:"+ code;
    	String url = "http://finance.google.com/finance/info?client=ig&q=TPE:"+ 2317;
    	
		/*
    	System.out.println(url);
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			//int responseCode = con.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) 
			{
				//response.append(inputLine);
			}
			in.close();
			//String result_str = response.toString().substring(3,response.toString().length());
			//System.out.println(response.toString().substring(3,response.toString().length()));
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		*/

    	/*
    	Document doc = Jsoup.connect("http://finance.google.com/finance/info?client=ig&q=TPE:2317").get();
		//System.out.println(doc);
		Elements body = doc.select("body");
//		String json_str;
//		json_str = body.text().substring(3, body.text().length());
		//System.out.println(json_str);	
    	*/
		
    	/*
    	try{
    		InputStream is = new URL(url).openStream();
    		BufferedReader rd = new BufferedReader(new InputStreamReader(is,"utf-8")); 	//避免中文亂碼問題
            StringBuilder sb = new StringBuilder();
            String inputLine;
            
            is.close();
            
    	}  catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	*/
    	
//    	InputStream is = null;		
//		try {
//			is = new URL(url).openStream();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
//		try {
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is,"utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		} 	
    	
    	
//        BufferedReader rd = new BufferedReader(new InputStreamReader(is,"utf-8"));
//        StringBuilder sb = new StringBuilder();
//        String inputLine;
//        while ((inputLine = rd.readLine()) != null) 
//        {
//			 sb.append(inputLine);
//       	 //System.out.println(inputLine);
//		 }
//        System.out.println(sb.toString());
        
    	URL urla;
		try {
			urla = new URL(url);
			URLConnection conn = urla.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			String inputLine;
//            while ((inputLine = br.readLine()) != null) {
//                    System.out.println(inputLine);
//            }
//            br.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	
    	
		// String to Json
		// String_to_Json(result_str);
	}
    
}
