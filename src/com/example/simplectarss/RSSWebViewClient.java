package com.example.simplectarss;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RSSWebViewClient extends WebViewClient{
	@Override
	public boolean shouldOverrideUrlLoading(WebView webview, String url){	
		if(url.contains("google")){
			MainActivity.wv.setVisibility(View.VISIBLE);	//If it's google, switch webview visibility to SHOW
			webview.loadUrl(url);							//Else just don't display simplecta's website.
		}													//A little buggy, upon first login, will need to reload app to work.
		else if(url.contains("http://simplecta.appspot.com")){
			webview.loadUrl(url);
		}
		else
			webview.loadUrl(url);
		
		return true;
	}
	
	@Override
	public void onPageFinished(WebView webview, String url){
		if(url.contains("http://simplecta.appspot.com"))//do inject
			webview.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
		
		super.onPageFinished(webview, url);
	}
	
	
}
