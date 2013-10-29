package com.example.simplectarssreader;

import android.content.Context;
import android.util.Log;
import android.view.View;
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
				webview.loadUrl(url);
				return true;
			}
		}
		
		//new RequestTask(context,TAG).execute(url);
		webview.loadUrl(url);
		return true;
	}
	
	@Override
    public void onPageFinished(WebView webview, String url){
		MainActivity.URLtoLoad = url;
		String urlHost = url.substring(0,url.indexOf(".com/")+4);
		Log.d(TAG, "host is :" + urlHost);
		Log.d(TAG, "url is : " + url);
		if (urlHost.contains("simplecta.appspot.com")){
			webview.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
			if (MainActivity.isLoggedIn == false){
				Log.d(TAG, "log in sucess");
				MainActivity.isLoggedIn = true;
				MainActivity.wv.setVisibility(View.GONE);
				webview.loadUrl("http://simplecta.appspot.com/feeds/");
			}
		}
		if (urlHost.contains("simplecta.com")){
			webview.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
			if (MainActivity.isLoggedIn == false){
				Log.d(TAG, "log in sucess");
				MainActivity.isLoggedIn = true;
				//webview.loadUrl("http://simplecta.appspot.com/feeds/");
			}
		//	if (url.contains("/feeds/")){
		//		for (int i = 0; i<MainActivity.objectList.size(); i++){
		//			webview.loadUrl("http://simplecta.appspot.com" + MainActivity.objectList.get(i).getUrl());
		//		}
		//	}
		}
	}
	
}