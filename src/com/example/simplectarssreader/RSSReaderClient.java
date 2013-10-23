package com.example.simplectarssreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class RSSReaderClient extends WebViewClient {
	final static String TAG = "RSSReaderClient";
	
	Context context;
	String fromActivity;
	
	protected RSSReaderClient(Context c, String calledFrom){
		context = c;
		fromActivity = calledFrom;
	}
	
	//Override loading URL into default web client
	@Override
	public boolean shouldOverrideUrlLoading(WebView webview, String url){
		MainActivity.URLtoLoad = url;
		if (fromActivity == "MainActivity"){
			if (url.contains("simplecta.appspot.com")){
				webview.loadUrl(url);
				return false;
			}
			else if (url.contains("google.com/accounts/ServiceLogin")){
				webview.loadUrl(url);
				return false;
			}
			else {//user clicked an rss feed
				
				return true;
			}
		}
		
		//new RequestTask(context,TAG).execute(url);
			
		return true;
	}
	
	@Override
    public void onPageFinished(WebView webview, String url){
		if (Uri.parse(url).getHost().equals("simplecta.appspot.com")){
			MainActivity.isLoggedIn = true;
			/* This call inject JavaScript into the page which just finished loading. */
			//this only needs to be done to simplecta urls because it is behind a log in, can httpget everything else
			webview.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
		}
	}
	
}