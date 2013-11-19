package com.example.simplectarssreader;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class BuildView {
final static String TAG = "BuildView";
	
	Context context;
	String fromActivity;
	
	protected BuildView(Context c, String calledFrom){
		context = c;
		fromActivity = calledFrom;
	}
	
	public String buildMainPageSimple(){
		String indexPage = null;
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 

		//This takes a while to load. Links somehow not working
		for(int i=0; i<MainActivity.feeds.size(); i++){
			//System.out.println(MainActivity.feeds.get(i).getURL());
			indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2><a href=\""+"1"+"/"+Integer.toString(i)+MainActivity.feeds.get(i).getURLKey()
			+"\" /a>" + MainActivity.feeds.get(i).getDesc() +"</a></h2><p>" + MainActivity.feeds.get(i).getCategory()	
			+"</p></div><div class=\"clear\"></div></article>";
		}
		indexPage+="</div></body></html>";
		MainActivity.feedsPage = "simple";
		return indexPage;
	}
	
	public String buildMainPageDetailed(){
		String indexPage, extendedDesc = "";
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 

		for(int i=0; i<MainActivity.feeds.size(); i++){
			for (int j=0; j<MainActivity.XMLitems.size(); j++){
				if (MainActivity.XMLitems.get(j).size() > 0){
					//Log.d(TAG, MainActivity.XMLitems.get(j).get(0).getCategory() + "," + MainActivity.feeds.get(i).getCategory());
					if (MainActivity.XMLitems.get(j).get(0).getCategory().equalsIgnoreCase(MainActivity.feeds.get(i).getCategory())){
					//Log.d(TAG, "matched");
						for (int k=0; k<MainActivity.XMLitems.get(j).size(); k++){
						//Log.d(TAG, MainActivity.XMLitems.get(j).get(k).getTitle() + "," + MainActivity.feeds.get(i).getDesc());
							if (MainActivity.XMLitems.get(j).get(k).getTitle().equalsIgnoreCase(MainActivity.feeds.get(i).getDesc())){
								extendedDesc = "<p>" + MainActivity.XMLitems.get(j).get(k).getDesc() + "</p>";
							}
						}
					}
				}
			}
			indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2><a href=\""+"1/"+Integer.toString(i)+MainActivity.feeds.get(i).getURLKey()
			+"\" /a>" + MainActivity.feeds.get(i).getDesc() +"</a></h2><p>" + MainActivity.feeds.get(i).getCategory()
			+"</p>" + extendedDesc + "</div><div class=\"clear\"></div></article>";
		}
		indexPage+="</div></body></html>";
		MainActivity.feedsPage = "detailed";
		return indexPage;
	}
	
	public String buildManageFeedsPage(){
		Log.d(TAG, "buildManageFeedsPage()");
		String indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		
		for (int i = 0; i<MainActivity.objectList.size(); i++){
			indexPage+="<article class=\"underline\"><div class=\"post-content\">"
					//+"<h2><a href=\""+ "2" + MainActivity.objectList.get(i).getUrl() + "\" /a>"+ MainActivity.objectList.get(i).getTitle() +"</a></h2>"
					+"<h2>" + MainActivity.objectList.get(i).getTitle() + "</h2>"
					+"<p>"
					+"<a href=\""+"2"+MainActivity.objectList.get(i).getTitle()+"\" /a>" + "[View]" +"</a>"
					+"<a href=\""+"3/" + Integer.toString(i) +MainActivity.objectList.get(i).getSub()+"\" /a>" + "[Unsubscribe]" +"</a>"
					+"</p>" 
					+"</div><div class=\"clear\"></div></article>";
		}
		
		indexPage+="</div></body></html>";
	
		return indexPage;
	}
	
	public String buildMainPageFeed(String feedTitle){
		String indexPage, extendedDesc = "";
		int index = -1;
		for (int i=0; i<MainActivity.XMLitems.size(); i++){
			if (MainActivity.XMLitems.get(i).get(0).getCategory().equalsIgnoreCase(feedTitle)){
				index = i;
			}
		}
		
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		if (index < -1){
			for(int i=0; i<MainActivity.XMLitems.get(index).size(); i++){
				indexPage+="<article class=\"underline\"><div class=\"post-content\">"
						+"<h2><a href=\""+ "4" + MainActivity.XMLitems.get(index).get(i).getLink() + "\" /a>"+ MainActivity.XMLitems.get(index).get(i).getTitle() +"</a></h2>"
						+"<p>"
						+MainActivity.XMLitems.get(index).get(i).getDesc()
						+"</p>" 
						+"</div><div class=\"clear\"></div></article>";
			}
		}
		indexPage+="</div></body></html>";
		MainActivity.feedsPage = "feed-" + feedTitle;
		return indexPage;
	}
	
	
}
