package com.example.simplectarssreader;

//use this filter for logcat to get rid of message with those tags
//tag:^(?!(Choreographer|dalvikvm|libEGL|EGL_emulation|cutils-trace))

/*
 * to do list:
x* create managefeed
 *  display individual feed page on [view] click, xmlget if not already loaded
x*  unsub, adjust display, remove from arraylist
x* create addrssfeeds
x*  successful subscribe rss 
 *   needs to xmlget rss feed, update display, clear textbox
 *   currently calls refresh as lazy method
 * 	unsuccessful subscribe needs warn and/or correction
x* create refresh
 *  preload = true
x*   handle refresh from main page
x*   handle refresh from manage
 *   handle refresh from individual feed
 *  preload = false
x*   handle refresh from main page
x*   handle refresh from manage
 *   handle refresh from individual feed
x* create a function in parse that will fix all the html garbage for example %2f = /
x* create markallread
 * handle back
 */
import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener, SearchView.OnQueryTextListener{
	final static String TAG = "MainActivity";
	
	public static File filesDir;
	
	public static WebView wvMain, wvUser, wvManageFeeds;
	
	public static Button manageFeedsDone, manageFeedsAdd;
	public static TextView loadText;

	public static boolean isLoggedIn, isRefresh;
	
	public static boolean loaded_feeds, loaded_managefeeds;
	public static ArrayList<String> loaded_individualfeed;
	
	public static ArrayList<ParsedMain> feeds;
	public static ArrayList<ParseSubscriptions> objectList;
	public static ArrayList<ArrayList<ParsedXML>> XMLitems;
	public static ArrayList<Boolean> isParseComplete;
	
	public static String URLtoLoad, currPage, feedsPage, prevPage;
	
	Context context;
	Activity activity;
	
	Menu mainMenu;
	SharedPreferences prefs;
	//PreferenceManager.getDefaultSharedPreferences(c).getBoolean("sound_enable_checkbox", true) == true
	
	@Override	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		Log.d(TAG, "-------------START APP----------");
		
		context = this;
		activity = MainActivity.this;
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		
		loaded_feeds = false;	//check if mainpage is loaded
		loaded_managefeeds = false;	//check if feed list from managefeed is loaded
		loaded_individualfeed = new ArrayList<String>();	//string of title of rss feed, track which are loaded
		
		feeds = new ArrayList<ParsedMain>(); 		//parsed main simplecta page 
		objectList = new ArrayList<ParseSubscriptions>();	//parsed subscription list
		XMLitems = new ArrayList<ArrayList<ParsedXML>>();	//parsed xmls of all rss subscriptions
		
	/*	
		Log.d(TAG, "create file directory");
		File sdCard = Environment.getExternalStorageDirectory();
        filesDir = new File (sdCard.getAbsolutePath() + "/Android/data/com.example.simplectarssreader");
        filesDir.mkdirs();
	*/	
		isRefresh = false;
		isLoggedIn = false;
        
		loadText = (TextView) findViewById(R.id.LoadText);
		
		wvUser = (WebView) findViewById(R.id.MainActivity_WebView_User);
		wvUser.getSettings().setJavaScriptEnabled(true);
		wvUser.setWebViewClient(new RSSReaderClient(this, TAG));
		wvUser.addJavascriptInterface(new JavaScriptInterface(context, TAG), "Android");
		
		wvMain = (WebView) findViewById(R.id.MainActivity_WebView_Main);
		wvMain.getSettings().setJavaScriptEnabled(true);
		wvMain.setWebViewClient(new WebViewClient());
		
		wvManageFeeds = (WebView) findViewById(R.id.MainActivity_WebView_Manage);
		wvManageFeeds.getSettings().setJavaScriptEnabled(true);
		wvManageFeeds.setWebViewClient(new RSSReaderClient(this, TAG));
		manageFeedsDone = (Button) findViewById(R.id.done_button);
		manageFeedsDone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				new getPage(context, TAG, activity).display("feeds");
			}
		});
		manageFeedsAdd = (Button) findViewById(R.id.addfeed_button);
		manageFeedsAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addRSSFeed();
			}
		});

		new getPage(context, TAG, activity).display("load");	
		new getPage(context, TAG, activity).SimplectaLogIn();
		
	}//end of oncreate
	
	public void markReadAll(){
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {
			boolean marked = false;
			@Override
			public void onPageFinished(WebView view, String url) {
				if (marked == false){
					marked = true;
					wvMain.loadUrl("javascript:$(\".ajax_link\").click();");
					refresh();
				}			
			}		
		});  
		wvMain.loadUrl("simplecta.appspot.com/all/");
		
	}
	
	public void newRSSFeed(String rssURL){
		Log.d(TAG, "newRSSFeed()");
		final String fillForm = "javascript:void(document.forms[1].url.value=\"" + rssURL + "\");";
		final String submitForm = "javascript:(document.forms[1].submit());";

		wvMain.setWebViewClient(new WebViewClient(){
			boolean loaded = false;
			@Override
			public void onPageFinished(WebView webview, String url){
				if (loaded == false){
					Log.d(TAG, "loaded = false");
					Log.d(TAG, "loadUrl(" + fillForm + submitForm + ")");
					loaded = true;
					webview.loadUrl(fillForm + submitForm);
				}
				else if (loaded == true){
					Log.d(TAG, "loaded = true");
					if (url.contains("/addRSS/?")){
						Log.d(TAG, "failed to add rss feed");
						Toast.makeText(context, "Failed to subscribe to rss feed", Toast.LENGTH_LONG).show();
					}
					else {
						Log.d(TAG, "successfully added new rss feed");
						loaded_feeds = false;
						loaded_managefeeds = false;
						refresh();
					}
				}
			}		
		});
		
		wvMain.loadUrl(getString(R.string.simplecta_managefeeds_URL));	
	}
	
	public void addRSSFeed(){
		final EditText input = new EditText(this);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("New RSS Feed");
    	//builder.setMessage("Message");
    	builder.setView(input);
    	builder.setNegativeButton("Subscribe", new DialogInterface.OnClickListener(){
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
	
	public void manageFeeds(){
		Log.d(TAG, "manageFeeds()");
		if (loaded_managefeeds == false){
			Log.d(TAG, "simplecta feeds list needs to be loaded");
			new getPage(context, TAG, activity).display("load");
			new getPage(context, TAG, activity).getSimplectaFeedsList();
		}
		wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
    	new getPage(context, TAG, activity).display("managefeeds");
	}
	
	public void refresh(){
		prevPage = currPage;
		isRefresh = true;
		new getPage(context,TAG,activity).display("load");
		if (prefs.getBoolean("preload_checkbox", false) == true){
			new getPage(context,TAG, activity).getSimplectaMain();
		}
		else if (prefs.getBoolean("preload_checkbox", false) == false){
			if (currPage == "feeds"){
				if (feedsPage == "simple"){
					new getPage(context,TAG, activity).getSimplectaMain();
				}
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	if (currPage == "login"){
	    		Log.d(TAG, "confirm exit");
	    	}
	    	else if (currPage == "feeds"){
	    		if (feedsPage.contains("feed-")){
	    			Log.d(TAG, "go back to simplecta main page");
	    		}
	    		else {
	    			Log.d(TAG, "logout of exit");
	    		}
	    	}
	    	else if (currPage == "managefeeds"){
	    		Log.d(TAG, "go back to simplecta main page");
	    	}
	    	else {
	    		Log.d(TAG, "unhandled event on back");
	    	}
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mainMenu = menu;
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search_icon).getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
		Log.d("SEARCH TAG", "CLICKED OPTIONS!!");
        switch (item.getItemId()) {
        	case R.id.action_search_icon:
        	Toast.makeText(this, "HELLO SEARCH!!", Toast.LENGTH_SHORT).show();
        	return true;
            case R.id.action_settings:
               Intent settings = new Intent(this, SettingsActivity.class);
               startActivity(settings);
               return true;
            case R.id.action_logout:
            	new getPage(context, TAG, activity).SimplectaLogOut();
            	return true; 

            case R.id.action_manage:
            	wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
            	new getPage(context, TAG, activity).display("managefeeds");
            	return true;
            case R.id.action_reload:
            	//new getPage(context,TAG,activity).display("load");
            	//new getPage(context, TAG, activity).getSimplectaMain();
            	refresh();
            	return true;
            case R.id.action_markAllRead:
            	markReadAll();
            	return true;
            case R.id.item1:
            	new getPage(context, TAG, activity).display("login");
            	return true;
            case R.id.item2:
            	new getPage(context, TAG, activity).display("feeds");
            	return true;
            case R.id.item3:
            	new getPage(context, TAG, activity).display("managefeeds");
            	return true;   
            case R.id.item4:
            	new getPage(context, TAG, activity).display("load");
            	return true; 
            case R.id.item5:
            	return true; 
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        +        "<body><div class=\"wrap\"><div class=\"content\">"; 
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
        new getPage(context, TAG, activity).display("feeds");
        MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", indexPage, "text/html", "UTF-8", null);
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	
}//end of mainactivity
