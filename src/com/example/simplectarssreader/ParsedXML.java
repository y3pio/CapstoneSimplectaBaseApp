package com.example.simplectarssreader;

public class ParsedXML {
	String link, desc, title, category;
	
	protected ParsedXML(String a, String b, String c, String d){
		link = a;
		desc = b;
		title = c;
		category = d;
	}
	
	public String getLink(){
		return link;
	}
	public String getTitle(){
		return title;
	}
	public String getDesc(){
		return desc;
	}
	public String getCategory(){
		return category;
	}

}

