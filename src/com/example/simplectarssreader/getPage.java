package com.example.simplectarssreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class getPage{
	final static String TAG = "getpage";
	
	Context context;
	Activity activity;
	String fromActivity;
	SharedPreferences prefs;
	
	protected getPage(Context c, String calledFrom, Activity a){
		context = c;
		String fromActivity = calledFrom;
		activity = a;
		prefs = PreferenceManager.getDefaultSharedPreferences(context); 
	}
	
	protected getPage(Context c, String calledFrom){
		context = c;
		fromActivity = calledFrom;
		prefs = PreferenceManager.getDefaultSharedPreferences(context); 
	}
	
	public void SimplectaLogIn(){
		Log.d(TAG, "loadSimplectaLogIn()");
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				if (urlHost.contains("simplecta")){
					Log.d(TAG, "log in success: ");
					MainActivity.isLoggedIn = true;
					display("load");
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				if (urlHost.contains("simplecta")){
					MainActivity.wvMain.setWebViewClient(new WebViewClient());
					Log.d(TAG,"log in done");

					getSimplectaMain();//start getting info from simplecta
				}
				else if (urlHost.contains("accounts.google") || urlHost.contains("appengine.google")){
					display("login");
					Log.d(TAG, "attempt log in " + url);
				}
				else {
					Log.d(TAG, "failure to log in" + url + "\nredirect back to login");
					view.loadUrl(context.getString(R.string.simplecta_login_URL));
				}
			}
		}); 
		
		display("login");	
		MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_login_URL));
	}
	
	public void getSimplectaMain(){
		Log.d(TAG, "getSimplectaMain()");
		MainActivity.wvMain.addJavascriptInterface(new JavaScriptInterface(context, TAG){
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getSimplectaMainHTML(String html){
				MainActivity.feeds.clear();
				new Parse(context, TAG, html).JasonParse();
				MainActivity.loaded_feeds = true;
				Log.d(TAG, "getSimplectaMain(), got html");
				
				if (MainActivity.isRefresh == false){
					if (prefs.getBoolean("preload_checkbox", false) == false){
						Log.d(TAG, "pref preload = false; display main simple");
						activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
							public void run() {
								MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageSimple(), "text/html", "UTF-8", null);
								display("feeds");
							}
						});
					}
					else if (prefs.getBoolean("preload_checkbox", false) == true){
						if (prefs.getBoolean("detailed_mainpage_checkbox", false) == false){
							Log.d(TAG, "pref preload = true, detailedmain = false; display simple main");
							activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
								public void run() {
									MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageSimple(), "text/html", "UTF-8", null);
									display("feeds");
								}
							});
						}
					}
				}
				else if (MainActivity.isRefresh == true){
					Log.d(TAG, "isRefresh");
					if (prefs.getBoolean("preload_checkbox", false) == false){//handle refresh when preload = false
						if (MainActivity.prevPage == "feeds"){//if the page was feeds page, done with refresh
							MainActivity.isRefresh = false;
							Log.d(TAG, "refresh: preload = false; page = feeds");
							activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
								public void run() {
									MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageSimple(), "text/html", "UTF-8", null);
									display("feeds");
								}
							});
						}
					}
					else if (prefs.getBoolean("preload_checkbox", false) == true){ //handle refresh when preload = true
						if (prefs.getBoolean("detailed_mainpage_checkbox", false) == false){//if detailedmain = false
							if (MainActivity.prevPage == "feeds"){//if page to refresh = feeds, done
								MainActivity.isRefresh = false;
								Log.d(TAG, "refresh: preload = true, detailedmain = false; page = feeds");
								activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
									public void run() {
										MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageSimple(), "text/html", "UTF-8", null);
										display("feeds");
									}
								});
							}
						}
					}
				}
					
				MainActivity.loaded_feeds = true;
				
				if (prefs.getBoolean("preload_checkbox", false) == true){
					Log.d(TAG, "preload = true");
					activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
						public void run() {
							getSimplectaFeedsList();
						}
					});
				}
			}
		}, "HTMLOUT");
				
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {	
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d(TAG, "getSimplectaMainPage() onPageFinished(), inject javacript");
				MainActivity.wvMain.loadUrl("javascript:window.HTMLOUT.getSimplectaMainHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");		
			}
		});  
				
		MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_feeds_URL));
	}
	
	public void getSimplectaFeedsList(){
		activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
			public void run() {
				getSimplectaFeedsList(false);
			}
		});
	}
	
	public void getSimplectaFeedsList(final Boolean isManageFeeds){
		Log.d(TAG, "getSimplectaFeedsList()");
		MainActivity.wvMain.addJavascriptInterface(new JavaScriptInterface(context, TAG){
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getSimplectaFeedsListHTML(String html){
				MainActivity.objectList.clear();
				new Parse(context, TAG, html).BrianParse();
				MainActivity.loaded_managefeeds = true;
				Log.d(TAG, "getSimplectaFeedsList() get html done");
				
				if (isManageFeeds == true){
					Log.d(TAG, "isManageFeeds = true, load managefeeds page");
					activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
						public void run() {
							MainActivity.wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
					    	display("managefeeds");
						}
					});
				}
				if (prefs.getBoolean("preload_checkbox", false) == true){
					getXMLs();
				}
			}
		}, "HTMLOUT");
				
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d(TAG, "getSimplectaFeedsList() onPageFinished() start inject javascript" + url);
				MainActivity.wvMain.loadUrl("javascript:window.HTMLOUT.getSimplectaFeedsListHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
			}		
		});  
		
		MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_managefeeds_URL));
	}
			
			
	public void SimplectaLogOut(){
		Log.d(TAG, "simplectaLogOut()");
		if (MainActivity.isLoggedIn == true){
			MainActivity.wvMain.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					String urlHost = url.substring(0,url.indexOf(".com/")+4);
					if (urlHost.contains("simplecta")){
						Log.d(TAG, "log in success: " + url);
						MainActivity.wvMain.setVisibility(View.GONE);
						MainActivity.wvMain.setWebViewClient(new WebViewClient());
						getSimplectaMain();
					}
					else if (urlHost.contains("accounts.google")){
						MainActivity.isLoggedIn = false;
						//MainActivity.isLogInFinished = false;
						MainActivity.feeds.clear();
						MainActivity.XMLitems.clear();
						MainActivity.objectList.clear();
						display("load");
						SimplectaLogIn();
						Log.d(TAG, "log out sucess");
					}
					else{
						Log.d(TAG, "failure to log out");
					}
				}
			});	    	
			MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_logout_URL));
		}
		else {
			Log.d(TAG, "not logged in yet");
		}
	}
			
	public void display(String view){
		if (view.equalsIgnoreCase("feeds")){
			MainActivity.currPage = ("feeds");
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
			
			MainActivity.wvMain.setVisibility(View.GONE);
			
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.equalsIgnoreCase("login")){
			MainActivity.currPage = ("login");
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.GONE);
			
			MainActivity.wvMain.setVisibility(View.VISIBLE);
					
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.equalsIgnoreCase("managefeeds")){
			MainActivity.currPage = ("managefeeds");
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.GONE);
					
			MainActivity.wvMain.setVisibility(View.GONE);

			MainActivity.manageFeedsDone.setVisibility(View.VISIBLE);
			MainActivity.manageFeedsAdd.setVisibility(View.VISIBLE);
			MainActivity.wvManageFeeds.setVisibility(View.VISIBLE);
				
		}
		else if (view.equalsIgnoreCase("load")){
			MainActivity.currPage = ("load");
			
			MainActivity.loadText.setVisibility(View.VISIBLE);
			
			MainActivity.wvUser.setVisibility(View.GONE);
					
			MainActivity.wvMain.setVisibility(View.GONE);
					
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
	}
	
	public void getXMLs(){
		Log.d(TAG, "getXMLs()");
		MainActivity.isParseComplete = new ArrayList<Boolean>();
		for (int i = 0; i<MainActivity.objectList.size(); i++){
			Log.d(TAG, MainActivity.objectList.get(i).getUrl());
			MainActivity.isParseComplete.add(false);
			String fixedURL = MainActivity.objectList.get(i).getUrl();
			fixedURL = fixedURL.substring(fixedURL.indexOf("/feed/?")+7);
			fixedURL = fixedURL.replace("%3a", ":");
			fixedURL = fixedURL.replace("%2f", "/");
			//System.out.println(fixedURL);
			//System.out.println("-----------");
			new RequestTask(context,TAG, activity).execute(fixedURL);
		}
		Log.d(TAG, "done getting xml from feeds");
	}
	
	public String clean(String text){
		String cleanedText = text;
		cleanedText = cleanedText.replace("%20", " ");
		cleanedText = cleanedText.replace("%3a", ":");
		cleanedText = cleanedText.replace("%2f", "/");
		cleanedText = cleanedText.replace("&lt;", "<");
		cleanedText = cleanedText.replace("&#34;", "\"");
		cleanedText = cleanedText.replace("&gt;", ">");
		cleanedText = cleanedText.replace("&amp", "&");		
		return cleanedText;
	}

	
}
