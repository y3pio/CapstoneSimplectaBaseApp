package com.example.simplectarssreader;

import android.content.Context;
import android.util.Log;

public class Parse {
	final static String TAG = "Parse";
	
	Context context;
	String fromActivity, html;
	
	protected Parse(String htmlstring){
		context = null;
		fromActivity = "";
		html = htmlstring;
		Log.d(TAG, "------------Parse---------");
	}
	
	protected Parse(Context c, String calledFrom, String htmlstring){
		context = c;
		fromActivity = calledFrom;
		html = htmlstring;
		Log.d(TAG, "------------Parse---------");
	}
	
	void YeParse(){
		Log.d(TAG, "YeParse()");
		String url = null, title = null, description = null;
		String lines[] = html.split("\\r?\\n");
		for(int i=0; i<lines.length; i++){
			if(lines[i].contains("a class=\"largefeedlink\"")){
				int start = lines[i].indexOf("href")+8;
				int end = lines[i].indexOf("</a>", start);
				description = lines[i].substring(start, end);
				//System.out.println("Description = " + description);
			}
			else if(lines[i].contains("a class=\"read_link\"")){
				int start = lines[i].indexOf("http");
				int end = lines[i].indexOf(">", start);
				if(start != -1)
					url = lines[i].substring(start, end-1);
				//System.out.println(lines[i]);
				//System.out.println("URL = " + url);
				
				start = lines[i].indexOf(">", start)+1;
				end = lines[i].indexOf("</a>", start);
				title = lines[i].substring(start, end);
				//System.out.println(title);		
				MainActivity.parsedItems.add(new ParsedFeed(url, title, description));
			}	
		}
		Log.d(TAG, "YeParse() done");
	}
	
	void BrianParse(){ 
		Log.d(TAG,"BrianParse()");
		String lines[] = html.split("\\r?\\n");
	    
	    for(int i = 0; i < lines.length; i++){
	    	if(lines[i].contains("largefeedlink")){
	    		int StartLink = lines[i].indexOf("href=") + 6;
	    		int EndLink = lines[i].indexOf(">") -1;
	    		int StartDesc = lines[i].indexOf(">") + 1;
	    		int EndDesc = lines[i].indexOf("</a>");
	    		int StartSub = lines[i].indexOf("peek") + 12;
	    		int EndSub = lines[i].indexOf("<br>") - 18;
	    		String link = lines[i].substring(StartLink, EndLink);
	    		String desc = lines[i].substring(StartDesc, EndDesc);
	    		String sub = lines[i].substring(StartSub, EndSub);
Log.d(TAG, link + "\n" + desc + "\n" + sub);	    		
	    		MainActivity.objectList.add(new ParseSubscriptions(link, desc, sub));    		
	    	}
	    }
	    Log.d(TAG, "BrianParse() done");
	}
	
	public void JasonParse(){ //parse /all/
Log.d(TAG,"JasonParse()");
		String lines[] = html.split("\\r?\\n");
		String url = null, key = null, description = null, category = null, rssUrl = null;
		for(int i=0; i<lines.length; i++){			
			if(lines[i].contains("class=\"feedlink\" href=\"")){
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"feedlink\" href=\"")+23);
				rssUrl = lines[i].substring(0, lines[i].indexOf("\">"));
				category = lines[i].substring(lines[i].indexOf("\">")+2, lines[i].indexOf("</a>"));				
			}
			else if(lines[i].contains("class=\"read_link\" href=\"")){
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"read_link\" href=\"")+24);
				key = lines[i].substring(0,lines[i].indexOf("\">"));
				description = lines[i].substring(lines[i].indexOf("\">")+2, lines[i].indexOf("</a>"));
				lines[i] = lines[i].substring(lines[i].indexOf("</a>"));
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"peek\" href=\"")+19);
				url = lines[i].substring(0,lines[i].indexOf("\">"));
				MainActivity.feeds.add(new ParsedMain(url, key, description, category, rssUrl));
			}
		}
Log.d(TAG, "JasonParse() done");
	}
	
	public void XMLParse(){//used to parse individual rss feeds
		Log.d(TAG, "XMLParse()");
		String htmlString = html;
		
		int channelstart = htmlString.indexOf("<channel>");
		int channelend = htmlString.indexOf("<item>");
		
		String channelInfo = htmlString.substring(channelstart,channelend);
		String channelTitle = channelInfo.substring(channelInfo.indexOf("<title>")+7, channelInfo.indexOf("</title>"));
		//System.out.println(channelTitle);
	/*	String channelLink = channelInfo.substring(channelInfo.indexOf("<link>")+6, channelInfo.indexOf("</link>"));
		System.out.println(channelLink);
		String channelDesc = channelInfo.substring(channelInfo.indexOf("<description>")+13, channelInfo.indexOf("</description>"));
		System.out.println(channelDesc);*/
		//System.out.println("--------------------");
		
		int bookmark = channelend;
		String title = "", link = "", desc = "";
		while (bookmark > -1){
			bookmark = htmlString.indexOf("<item>");
			if (bookmark > -1){
				int bookmarkend = htmlString.indexOf("</item>")+7;
				String item = htmlString.substring(bookmark,bookmarkend);
				
				String iTitle = item.substring(item.indexOf("<title>")+7, item.indexOf("</title>")).replace("\n", "");
				String iLink = item.substring(item.indexOf("<link>")+6, item.indexOf("</link>")).replace("\n", "");
				String iDesc = item.substring(item.indexOf("<description>")+13, item.indexOf("</description>")).replace("\n", "");

				MainActivity.XMLitems.add(new ParsedXML(iLink, iDesc, iTitle, channelTitle));
				htmlString = htmlString.substring(bookmarkend);
			}
		}
		Log.d(TAG, "XMLParse() done");
	}
}
