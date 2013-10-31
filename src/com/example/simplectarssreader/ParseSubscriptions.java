package com.example.simplectarssreader;

public class ParseSubscriptions {
	protected String url;
	protected String title;
	protected String unsubscribe;
	
	protected ParseSubscriptions(String u, String t, String s)
	{
		url = u;
		title = t;
		unsubscribe = s;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getSub()
	{
		return unsubscribe;
	}
}
