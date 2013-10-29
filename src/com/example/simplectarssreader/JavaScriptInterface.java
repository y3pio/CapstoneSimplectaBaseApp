package com.example.simplectarssreader;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
	final static String TAG = "JavaScriptInterface";
	
	Context context;
	String calledFrom;
	
	protected JavaScriptInterface(Context c, String fromActivity){
		context = c;
		calledFrom = fromActivity;
	}
	
	@SuppressWarnings("unused")
	@JavascriptInterface
    public void processHTML(String html){
		MainActivity.html = html;
		
		Log.d(TAG, "--------processHTML---------");
		Log.d(TAG, "loading : " + MainActivity.URLtoLoad);
		
		//save html to a text file of simplecta.appspot.com pages for debugging/testing purposes
		//HtmlFile writer = new HtmlFile(TAG);
		//writer.makeHTMLFile(html);
				
		//parse here, please replace "your url here" with your url	
		if (MainActivity.URLtoLoad.contains("simplecta.appspot.com/feed/")){
			Log.d(TAG, "ye parse");
			MainActivity.parsedItems.clear();
			new Parse(context, TAG, html).YeParse();

			MainActivity.loadedYe = true;
		}
		else if (MainActivity.URLtoLoad.contains("simplecta.appspot.com/feeds/")){
			Log.d(TAG, "brian parse");
			MainActivity.objectList.clear();
			new Parse(context, TAG, html).BrianParse();

			MainActivity.loadedBrian = true;
			if (MainActivity.loadedYe == false){
				MainActivity.loadedYe = true;
				for (int i = 0; i<MainActivity.objectList.size(); i++){
					System.out.println(MainActivity.objectList.get(i).getUrl());
					String fixedURL = MainActivity.objectList.get(i).getUrl();
					fixedURL = fixedURL.substring(fixedURL.indexOf("/feed/?")+7);
					fixedURL = fixedURL.replace("%3a", ":");
					fixedURL = fixedURL.replace("%2f", "/");
					System.out.println(fixedURL);
					System.out.println("-----------");
					new RequestTask(context,TAG).execute(fixedURL);
				}
			}
		}	
		else if (MainActivity.URLtoLoad.contains("/all/")){
			Log.d(TAG, "jason parse");
			MainActivity.feeds.clear();
			new Parse(context, TAG, html).JasonParse();

			MainActivity.loadedJason = true;
			if (MainActivity.loadedBrian = false){
				MainActivity.wv.loadUrl("http://simplecta.appspot.com/feeds/");
				MainActivity.loadthis = MainActivity.htmlstart;
				for(int i=0; i<MainActivity.feeds.size(); i++){
					MainActivity.loadthis = MainActivity.loadthis + MainActivity.htmlheaderstart+MainActivity.feeds.get(i).getDesc()+MainActivity.htmlheaderend;
				}
				MainActivity.wv2.loadData(MainActivity.loadthis, "text/html", null);
				MainActivity.wv2.setVisibility(View.VISIBLE);
			}
		}
		else {
			Log.d(TAG, "jason parse");
			MainActivity.feeds.clear();
			new Parse(context, TAG, html).JasonParse();

			MainActivity.loadedJason = true;
			if (MainActivity.loadedBrian = false){
				MainActivity.wv.loadUrl("http://simplecta.appspot.com/feeds/");
			}
		}
		Log.d(TAG, "procesHTML complete");
    }
	
	
}


