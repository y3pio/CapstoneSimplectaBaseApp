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
	}
	
	protected Parse(Context c, String calledFrom, String htmlstring){
		context = c;
		fromActivity = calledFrom;
		html = htmlstring;
	}
	
	void YeParse(){
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
	}
	
	public void JasonParse(){
		String lines[] = html.split("\\r?\\n");
		String url = null, key = null, description = null, category = null, rssUrl = null;
		for(int i=0; i<lines.length; i++){			
			if(lines[i].contains("class=\"feedlink\" href=\"")){
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"feedlink\" href=\"")+23);
				int mark = lines[i].indexOf("\">");
				int mark2 = lines[i].indexOf("</a>");
				rssUrl = lines[i].substring(0,mark);
				category = lines[i].substring(mark+2, mark2);				
			}
			else if(lines[i].contains("class=\"read_link\" href=\"")){
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"read_link\" href=\"")+24);
				int mark = lines[i].indexOf("\">");
				key = lines[i].substring(0,mark);
				int mark2 = lines[i].indexOf("</a>");
				description = lines[i].substring(mark+2,mark2);
				lines[i] = lines[i].substring(mark2);
				lines[i] = lines[i].substring(lines[i].indexOf("class=\"peek\" href=\"")+19);
				mark = lines[i].indexOf("\">");
				url = lines[i].substring(0,mark);
				MainActivity.feeds.add(new ParsedMain(url, key, description, category, rssUrl));
			}
		}
	}
}
