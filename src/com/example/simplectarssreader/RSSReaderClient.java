package com.example.simplectarssreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class RSSReaderClient extends WebViewClient{
	final static String TAG = "RSSReaderClient";
	
	Context context;
	String fromActivity;
	Activity activity;
	
	protected RSSReaderClient(Context c, String calledFrom, Activity a){
		context = c;
		fromActivity = calledFrom;
		activity = a;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView webview, String url){
		Log.d(TAG, "shouldOverrideUrlLoading: " + url);
		/*
		 * fix the url if it is modified
		 */
		if (url.indexOf("file:///android_asset/") != -1){
			url = url.substring(url.indexOf("file:///android_asset/") + 22);
		}
		/*
		 * custom url handling determined by character at position 0 of the url string
		 * if 1, it is a link from the main simplecta feed, load it in separate browser, mark it read in wvMain, undisplay it if pref
		 * if 2, it is [view] from the manage feeds page, load customized html to display feeds from the rss feed
		 * if 3, it is [unsubscribe] from manage feeds, remove it from the display
		 * if 4, it is a link from individual rss feed display, load in separate browser
		 */
		if (url.charAt(0) == '1'){	
			int index;
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("auto_refresh_checkbox", false) == true){
				url = url.substring(2);
				index = Integer.parseInt(url.substring(0, url.indexOf("/")));
				MainActivity.feeds.remove(index);
				
				if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", false) == false){
					MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageSimple(), "text/html", "UTF-8", null);
				}
				else if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", false) == true){
					if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("detailed_mainpage_checkbox", false) == false){
						MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageSimple(), "text/html", "UTF-8", null);
					}
					else if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("detailed_mainpage_checkbox", false) == true){
						MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildMainPageDetailed(), "text/html", "UTF-8", null);
					}
				}
			}
			String parseKey = url.substring(url.indexOf("=")+1);
			while(parseKey.indexOf("&lt;") != -1){
				parseKey = parseKey.replace("&lt;", "<");
			}
			while(parseKey.indexOf("&#34;") != -1){
				parseKey = parseKey.replace("&#34;", "\"");
			}
			while(parseKey.indexOf("&gt;") != -1){
				parseKey = parseKey.replace("&gt;", ">");
			}
			while(parseKey.indexOf("&amp") != -1){
				parseKey = parseKey.replace("&amp", "&");
			}
			while(parseKey.indexOf("%3a") != -1){
				parseKey = parseKey.replace("%3a", ":");
			}
			while(parseKey.indexOf("%2f") != -1){
				parseKey = parseKey.replace("%2f", "/");
			}
			String parseUrl = parseKey.substring(parseKey.indexOf("&link=")+6);	//actual feed url
			parseKey = "http://simplecta.appspot.com/markRead/?" + parseKey.substring(0,parseKey.indexOf("&link="));//mark read
			
			MainActivity.wvMain.setWebViewClient(new WebViewClient());
			MainActivity.wvMain.loadUrl(parseKey);
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(parseUrl)));
		}
		else if (url.charAt(0) == '2'){//clicked view on managefeeds
			url = new Parse(context, TAG).clean(url.substring(1));
			Log.d(TAG, url);
			Boolean isXMLLoaded = false;
			for (int i=0; i<MainActivity.XMLitems.size(); i++){
				if (MainActivity.XMLitems.get(i).get(0).getCategory().equalsIgnoreCase(url)){
					Log.d(TAG, "already loaded, set up display " + MainActivity.XMLitems.get(i).get(0).getCategory());
					isXMLLoaded = true;
					
					MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageFeed(i), "text/html", "UTF-8", null);
					new getPage(context, TAG).display("feeds");
				}
			}
			if (isXMLLoaded == false){
				Log.d(TAG,  "xml not loaded");
				MainActivity.isLoadXML = true;
				new getPage(context,TAG).display("load");
				for (int i=0; i<MainActivity.objectList.size(); i++){
					Log.d(TAG,MainActivity.objectList.get(i).getTitle());
					if (MainActivity.objectList.get(i).getTitle().equalsIgnoreCase(url)){
						String fixedUrl = MainActivity.objectList.get(i).getUrl();
						fixedUrl = new Parse(context,TAG).clean(fixedUrl);
						fixedUrl = fixedUrl.substring(fixedUrl.indexOf("http"));
						Log.d(TAG, "found " + fixedUrl);
						new RequestTask(context, TAG, activity).execute(fixedUrl);
					}
				}
			}
		}
		else if (url.charAt(0) == '3'){//clicked unsubscribe
			url = url.substring(2);
			int index = Integer.parseInt(url.substring(0, url.indexOf("/")));
			MainActivity.objectList.remove(index);
			MainActivity.wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
			String unsub = "http://simplecta.appspot.com" + url.substring(url.indexOf("/unsubscribe/?"));
			MainActivity.wvMain.setWebViewClient(new WebViewClient());
			MainActivity.wvMain.loadUrl(unsub);
		}
		else if (url.charAt(0) == '4'){//clicked a link on individual rss feed page
			url = url.substring(url.indexOf("http"));
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
		else {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
		return true;
	}
	
}