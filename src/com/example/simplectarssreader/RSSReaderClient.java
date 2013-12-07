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
		
		//fix the url if it is not a 'real' url
		if (url.indexOf("file:///android_asset/") != -1){
			url = url.substring(url.indexOf("file:///android_asset/") + 22);
		}
		
		/*
		 * custom url handling determined by character at position 0 of the url string
		 * if 1, it is a link from the main simplecta page w/desc page
		 * if 2, it is [view] from the manage feeds page
		 * if 3, it is [unsubscribe] from manage feeds
		 * if 4, it is link from a specific feed page
		 * if 5, it is link from main simplecta page w/o desc page
		 * if 6, it is [mark unread] from main simplecta page
		 */
		if (url.charAt(0) == '1'){
			url = url.substring(2);
			int mark = url.indexOf("/");
			int index = Integer.parseInt(url.substring(0,mark));
			url = url.substring(url.indexOf("/")+1);
			mark = url.indexOf("/");
			int XMLindex = Integer.parseInt(url.substring(0,mark));
			url = url.substring(url.indexOf("/")+1);
			int XMLitemIndex = Integer.parseInt(url);
			
			String markRead = "http://simplecta.appspot.com/markRead/?" +  MainActivity.feeds.get(index).getItemLinkKey().substring(11);
			markRead = markRead.replace("&amp;", "&");
			markRead = markRead.substring(0,markRead.indexOf("&link="));
			
			String html = new BuildView(context, TAG).BuildDesc(XMLindex, XMLitemIndex);
			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
			new ViewSwapper(context).display("descMain");
			
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("auto_refresh_checkbox", true) == true){
				MainActivity.feeds.remove(index);
			}
			else {
				MainActivity.feeds.get(index).setMarked();
			}

		}
		else if (url.charAt(0) == '2'){//clicked view on managefeeds
			url = url.substring(2);
			int index = Integer.parseInt(url);
			String channelTitle = MainActivity.objectList.get(index).getChannelTitle();
			
			int xmlIndex = -1;
			for (int i=0; i<MainActivity.XMLitems.size(); i++){
				if (MainActivity.XMLitems.get(i).get(0).getChannelTitle().equals(channelTitle)){
					xmlIndex = i;
				}
			}
			
			if (xmlIndex == -1){
				Log.d(TAG, "rss xml not loaded");
				String cleanedChannelXMLLink = new Parse(context, activity).clean(MainActivity.objectList.get(index).getChannelLink().substring(7));
				new RequestTask(context, activity, 2).execute(cleanedChannelXMLLink);
			}
			else{
				Log.d(TAG, "rss xml loaded, start load single feed page");
				String html = new BuildView(context, TAG).buildMainPageFeed(xmlIndex);
				MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
				new ViewSwapper(context).display("feed-" + channelTitle);
			}
		}
		else if (url.charAt(0) == '3'){//clicked unsubscribe on managefeeds
			url = url.substring(2);
			int index = Integer.parseInt(url);
			String channelUnsubLink = "http://simplecta.appspot.com" + MainActivity.objectList.get(index).getChannelUnsubLink();

			MainActivity.objectList.remove(index);
			MainActivity.wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
			
			MainActivity.wvMain.setWebViewClient(new WebViewClient());
			MainActivity.wvMain.loadUrl(channelUnsubLink);
		}
		else if (url.charAt(0) == '4'){//clicked a link on individual rss feed page
			url = url.substring(2);
			int xmlIndex = Integer.parseInt(url.substring(0, url.indexOf("/")));
			int itemIndex = Integer.parseInt(url.substring(url.indexOf("/")+1));
			String html = new BuildView(context, TAG).BuildDesc(xmlIndex, itemIndex);
			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
			new ViewSwapper(context).display("descFeed-" + MainActivity.XMLitems.get(xmlIndex).get(0).getChannelTitle());
		}
		else if (url.charAt(0) == '5'){
			url = url.substring(2);
			int index = Integer.parseInt(url);
				
			String markRead = "http://simplecta.appspot.com/markRead/?" +  MainActivity.feeds.get(index).getItemLinkKey().substring(11);
			markRead = markRead.replace("&amp;", "&");
			markRead = markRead.substring(0,markRead.indexOf("&link="));
			String feedURL = MainActivity.feeds.get(index).getItemLink();
			MainActivity.wvMain.setWebViewClient(new WebViewClient());
			MainActivity.wvMain.loadUrl(markRead);
			
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(feedURL)));
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("auto_refresh_checkbox", true) == true){
				MainActivity.feeds.remove(index);
			}
			else {
				MainActivity.feeds.get(index).setMarked();
			}
			String html = new BuildView(context,TAG).buildMainPage();
			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
		}
		else if (url.charAt(0) == '6'){
			url = url.substring(2);
			int index = Integer.parseInt(url);
				
			String markUnread = "http://simplecta.appspot.com/markUnread/?" +  MainActivity.feeds.get(index).getItemLinkKey().substring(11);
			markUnread = markUnread.replace("&amp;", "&");
			markUnread = markUnread.substring(0,markUnread.indexOf("&link="));

			MainActivity.wvMain.setWebViewClient(new WebViewClient());
			MainActivity.wvMain.loadUrl(markUnread);
			
			MainActivity.feeds.get(index).setUnMarked();
			String html = new BuildView(context,TAG).buildMainPage();
			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
		}
		else {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
		return true;
	}
	
}