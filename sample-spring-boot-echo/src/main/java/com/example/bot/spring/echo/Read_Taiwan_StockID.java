import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Read_Taiwan_StockID 
{
	// Pattern expression
	private Pattern p;
	private Matcher m;
	private String space_pattern = "^[0-9]";	
		
	public ArrayList<String> code = new ArrayList<String>();
	public ArrayList<String> name = new ArrayList<String>();
		
	public Read_Taiwan_StockID() throws Exception
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
	
	private void boolean Pattern_expression_digital(String input)
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
    
    private void void Separation(String input)
	{
		String code_temp = input.substring(0, 4);
		String name_temp = input.substring(5, input.length());
		
		code.add(code_temp);
		name.add(name_temp);
	}
    
    public ArrayList Return_name()
    {    	
    	return name;
    }
	
    public ArrayList Return_code()
    {
    	return code;
    }
    
}
