package com.example.simplectarssreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class RSSReaderClient extends WebViewClient{
	final static String TAG = "RSSReaderClient";
	
	Context context;
	String fromActivity;
	
	protected RSSReaderClient(Context c, String calledFrom){
		context = c;
		fromActivity = calledFrom;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView webview, String url){
Log.d(TAG, "shouldOverrideUrlLoading");
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		return true;
	}
	
}