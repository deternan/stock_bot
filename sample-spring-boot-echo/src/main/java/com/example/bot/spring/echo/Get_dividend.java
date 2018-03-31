/*
 * 
 * Get stock dividend 
 * Copyright (C) 2018 Phelps Ke, phelpske.dev at gmail dot com
 * 
 * divided information source
 * https://goodinfo.tw/StockInfo
 * 
 * Last revision: March 11, 2018 11:29 PM
 * 
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Get_dividend 
{
	private String stock_url = "https://goodinfo.tw/StockInfo/StockDividendPolicy.asp?STOCK_ID=";
	private String stock_path = "";
	
	String year;
	double cash;
	double divided;
	double value;
	
	// Return
	double cash_r;
	double divided_r;
	
	public Get_dividend(int code, int year_def) throws Exception
	{
		stock_path = stock_url + String.valueOf(code);
		
		// <td width='11%'>
		value(code);
		
		// divided information
		divided_info(code, year_def);			
	}
	
	private void value(int code) throws Exception
	{
		Document doc = Jsoup.connect(stock_path).get();
		Elements tr = doc.select("td[style]");
		
		value = Double.parseDouble(tr.get(14).text());
		//System.out.println(tr.size()+"	"+tr.get(14).text());
	}
	
	private void divided_info(int code, int year_def) throws Exception
	{
		Document doc = Jsoup.connect(stock_path).get();
		Elements tr = doc.select("tr[OnMouseOver]");
		//System.out.println(tr.size()+"	"+code+"	"+name);
		
		// ----------------------------
		// Year
		Elements td = tr.get(0).select("td b");
		year = td.get(0).text().toString();				// fixed
		// record
		Elements td_list = tr.get(0).select("td[title]");
		
		if(td_list.get(2).text().toString().indexOf("-") > 0) {
			cash = -1;
			divided = -1;
		}else {
			cash = Double.parseDouble(td_list.get(2).text().toString());
			divided = Double.parseDouble(td_list.get(5).text().toString());
		}		
		//System.out.println(year+"	"+code+"	"+cash+"	"+divided);
		
		if(Integer.parseInt(year) == year_def) {
			cash_r = cash;
			divided_r = divided;
		}else {
			cash_r = -1;
			divided_r = -1;
		}
	}
	
	public double return_value()
	{
		return value;
	}
	
	public double return_cash()
	{
		return cash_r;
	}
	
	public double return_divided()
	{
		return divided_r;
	}

}
