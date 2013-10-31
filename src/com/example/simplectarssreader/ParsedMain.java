package com.example.simplectarssreader;

public class ParsedMain {
	
	String url, urlKey, desc, category, rssUrl;
	
	protected ParsedMain(String a, String b, String c, String d, String e){
		url = a;
		urlKey = b;
		desc = c;
		category = d;
		rssUrl = e;
	}
	
	public String getURL(){
		return url;
	}
	public String getURLKey(){
		return urlKey;
	}
	public String getDesc(){
		return desc;
	}
	public String getCategory(){
		return category;
	}
	public String getRSSURL(){
		return rssUrl;
	}
	
}

