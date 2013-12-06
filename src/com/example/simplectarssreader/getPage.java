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
	final static String TAG = "getPage";
	
	Context context;
	Activity activity;
	
	protected getPage(Context c, Activity a){
		context = c;
		activity = a;
	}
	
	public void SimplectaLogIn(){
		Log.d(TAG, "loadSimplectaLogIn()");
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				if (url.equals("http://simplecta.appspot.com/")){
					Log.d(TAG, "log in success, start simplecta: " + url);
					MainActivity.isLoggedIn = true;
					new ViewSwapper(context).display("load");
					getSimplectaMain();
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				/*
				if (url.equals("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&shdf=ChULEgZhaG5hbWUaCVNpbXBsZWN0YQwSAmFoIhRkt2n5icx2ZfNQrZC4ga09Chc6dSgBMhR2T5mk9ZxKsiZCXaUb-7IoKVexlw&service=ah&ltmpl=gm")){
					Log.d(TAG, "login: google log in page");
				}
				else if (url.equals("https://appengine.google.com/_ah/loginform?state=AJKiYcGBUGPSxov2outoup7tzPGzr6GKmvPLuJFWCwFqnAtknEDEfq1ZzG5BPEHk35Fx8XIsoHTBDLNaZwEX2ZbatuAbP2yo3Y4-5hu_5l5v3pVzaQj7t6jYBsakaEJcyAr3gZRqy-YVIV81xa6gv0E6-un5a6jo25z1d3WeMjC7aFuLriTi3wQsA8xX96_I7EL8IQr-OI1L5UQvT26Aq4MiguGkFYFOTVSDWIehXFbs4izvSCtU1l8rF2F6tVw2l-fpokvnwhffIGJSyGAE1yhItnrrlK33OFKHaDUrlxiIwJWMGle2CFKZUbIOizUIET4Wca9B8nxn3e30RBmW1k9NZ7uaOxBQmtXLYw5Wcl3vyH3qlziVJtHdAKxQc729hY3CScHADszegb7-oLh26n99zmEZags4IfYYJwG_53ByoR96N5itGyn1LniMwbVmSf8A-rEHQ-QyEiCo3kT2i8rZmJT0AKs5ZrROJ0MhjZtbwrUhS7YEgPwP8wzlS0dfEdLbpcZvlhSsHTVY7IKlWvkcs9rd2ce3EA")){
					Log.d(TAG, "login: google verify/remember me page");
				}*/
				if (url.contains("https://appengine.google.com/_ah/loginform?state=")){
					Log.d(TAG, "login: google verify/remember me page, allowing...");
					//skip this page by clicking the button
					view.loadUrl("javascript: document.getElementById(\"approve_button\").click(); void 0;");
				}
				else if (url.equals("https://accounts.google.com/b/0/AccountRecoveryOptionsPrompt?continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&sarp=1&service=ah")){
					Log.d(TAG, "login: google dont get locked out page, skipping...");
					//skip this page by clicking the button
					view.loadUrl("javascript:document.forms[0].submit()");
				}
				else if (url.equals("https://accounts.google.com/AccountChooser?continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&hl=en&service=ah")){
					Log.d(TAG, "login: google account chooser page, redirecting...");
					//redirect to non account chooser
					view.loadUrl("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&hl=en&service=ah");
				}
				else if (url.contains("https://accounts.google.com/ServiceLogin?continue=https")){
					Log.d(TAG, "login: google log in page but usernamed prefilled, redirecting...");
					//its the login page but the username is already filled in, so redirect to another log in
					view.loadUrl("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&hl=en&service=ah");
				}
				else if (url.contains("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&hl=en&service=ah")){
					Log.d(TAG, "login: google log in page");
					//should submit login here
				}
				else if (url.equals("https://accounts.google.com/ServiceLoginAuth")){
					Log.d(TAG, "login: invalid log in");
					//resubmit login here
				}
				else if (url.contains("https://accounts.google.com/SecondFactor?continue=")){
					Log.d(TAG, "login: require user pin");
					//submit pin, check remember
				}
				else if (url.equals("https://accounts.google.com/SecondFactor")){
					Log.d(TAG, "login: invalid user pin");
					//submit pin, check remember
				}
				else{
					Log.d(TAG, "login: else = " + url);
					//log the url to figure out what was not handled
				}
			}
		}); 
		
		new ViewSwapper(context).display("login");	
		//MainActivity.wvMain.loadUrl("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&hl=en&service=ah");
		//MainActivity.wvMain.loadUrl("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F&shdf=ChULEgZhaG5hbWUaCVNpbXBsZWN0YQwSAmFoIhRkt2n5icx2ZfNQrZC4ga09Chc6dSgBMhR2T5mk9ZxKsiZCXaUb-7IoKVexlw&service=ah&ltmpl=gm");
		MainActivity.wvMain.loadUrl("https://accounts.google.com/Logout?continue=https%3A%2F%2Faccounts.google.com%2FServiceLogin%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttp%253A%252F%252Fsimplecta.appspot.com%252F%26service%3Dah%26ltmpl%3Dgm%26shdf%3DChULEgZhaG5hbWUaCVNpbXBsZWN0YQwSAmFoIhRkt2n5icx2ZfNQrZC4ga09Chc6dSgBMhR2T5mk9ZxKsiZCXaUb-7IoKVexlw&il=true&zx=vqrv7fixo1v");
	}
	
	public void getSimplectaMain(){
		getSimplectaMain(true);
	}
	
	public void getSimplectaMain(final Boolean shouldDisplay){
		Log.d(TAG, "getSimplectaMain()");
		MainActivity.wvMain.addJavascriptInterface(new JavaScriptInterface(context, TAG){
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getSimplectaMainHTML(String html){
				MainActivity.feeds.clear();
				new Parse(context, activity).parseMain(html);
				MainActivity.loaded_feeds = true;
				
				if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", true) == true){
					activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
						public void run() {
							getSimplectaFeedsList(false);
						}
					});
				}
				else {
					if (shouldDisplay == true){
						activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
							public void run() {
								String html = new BuildView(context,TAG).buildMainPage();
								MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
								new ViewSwapper(context).display("main");
							}
						});
					}
				}
			}
		}, "HTMLOUT");
				
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {	
			@Override
			public void onPageFinished(WebView view, String url) {
				MainActivity.wvMain.loadUrl("javascript:window.HTMLOUT.getSimplectaMainHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");		
			}
		});  
				
		MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_feeds_URL));
	}
	
	public void getSimplectaFeedsList(){
		getSimplectaFeedsList(true);
	}
	
	public void getSimplectaFeedsList(final boolean shouldDisplay){
		Log.d(TAG, "getSimplectaFeedsList()");
		MainActivity.wvMain.addJavascriptInterface(new JavaScriptInterface(context, TAG){
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getSimplectaFeedsListHTML(String html){
				MainActivity.objectList.clear();
				new Parse(context, activity).BrianParse(html);
				MainActivity.loaded_managefeeds = true;
				
				if (shouldDisplay == true){
					activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
						public void run() {
							String html = new BuildView(context, TAG).buildManageFeedsPage();
							MainActivity.wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
					    	new ViewSwapper(context).display("managefeeds");
						}
					});
				}
				
				if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", true) == true){
					getAllXMLs();
				}
			}
		}, "HTMLOUT");
				
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				MainActivity.wvMain.loadUrl("javascript:window.HTMLOUT.getSimplectaFeedsListHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
			}		
		});  
		
		MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_managefeeds_URL));
	}
	

	public void SimplectaLogOut(){/*
		Log.d(TAG, "simplectaLogOut()");
		new ViewSwapper(context).display("load");
		if (MainActivity.isLoggedIn == true){
			MainActivity.wvMain.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					String urlHost = url.substring(0,url.indexOf(".com/")+4);
					if (urlHost.contains("simplecta")){
						Log.d(TAG, "log in success: " + urlHost);
						new ViewSwapper(context).display("load");
						MainActivity.wvMain.setWebViewClient(new WebViewClient());
						getSimplectaMain();
					}
					else if (urlHost.contains("accounts.google")){
						MainActivity.isLoggedIn = false;
						MainActivity.feeds.clear();
						MainActivity.XMLitems.clear();
						MainActivity.objectList.clear();
						new ViewSwapper(context).display("load");
						SimplectaLogIn();
						Log.d(TAG, "log out sucess");
					}
					else{
						Log.d(TAG, "failure to log out");
					}
				}
			});	    	
			MainActivity.wvMain.loadUrl("https://accounts.google.com/Logout?continue=https%3A%2F%2Faccounts.google.com%2FServiceLogin%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttp%253A%252F%252Fsimplecta.appspot.com%252F%26service%3Dah%26ltmpl%3Dgm%26shdf%3DChULEgZhaG5hbWUaCVNpbXBsZWN0YQwSAmFoIhRkt2n5icx2ZfNQrZC4ga09Chc6dSgBMhR2T5mk9ZxKsiZCXaUb-7IoKVexlw&il=true&zx=vqrv7fixo1v");
		}
		else {
			Log.d(TAG, "not logged in yet");
			new ViewSwapper(context).display("load");
			SimplectaLogIn();
		}*/
	}
	
	public void getAllXMLs(){
		Log.d(TAG, "getAllXMLs()");
		MainActivity.isParseComplete = new ArrayList<Boolean>();
		for (int i = 0; i<MainActivity.objectList.size(); i++){			
			MainActivity.isParseComplete.add(false);
			String fixedURL = MainActivity.objectList.get(i).getChannelLink();
			fixedURL = fixedURL.substring(fixedURL.indexOf("/feed/?")+7);
			fixedURL = new Parse(context, activity).clean(fixedURL);
			new RequestTask(context,activity, 1).execute(fixedURL);
		}
	}
	
}
