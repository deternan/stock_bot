
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

 /*
  * Last updated: March 31, 2018 05:14 PM
  *
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


import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


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
	
//	static ArrayList<String> code = new ArrayList<String>();
//	static ArrayList<String> name = new ArrayList<String>();
	
	// Output
	JSONParser parser_output = new JSONParser();
	JSONArray array_output;	
		// Tag 
//		private String l_cur;		// 成交價格
//		private String ltt;			// 時間
//		private String lt;			// 日期時間
//		private String c;			// 漲跌
//		private String cp;			// 漲跌幅

	String code;
	String name;
	String year;
	
	// Function
	Get_dividend div;
	// Return Stock data
	double stock_cash;
	double stock_divided;	
	// Calculate
	double cash;
	double divided;
	
	
    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception
    {
        // 
    	String temp[] = event.getMessage().getText().split("\\s");
    	code = temp[0];
    	year = temp[1];
    	
    	System.out.printl(code+"	"+year);
    	
//    		divided(code, year);
//    		//System.out.println(year+"	"+code+"	"+stock_value+"	"+stock_cash+"	"+stock_divided);
//    		if(stock_cash > 0) {
//    			//System.out.println(year+"	"+code+"	"+cash+"	"+divided);
//    		}else {
//    			System.out.println(year+"	unreleased");
//    		}
//    		
//    		// Calculate
//    		Calculate(stock_value, stock_cash, stock_divided);
    	
    	
    	
    	// Google Data
    	//code.clear();
    	//name.clear();
    	//Read_Taiwan_StockID();    	
    	    	
//    	String get_return = "";
//    	String get_stockname = "";
//		String getstockcode = "";
//		// Digital check        
//        digital_check = Regular_Expression_Digital(event.getMessage().getText());
//        if(digital_check == true){
//        	// Input stock ID
//        	if(event.getMessage().getText().length() == 4){
//        		        		
//        		get_stockname = "";        	
//            	get_stockname = Return_name(event.getMessage().getText());
//            	if(get_stockname.length() > 0){
//            		
//            		String result_txt;
//            		// Google 
//            		result_txt = Google_data(event.getMessage().getText());
//            		
//            		// String to Json 
//            		JSONObject google_json = String_to_Json(result_txt);            		
//            		
//            		return new TextMessage(Display_str);
//            		//return new TextMessage(get_stockname);
//            	}else{
//            		get_return = "illegal";        	
//            		return new TextMessage(get_return);
//            	}        		
//            	
//        	}else{
//        		get_return = "Please input 4 digital code or Stock name";
//        		return new TextMessage(get_return);
//        	}        	
//        }else{
//        	// Input stock name
//        	getstockcode = "";
//        	if(event.getMessage().getText().length() > 0){
//        		getstockcode = Return_code(event.getMessage().getText());
//        		//String result_txt;
//        		// Google finicial
//        		//result_txt = Google_data(getstockcode);
//        		
//        		//return new TextMessage(result_txt);
//        		return new TextMessage(getstockcode);
//        	}else{
//        		get_return = "illegal"; 
//        		return new TextMessage(get_return);
//        	}
//        	        	
//        }
        
    }

    private void divided(int code, int year)
	{
		try {
			div = new Get_dividend(code, year);
			stock_cash = div.return_cash();
			stock_divided = div.return_divided();
			stock_value = div.return_value();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
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
    
    private JSONObject String_to_Json(String input) throws Exception
	{
		// String to JsonArray		
    	String input_json = input.substring(3, input.length());
		array_output = (JSONArray)parser_output.parse(input_json);		
		JSONObject output_json = (JSONObject)array_output.get(0);
		//System.out.println(output_json);
		
		return output_json;
	}
    
}
