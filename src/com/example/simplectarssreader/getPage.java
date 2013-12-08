package com.example.simplectarssreader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class getPage{
	final static String TAG = "getPage";
	
	Context context;
	Activity activity;
	
	protected getPage(Context c, Activity a){
		context = c;
		activity = a;
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public void showNotConnectedMessage(){
		Toast toast = Toast.makeText(context, "ERROR: Not connected to the internet", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	public void SimplectaLogIn(){
		Log.d(TAG, "SimplectaLogIn()");
		if (isOnline() == false){
			Log.d(TAG, "not connected to internet");
			showNotConnectedMessage();
			return ;
		}
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
		MainActivity.isLoggedIn = false;
		MainActivity.feeds.clear();
		MainActivity.XMLitems.clear();
		MainActivity.objectList.clear();
		MainActivity.isParseComplete.clear();
		MainActivity.wvMain.loadUrl("https://accounts.google.com/Logout?continue=https%3A%2F%2Faccounts.google.com%2FServiceLogin%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttp%253A%252F%252Fsimplecta.appspot.com%252F%26service%3Dah%26ltmpl%3Dgm%26shdf%3DChULEgZhaG5hbWUaCVNpbXBsZWN0YQwSAmFoIhRkt2n5icx2ZfNQrZC4ga09Chc6dSgBMhR2T5mk9ZxKsiZCXaUb-7IoKVexlw&il=true&zx=vqrv7fixo1v");
		//MainActivity.wvMain.loadUrl(context.getString(R.string.simplecta_googlelogin_URL));
	}
	
	public void getSimplectaMain(){
		getSimplectaMain(true);
	}
	
	public void getSimplectaMain(final Boolean shouldDisplay){
		Log.d(TAG, "getSimplectaMain()");
		if (isOnline() == false){
			Log.d(TAG, "not connected to internet");
			showNotConnectedMessage();
			return ;
		}
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
		if (isOnline() == false){
			Log.d(TAG, "not connected to internet");
			showNotConnectedMessage();
			return ;
		}
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
	
	public void getAllXMLs(){
		Log.d(TAG, "getAllXMLs()");
		if (isOnline() == false){
			Log.d(TAG, "not connected to internet");
			showNotConnectedMessage();
			return ;
		}
		MainActivity.isParseComplete.clear();
		for (int i = 0; i<MainActivity.objectList.size(); i++){			
			MainActivity.isParseComplete.add(false);
			String fixedURL = MainActivity.objectList.get(i).getChannelLink();
			fixedURL = fixedURL.substring(fixedURL.indexOf("/feed/?")+7);
			fixedURL = new Parse(context, activity).clean(fixedURL);
			new RequestTask(context,activity, 1).execute(fixedURL);
		}
	}
	
}
