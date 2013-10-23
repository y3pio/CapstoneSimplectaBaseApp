package com.example.simplectarssreader;

public class ParsedFeed {
	private String url;				//Url of the rss feed
	private String title;			//Title of it
	private String description;		//Which rss subscription is found under
	
	protected ParsedFeed(String u, String t, String d){
		url = u;
		title = t;
		description = d;
	}
	
	public String getUrl(){
		return url;
	}
	public String getTitle(){
		return title;
	}
	public String getDescription(){
		return description;
	}
}
