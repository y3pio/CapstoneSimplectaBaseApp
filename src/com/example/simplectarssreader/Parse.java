package com.example.simplectarssreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Parse {
	final static String TAG = "Parse";
	
	Context context;
	Activity activity;
	
	protected Parse(Context c, Activity a){
		context = c;
		activity = a;
	}
	
	void BrianParse(String parseThis){ 
		String html = parseThis;
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
	    		MainActivity.objectList.add(new ParseSubscriptions(link, desc, sub));    		
	    	}
	    }
	    Log.d(TAG, "BrianParse() done");
	}
	
	public void parseMain(String parseThis){ //parse /all/
		Log.d(TAG, "parseMain()");
		String html = parseThis;
		String lines[] = html.split("\\r?\\n");
		String url = "", 
				key = "", 
				description = "", 
				category = "", 
				rssUrl = "";
		
		for(int i=0; i<lines.length; i++){			
			if(lines[i].contains("class=\"feedlink\" href=\"")){
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"feedlink\" href=\"")+23);
				rssUrl = lines[i].substring(0, lines[i].indexOf("\">"));
				rssUrl = rssUrl.substring(7);
				rssUrl = clean(rssUrl);
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
	}
	
    public void XMLParse(String html){//used to parse individual rss feeds
    	Log.d(TAG, "XMLParse()");
		String htmlString = html;

		int channelstart = htmlString.indexOf("<channel>");
		int channelend = htmlString.indexOf("<item>");
		
		String channelInfo = htmlString.substring(channelstart,channelend);
		String channelTitle = channelInfo.substring(channelInfo.indexOf("<title>")+7, channelInfo.indexOf("</title>"));
		
		ArrayList<ParsedXML> xmlItems = new ArrayList<ParsedXML>();
		
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
				iDesc = clean(iDesc);
				xmlItems.add(new ParsedXML(iLink, iDesc, iTitle, channelTitle));
				
				htmlString = htmlString.substring(bookmarkend);
			}
		}	
		MainActivity.XMLitems.add(xmlItems);
    }
	
	public String clean(String text){
		String cleanText = text;
		cleanText = cleanText.replace("%20", " ");
		cleanText = cleanText.replace("&;", "&");
		cleanText = cleanText.replace("%3a", ":");
		cleanText = cleanText.replace("%2f", "/");
		cleanText = cleanText.replace("&lt;", "<");
		cleanText = cleanText.replace("&#34;", "\"");
		cleanText = cleanText.replace("&gt;", ">");
		cleanText = cleanText.replace("&amp", "&");	
		return cleanText;
	}
	
}
