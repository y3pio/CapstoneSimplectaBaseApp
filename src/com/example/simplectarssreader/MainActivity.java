package com.example.simplectarssreader;

//tag:^(?!(Choreographer|dalvikvm))


import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;


public class MainActivity extends Activity{
	final static String TAG = "MainActivity";
	
	public static File filesDir;
	
	public static WebView wv,wv2;

	public static String html, URLtoLoad;
	public static boolean isLoggedIn, loadedYe, loadedBrian, loadedJason;
	
	public static ArrayList<ParsedMain> feeds;
	public static ArrayList<ParsedFeed> parsedItems;
	public static ArrayList<ParseSubscriptions> objectList;
	public static ArrayList<ParsedXML> XMLitems;
	
	public static String htmlstart, htmlend, loadthis;
	
	public static String htmlheaderstart, htmlheaderend;
	
	@Override	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.new_activity_main);
		Log.d(TAG, "-------------START APP----------");
		
		feeds = new ArrayList<ParsedMain>(); 		//Jason's 
		parsedItems = new ArrayList<ParsedFeed>(); 	//Ye's
		objectList = new ArrayList<ParseSubscriptions>();	//Brian's
		XMLitems = new ArrayList<ParsedXML>();
		
		loadedJason = false;
		loadedYe = false;
		loadedBrian = false;
		
		htmlstart = "<!DOCTYPE html>\n<html>\n<body>\n<p>main page</p>";
		htmlend = "</body>\n</html>";
		
		htmlheaderstart = "<p>";
		htmlheaderend = "</p>";
		
		
		Log.d(TAG, "create file directory");
		File sdCard = Environment.getExternalStorageDirectory();
        filesDir = new File (sdCard.getAbsolutePath() + "/Android/data/com.example.simplectarssreader");
        filesDir.mkdirs();
		
		isLoggedIn = false;
        
		//new RequestTask(this, TAG).execute(urlString);
		
		wv2 = (WebView) findViewById(R.id.webView);
		wv2.getSettings().setJavaScriptEnabled(true);
		wv2.setWebViewClient(new RSSReaderClient(this, TAG));
		wv2.setVisibility(View.GONE);
		
		wv = (WebView) findViewById(R.id.MainActivity_WebView1);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new JavaScriptInterface(this, TAG), "HTMLOUT");
		wv.setWebViewClient(new RSSReaderClient(this, TAG));
			
		String url = getString(R.string.simplecta_URL);
		URLtoLoad = url;
		Log.d(TAG, "loading: " + url);
		wv.loadUrl(url);
		
		//feeds.add(new RSSFeed("a","b","c","d"));
		
	}//end of oncreate
	
	
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
            	if (isLoggedIn == true){
            		wv.loadUrl(getString(R.string.simplecta_managefeeds_URL));
            	}
            	return true;
            case R.id.action_logout:
            	if (isLoggedIn == true){
            		wv.loadUrl(getString(R.string.simplecta_logout_URL));
            		Log.d(TAG, "log out sucess");
            		isLoggedIn = false;
            		loadedBrian = false;
            		loadedYe = false;
            		feeds.clear();
            		XMLitems.clear();
            		objectList.clear();
            	}
            	return true;  
            case R.id.item1:
            	wv2.setVisibility(View.GONE);
            	wv.setVisibility(View.VISIBLE);
            	return true;
            case R.id.item2:
            	wv.setVisibility(View.GONE);
            	wv2.setVisibility(View.VISIBLE);
            	return true;           	
            case R.id.item3:
            	wv.setVisibility(View.GONE);
            	wv2.setVisibility(View.GONE);
            case R.id.item4:
            	;
            default:
                return super.onOptionsItemSelected(item);
        }
	}
	

}//end of mainactivity
