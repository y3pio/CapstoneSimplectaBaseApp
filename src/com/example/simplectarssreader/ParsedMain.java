package com.example.simplectarssreader;

public class ParsedMain {
	
	String itemLink, itemLinkKey, itemTitle, channelTitle, channelLink;
	Boolean marked;
	
	protected ParsedMain(String a, String b, String c, String d, String e){
		itemLink = a;
		itemLinkKey = b;
		itemTitle= c;
		channelTitle = d;
		channelLink = e;
		marked = false;
	}
	public void setMarked(){
		marked = true;
	}
	public void setUnMarked(){
		marked = false;
	}
	public Boolean getMarked(){
		return marked;
	}
	public String getItemLink(){
		return itemLink;
	}
	public String getItemLinkKey(){
		return itemLinkKey;
	}
	public String getItemTitle(){
		return itemTitle;
	}
	public String getChannelTitle(){
		return channelTitle;
	}
	public String getChannelLink(){
		return channelLink;
	}
	
}

