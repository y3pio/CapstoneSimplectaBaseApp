package com.example.simplectarssreader;

import android.content.Context;
import android.preference.PreferenceManager;
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
	
	public String buildMainPage(){
		Log.d(TAG, "buildMainPage()");
		String indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head><body><div class=\"wrap\"><div class=\"content\">"; 
		
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", true) == true){
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("short_summary_checkbox", true) == true){
				for(int i=0; i<MainActivity.feeds.size(); i++){
					String itemTitle = MainActivity.feeds.get(i).getItemTitle();
					String channelTitle = MainActivity.feeds.get(i).getChannelTitle();
					int XMLindex = -1;
					int itemIndex = -1;
					for (int j=0; j<MainActivity.XMLitems.size(); j++){
						if (MainActivity.XMLitems.get(j).get(0).getChannelTitle().equals(channelTitle)){
							XMLindex = j; 
							for (int k=0; k<MainActivity.XMLitems.get(j).size(); k++){
								if (MainActivity.XMLitems.get(j).get(k).getItemTitle().equals(itemTitle)){
									itemIndex = k;
								}
							}
						}
					}
					if (itemIndex != -1){
						indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>" 
								+"<a href=\""
								+"1/"+ Integer.toString(i) +  "/" + Integer.toString(XMLindex) + "/" + Integer.toString(itemIndex)
								+"\" /a>" 
								+ MainActivity.feeds.get(i).getItemTitle() 
								+"</a></h2><p>" 
								+ MainActivity.feeds.get(i).getChannelTitle()
								+ "<br><font size=\"2\" color=\"red\">[Summary available]</font>";
						
						if (MainActivity.feeds.get(i).getMarked() == true){
							if (itemIndex != -1){
								indexPage += "<br>";
							}
							indexPage+= "<a href=\""
									+"6/" + Integer.toString(i)
									+"\" /a>"
									+"<font size=\"2\" color=\"yellow\">[Mark Unread]</font>"
									+"</a>";
							indexPage+="<button onclick=\"location.href='"
									+"6/" + Integer.toString(i)
									+"'\">Mark Unread</button>";
						}
						
						indexPage += "</p></div><div class=\"clear\"></div></article>";
					}
					else{
						indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>" 
								+"<a href=\""
								+ "5/"+ Integer.toString(i)
								+"\" /a>" 
								+ MainActivity.feeds.get(i).getItemTitle() 
								+"</a></h2><p>" 
								+ MainActivity.feeds.get(i).getChannelTitle();
						
						if (MainActivity.feeds.get(i).getMarked() == true){
							indexPage+= "<br><a href=\""
									+"6/" + Integer.toString(i)
									+"\" /a>"
									+"<font size=\"2\" color=\"yellow\">[Mark UnRead]</font>"
									+"</a>";
							indexPage+="<button onclick=\"location.href='"
									+"6/" + Integer.toString(i)
									+"'\">Mark Unread</button>";
						}
						
						indexPage += "</p></div><div class=\"clear\"></div></article>";
					}
				}
			}
			else{
				for(int i=0; i<MainActivity.feeds.size(); i++){
					indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>" 
							+"<a href=\""
							+ "5/"+ Integer.toString(i)
							+"\" /a>" 
							+ MainActivity.feeds.get(i).getItemTitle() 
							+"</a></h2><p>" 
							+ MainActivity.feeds.get(i).getChannelTitle();

					if (MainActivity.feeds.get(i).getMarked() == true){
						indexPage+= "<br><a href=\""
								+"6/" + Integer.toString(i)
								+"\" /a>"
								+"<font size=\"2\" color=\"yellow\">[Mark UnRead]</font>"
								+"</a>";
						indexPage+="<button onclick=\"location.href='"
								+"6/" + Integer.toString(i)
								+"'\">Mark Unread</button>";
					}
					
					indexPage += "</p></div><div class=\"clear\"></div></article>";
				}
			}
		}
		else{
			for(int i=0; i<MainActivity.feeds.size(); i++){
				indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>" 
						+"<a href=\""
						+ "5/"+ Integer.toString(i)
						+"\" /a>" 
						+ MainActivity.feeds.get(i).getItemTitle() 
						+"</a></h2><p>" 
						+ MainActivity.feeds.get(i).getChannelTitle();
				
				if (MainActivity.feeds.get(i).getMarked() == true){
					indexPage+= "<br><a href=\""
							+"6/" + Integer.toString(i)
							+"\" /a>"
							+"<font size=\"2\" color=\"yellow\">[Mark UnRead]</font>"
							+"</a>";
					indexPage+="<button onclick=\"location.href='"
							+"6/" + Integer.toString(i)
							+"'\">Mark Unread</button>";
				}
				
				indexPage += "</p></div><div class=\"clear\"></div></article>";
						
			}
		}
		
		indexPage+="</div></body></html>";
		return indexPage;
	}
	
	public String buildManageFeedsPage(){
		Log.d(TAG, "buildManageFeedsPage()");
		String indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		
		for (int i = 0; i<MainActivity.objectList.size(); i++){
			indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>"
					+MainActivity.objectList.get(i).getChannelTitle() 
					+ "</h2><p><a href=\""
					+"2/" + Integer.toString(i)
					+"\" /a>"
					+"[View]"
					+"</a><a href=\""
					+"3/" + Integer.toString(i)
					+"\" /a>" 
					+ "[Unsubscribe]" 
					+"</a></p></div><div class=\"clear\"></div></article>";
		}
		
		indexPage+="</div></body></html>";
	
		return indexPage;
	}
	
	public String buildMainPageFeed(int index){
		Log.d(TAG, "buildMainPageFeed()");

		String feedTitle = MainActivity.XMLitems.get(index).get(0).getChannelTitle();		
		String indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head><body><div class=\"wrap\"><div class=\"content\">";
		
		indexPage+="<article class=\"underline\"><div class=\"post-content\"><h1>" 
				+feedTitle	
				+"</h1>";
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", true) == true){
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("short_summary_checkbox", true) == true){
				indexPage+= "<br><font size=\"2\" color=\"red\">[Summaries are available for all items]</font>";
			}
		}
		indexPage+="</div><div class=\"clear\"></div></article>";
		
		for(int i=0; i<MainActivity.XMLitems.get(index).size(); i++){
			indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>" 
					+"<a href=\"";
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", true) == true){
				if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("short_summary_checkbox", true) == true){
					indexPage+="4/" + Integer.toString(index) + "/" + Integer.toString(i);
				}
				else {
					indexPage+=MainActivity.XMLitems.get(index).get(i).getItemLink();
				}
			}
			else {
				indexPage+=MainActivity.XMLitems.get(index).get(i).getItemLink();
			}
			indexPage+="\" /a>" 
					+ MainActivity.XMLitems.get(index).get(i).getItemTitle() 
					+"</a></h2></div><div class=\"clear\"></div></article>";
		}

		indexPage+="</div></body></html>";
		return indexPage;
	}
	
	public String buildMainPageNewest(int howMany){
		Log.d(TAG, "buildMainPageNewest()");
		String indexPage;
	
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		
		
		indexPage+="</div></body></html>";
		return indexPage;
	}
	
	public String BuildDesc(int i, int j){
		Log.d(TAG, "buildDesc()");
		String indexPage;
		
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		
		indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2>" 
				+"<a href=\""
				+ MainActivity.XMLitems.get(i).get(j).getItemLink()
				+"\" /a>" 
				+ MainActivity.XMLitems.get(i).get(j).getItemTitle() 
				+"</a></h2><p>" 
				+ MainActivity.XMLitems.get(i).get(j).getChannelTitle() 
				+"</p></div><div class=\"clear\"></div></article>";
		
		indexPage+="<article class=\"underline\"><div class=\"post-content\">"
				+"</a><p>" 
				+ MainActivity.XMLitems.get(i).get(j).getItemDesc() 
				+"</p></div><div class=\"clear\"></div></article>";
		
		indexPage+="</div></body></html>";

		return indexPage;
	}
	
}
