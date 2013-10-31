package com.example.simplectarssreader;

//tag:^(?!(Choreographer|dalvikvm))


import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity{
	final static String TAG = "MainActivity";
	
	public static File filesDir;
	
	public static WebView wvMain, wvUser;

	public static boolean isLoggedIn, isLogInFinished;
	
	public static ArrayList<ParsedMain> feeds;
	public static ArrayList<ParsedFeed> parsedItems;
	public static ArrayList<ParseSubscriptions> objectList;
	public static ArrayList<ParsedXML> XMLitems;
	
	public static String URLtoLoad, simplectaMain, simplectaFeedsList;
	
	Context context;
	
	@Override	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.new_activity_main);
		Log.d(TAG, "-------------START APP----------");
		
		context = this;
		
		feeds = new ArrayList<ParsedMain>(); 		//Jason's 
		parsedItems = new ArrayList<ParsedFeed>(); 	//Ye's
		objectList = new ArrayList<ParseSubscriptions>();	//Brian's
		XMLitems = new ArrayList<ParsedXML>();
		

		simplectaMain = null;
		simplectaFeedsList = null;
		
	/*	
		Log.d(TAG, "create file directory");
		File sdCard = Environment.getExternalStorageDirectory();
        filesDir = new File (sdCard.getAbsolutePath() + "/Android/data/com.example.simplectarssreader");
        filesDir.mkdirs();
	*/	
		isLoggedIn = false;
		isLogInFinished = false;
        
		wvUser = (WebView) findViewById(R.id.webView);
		wvUser.getSettings().setJavaScriptEnabled(true);
		wvUser.setWebViewClient(new RSSReaderClient(this, TAG));
		wvUser.addJavascriptInterface(new JavaScriptInterface(context, TAG), "USER");
		wvUser.setVisibility(View.GONE);
		
		wvMain = (WebView) findViewById(R.id.MainActivity_WebView1);
		wvMain.getSettings().setJavaScriptEnabled(true);
		wvUser.setWebViewClient(new WebViewClient());
			
		SimplectaLogIn();
		
	}//end of oncreate
	
	
	public void SimplectaLogIn(){
Log.d(TAG, "loadSimplectaLogIn()");
		wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				
				if (urlHost.contains("simplecta")){
Log.d(TAG, "log in success: " + url);
					isLoggedIn = true;
					
					//wvMain.setWebViewClient(new WebViewClient());
					wvMain.setVisibility(View.GONE);
					
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				if (urlHost.contains("simplecta")){
Log.d(TAG, "log in success: " + url);
					isLoggedIn = true;
					
					wvMain.setWebViewClient(new WebViewClient());
					
					getSimplectaMain();//start getting info from simplecta
				}
				else if (urlHost.contains("accounts.google")){
Log.d(TAG, "attempt log in");
				}
				else {
Log.d(TAG, "failure to log in");
				}
			}
		});  
		
		wvMain.loadUrl(getString(R.string.simplecta_login_URL));
	}
	
	public void getSimplectaMain(){
Log.d(TAG, "loadSimplectaMain()");
		wvMain.addJavascriptInterface(new JavaScriptInterface(context, TAG){
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getSimplectaMainHTML(String html){
Log.d(TAG, "jason parse");
				MainActivity.simplectaMain = html;
				MainActivity.feeds.clear();
				new Parse(context, TAG, html).JasonParse();

Log.d(TAG, "done setting up html, start load");
				
				runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
					public void run() {
						buildMainPage();
						if (isLogInFinished == false){
							isLogInFinished = true;
							Log.d(TAG, "begin retrieving the rest of simplecta's data");
							getSimplectaFeedsList();
						}
					}
				});
				
				
			}
		}, "HTMLOUT");
		
		wvMain.setWebViewClient(new WebViewClient() {	
			@Override
			public void onPageFinished(WebView view, String url) {
Log.d(TAG, "loadSimplectaMainPage() onPageFinished()");
Log.d(TAG, "start inject javascript");				
				wvMain.loadUrl("javascript:window.HTMLOUT.getSimplectaMainHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
			}
		});  
		
		
		wvMain.loadUrl(getString(R.string.simplecta_feeds_URL));
	}
	
	public void getSimplectaFeedsList(){
		wvMain.addJavascriptInterface(new JavaScriptInterface(context, TAG){
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getSimplectaFeedsListHTML(String html){
Log.d(TAG, "brian parse");
				MainActivity.simplectaFeedsList = html;
				MainActivity.objectList.clear();
				new Parse(context, TAG, html).BrianParse();
				
				getXMLs();
			}
		}, "HTMLOUT");
		
		wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
Log.d(TAG, "loadSimplectaFeedsList() onPageFinished()");
Log.d(TAG, "start inject javascript" + url);

				wvMain.loadUrl("javascript:window.HTMLOUT.getSimplectaFeedsListHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
			}		
		});  

		wvMain.loadUrl(getString(R.string.simplecta_managefeeds_URL));

	}
	
	
	public void SimplectaLogOut(){
		if (isLoggedIn == true){
	    	wvMain.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					String urlHost = url.substring(0,url.indexOf(".com/")+4);
					if (urlHost.contains("simplecta")){
Log.d(TAG, "log in success: " + url);
						wvMain.setVisibility(View.GONE);
						wvMain.setWebViewClient(new WebViewClient());
						getSimplectaMain();
					}
					else if (urlHost.contains("accounts.google")){
						isLoggedIn = false;
						isLogInFinished = false;
						feeds.clear();
						XMLitems.clear();
						objectList.clear();
						wvUser.setVisibility(View.GONE);
				    	wvMain.setVisibility(View.VISIBLE);
				    	SimplectaLogIn();
Log.d(TAG, "log out sucess");
					}
					else{
Log.d(TAG, "failure to log out");
					}
				}
			});	    	
	    	wvMain.loadUrl(getString(R.string.simplecta_logout_URL));
		}
		else {
Log.d(TAG, "not logged in yet");
		}
	}
	
	public void resetWebView(WebView wv){
		wv.addJavascriptInterface(null, null);
		wv.setWebViewClient(new WebViewClient());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                 //   Intent settings = new Intent(this, SettingsActivity.class);
                 //   startActivity(settings);
               return true;
            case R.id.action_manageFeeds:
            	;
            	return true;
            case R.id.action_logout:
            	SimplectaLogOut();
            	return true;  
            case R.id.item1:
            	wvUser.setVisibility(View.GONE);
            	wvMain.setVisibility(View.VISIBLE);
            	return true;
            case R.id.item2:
            	wvMain.setVisibility(View.GONE);
            	wvUser.setVisibility(View.VISIBLE);
            	return true;           	
            case R.id.item3:
            	wvMain.setVisibility(View.GONE);
            	wvUser.setVisibility(View.GONE);
            case R.id.item4:
            	;
            default:
                return super.onOptionsItemSelected(item);
        }
	}
	
	public void buildMainPage(){
		String indexPage = null;
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		//This takes a while to load. Links somehow not working
		for(int i=0; i<MainActivity.feeds.size(); i++){
			//System.out.println(MainActivity.feeds.get(i).getURL());
			indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2><a href=\""+MainActivity.feeds.get(i).getURL()
			+"\" /a>" + MainActivity.feeds.get(i).getDesc() + "</a></h2><p>" + MainActivity.feeds.get(i).getCategory()
					+"</p></div><div class=\"clear\"></div></article>";
		}
		indexPage+="</div></body></html>";
		MainActivity.wvUser.setVisibility(View.VISIBLE);
		MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", indexPage, "text/html", "UTF-8", null);
	}
	
	public void buildLinksPage(){
		
	}
	
	public void getXMLs(){
Log.d(TAG, "getting xml of feeds");
		for (int i = 0; i<MainActivity.objectList.size(); i++){
Log.d(TAG, MainActivity.objectList.get(i).getUrl());
			String fixedURL = MainActivity.objectList.get(i).getUrl();
			fixedURL = fixedURL.substring(fixedURL.indexOf("/feed/?")+7);
			fixedURL = fixedURL.replace("%3a", ":");
			fixedURL = fixedURL.replace("%2f", "/");
			System.out.println(fixedURL);
			System.out.println("-----------");
			new RequestTask(context,TAG).execute(fixedURL);
		}
Log.d(TAG, "done getting xml from feeds");
	}
	

}//end of mainactivity
