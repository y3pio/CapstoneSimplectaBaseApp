package com.example.simplectarssreader;

public class ParseSubscriptions {
	protected String channelLink;
	protected String channelTitle;
	protected String unsubscribe;
	
	protected ParseSubscriptions(String u, String t, String s)
	{
		channelLink = u;
		channelTitle = t;
		unsubscribe = s;
	}
	
	public String getChannelLink()
	{
		return channelLink;
	}
	
	public String getChannelTitle()
	{
		return channelTitle;
	}
	
	public String getChannelUnsubLink()
	{
		return unsubscribe;
	}
}
