package com.example.simplectarssreader;

public class ParsedXML {
	String itemLink, itemDesc, itemTitle, channelTitle;

	protected ParsedXML(String a, String b, String c, String d){
		itemLink = a;
		itemDesc = b;
		itemTitle = c;
		channelTitle = d;
	}
	
	public String getItemLink(){
		return itemLink;
	}
	public String getItemTitle(){
		return itemTitle;
	}
	public String getItemDesc(){
		return itemDesc;
	}
	public String getChannelTitle(){
		return channelTitle;
	}

}

