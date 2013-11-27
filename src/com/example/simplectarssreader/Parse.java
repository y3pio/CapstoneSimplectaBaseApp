package com.example.simplectarssreader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Parse {
	final static String TAG = "Parse";
	
	Context context;
	Activity activity;
	String fromActivity, html;
	
	protected Parse(String htmlstring){
		context = null;
		fromActivity = "";
		html = htmlstring;
		//Log.d(TAG, "------------Parse---------");
	}
	protected Parse(Context c, String calledFrom){
		context = c;
		fromActivity = calledFrom;
		html = "";
	}
	protected Parse(Context c, String calledFrom, String htmlstring){
		context = c;
		fromActivity = calledFrom;
		html = htmlstring;
		//Log.d(TAG, "------------Parse---------");
	}
	
	void BrianParse(){ 
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
	
	public void JasonParse(){ //parse /all/
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
	
	public String clean(String text){
		String cleanText = text;
		cleanText = cleanText.replace("%20", " ");
		cleanText = cleanText.replace("%3a", ":");
		cleanText = cleanText.replace("%2f", "/");
		cleanText = cleanText.replace("&lt;", "<");
		cleanText = cleanText.replace("&#34;", "\"");
		cleanText = cleanText.replace("&gt;", ">");
		cleanText = cleanText.replace("&amp", "&");	
		return cleanText;
	}
	
}
