package com.example.simplectarssreader;

//tag:^(?!(Choreographer|dalvikvm))


import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements OnClickListener, SearchView.OnQueryTextListener {
	final static String TAG = "MainActivity";
	
	public static File filesDir;
	
	public static WebView wvMain, wvUser;

	public static boolean isLoggedIn, isLogInFinished;
	
	public static ArrayList<ParsedMain> feeds;
	public static ArrayList<ParsedFeed> parsedItems;
	public static ArrayList<ParseSubscriptions> objectList;
	public static ArrayList<ParsedXML> XMLitems;
	
	public static String URLtoLoad, simplectaMain, simplectaFeedsList;
	
	public static boolean check;
	
	private SearchView mSearchView;
	
	Context context;
	
	@Override	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
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
        
		wvUser = (WebView) findViewById(R.id.MainActivity_WebView_User);
		wvUser.getSettings().setJavaScriptEnabled(true);
		wvUser.setWebViewClient(new RSSReaderClient(this, TAG));
		wvUser.addJavascriptInterface(new JavaScriptInterface(context, TAG), "USER");
		wvUser.setVisibility(View.GONE);
		
		wvMain = (WebView) findViewById(R.id.MainActivity_WebView_Main);
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
					wvMain.setVisibility(View.GONE);
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				String urlHost = url.substring(0,url.indexOf(".com/")+4);
				if (urlHost.contains("simplecta")){
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
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search_icon).getActionView();
        searchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search_icon:
            	Toast.makeText(this, "HELLO SEARCH!!", Toast.LENGTH_SHORT).show();
            	return true;
            case R.id.action_settings:
                 //   Intent settings = new Intent(this, SettingsActivity.class);
                 //   startActivity(settings);
               return true;
            case R.id.action_logout:
            	SimplectaLogOut();
            	return true;  
            case R.id.item1:
            	addRSSFeed();

            	
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
			+"\" /a>" + MainActivity.feeds.get(i).getDesc() +"</a></h2><p>" + MainActivity.feeds.get(i).getCategory()
			/*+"<input type=\"checkbox\" name=\"markBox\" value=\"markRead\">"*/	
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
	
	public void newRSSFeed(String rssURL){
Log.d(TAG, "newRSSFeed()");
		final String fillForm = "javascript:void(document.forms[1].url.value=\"" + rssURL + "\");";
		final String submitForm = "javascript:(document.forms[1].submit());";
Log.d(TAG, "fillForm = " + fillForm);
Log.d(TAG, "submitForm = " + submitForm);

		wvMain.setWebViewClient(new WebViewClient(){
			boolean loaded1 = false;
			boolean loaded2 = false;
			@Override
			public void onPageFinished(WebView webview, String url){
				if (loaded1 == false){
					Log.d(TAG, "loaded = false");
					Log.d(TAG, "loadUrl(" + fillForm + submitForm + ")");
					loaded1 = true;
					webview.loadUrl(fillForm + submitForm);
				}
				else if (loaded1 == true){
					Log.d(TAG, "loaded = true");
					if (url.contains("/addRSS/?")){
						Log.d(TAG, "failed to add rss feed");
					}
					else {
						Log.d(TAG, "successfully added new rss feed");
					}
				}
			}		
		});
		
		wvMain.loadUrl(getString(R.string.simplecta_managefeeds_URL));
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
	/*	case R.id.okButton:
			newRSSFeed(textBox.getText().toString());
			subscribeOK();*/
		}
		
	}
	
	public void addRSSFeed(){
		final EditText input = new EditText(this);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Title");
    	builder.setMessage("Message");
    	builder.setView(input);
    	builder.setNegativeButton("Add", new DialogInterface.OnClickListener(){
    		@Override
            public void onClick(DialogInterface dialog, int which){
              	;
            }
        });
    	builder.setNeutralButton("Clear", new DialogInterface.OnClickListener(){
    		@Override
            public void onClick(DialogInterface dialog, int which){
              	;
            }
        });
    	builder.setPositiveButton("Done", new DialogInterface.OnClickListener(){
    		@Override
            public void onClick(DialogInterface dialog, int which){
              	;
            }
        });
    	
    	final AlertDialog addRSS = builder.create();
    	addRSS.show();
    	addRSS.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){            
            @Override
            public void onClick(View v){
                Editable value = input.getText();
          	  	newRSSFeed(value.toString());                
            }
        });
    	addRSS.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener(){            
            @Override
            public void onClick(View v){
                input.setText("");              
            }
        });
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		String indexPage = null;
		indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
				+	"<body><div class=\"wrap\"><div class=\"content\">"; 
		//This takes a while to load. Links somehow not working
		for(int i=0; i<MainActivity.feeds.size(); i++){
			//System.out.println(MainActivity.feeds.get(i).getURL());
			if(MainActivity.feeds.get(i).getDesc().contains(query) || MainActivity.feeds.get(i).getURL().contains(query) || 
					MainActivity.feeds.get(i).getCategory().contains(query)){
			indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2><a href=\""+MainActivity.feeds.get(i).getURL()
			+"\" /a>" + MainActivity.feeds.get(i).getDesc() +"</a></h2><p>" + MainActivity.feeds.get(i).getCategory()
			/*+"<input type=\"checkbox\" name=\"markBox\" value=\"markRead\">"*/	
			+"</p></div><div class=\"clear\"></div></article>";
			}
		}
		indexPage+="</div></body></html>";
		MainActivity.wvUser.setVisibility(View.VISIBLE);
		MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", indexPage, "text/html", "UTF-8", null);
		return false;
	}
	
	

}//end of mainactivity
