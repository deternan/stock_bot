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

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


@SpringBootApplication
@LineMessageHandler
public class EchoApplication 
{
	
	static ArrayList<String> code = new ArrayList<String>();
	static ArrayList<String> name = new ArrayList<String>();
	
    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception{
        
    	Read_Taiwan_StockID(); 
    	
    	
    	System.out.println("event: " + event);
    	return new TextMessage(event.getMessage().getText()+"	"+code.size()+"	"+name.size());
        //return new TextMessage(event.getMessage().getText());
    	
    	//System.out.println("event: " + event);    	
//    	String get_return = "";
//    	String get_stockname = "";
//		String getstockcode = "";
//		// Digital check        
//        digital_check = Regular_Expression_Digital(event.getMessage().getText());
//        if(digital_check == true){
//        	if(event.getMessage().getText().length() == 4){
//        		//get_return = "4 digital";        		
//        		get_stockname = "";        	
//            	get_stockname = Return_name(event.getMessage().getText());
//            	if(get_stockname.length() > 0){
//            		
//            		// Google 
////            		Google_data(event.getMessage().getText());
//            		
//            		return new TextMessage(get_stockname);
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
//        	getstockcode = "";
//        	if(event.getMessage().getText().length() > 0){
//        		getstockcode = Return_code(event.getMessage().getText());
//        		
//        		// Google
////        		Google_data(getstockcode);
//        		
//        		return new TextMessage(getstockcode);
//        	}else{
//        		get_return = "illegal"; 
//        		return new TextMessage(get_return);
//        	}
//        	
//        	//Google_data(event.getMessage().getText());
//        }
        
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

}
