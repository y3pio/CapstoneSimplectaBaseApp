package com.example.simplectarssreader;

/*
 * use this filter for logcat to get rid of message with those tags
 * tag:^(?!(Choreographer|dalvikvm|libEGL|EGL_emulation|cutils-trace|TilesManager))
 */

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity implements OnClickListener, SearchView.OnQueryTextListener{
	final static String TAG = "MainActivity";
	
	public static String URLtoLoad;
	public static File filesDir;
	
	public static WebView wvMain, wvUser, wvManageFeeds;
	
	public static Button manageFeedsDone, manageFeedsAdd, loginButton, createAccButton;
	public static ImageView loadText, loginImage;
	public static TextView UsernameText, PasswordText;
	public static EditText manageFeedsEditText, userNameEditText, passwordEditText, googlePinEditText;

	public static boolean isLoggedIn, isRefresh, isAddRSSFeed, isPrefChanged;
	
	public static boolean loaded_feeds, loaded_managefeeds, loaded_main;
	public static ArrayList<String> loaded_individualfeed;
	public static ArrayList<Boolean> isParseComplete;
	
	public static ArrayList<ParsedMain> feeds;
	public static ArrayList<ParseSubscriptions> objectList;
	public static ArrayList<ArrayList<ParsedXML>> XMLitems;
	
	public static String currPage, prevPage;
	
	Context context;
	Activity activity;
	
	Menu mainMenu;
	SharedPreferences prefs;
	OnSharedPreferenceChangeListener listener;
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		Log.d(TAG, "-------------START APP----------");
		
		context = this;
		activity = MainActivity.this;
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				Log.d(TAG, "pref change: " + key);
				if (key.equals("preload_checkbox") || key.equals("short_summary_checkbox")){
					Log.d(TAG, "isPrefChanged = true");
					isPrefChanged = true;
				}
			}
		};
		prefs.registerOnSharedPreferenceChangeListener(listener);
		
		loaded_main = false;
		loaded_feeds = false;	//check if mainpage is loaded
		loaded_managefeeds = false;	//check if feed list from managefeed is loaded
		
		feeds = new ArrayList<ParsedMain>(); 		//parsed main simplecta page 
		objectList = new ArrayList<ParseSubscriptions>();	//parsed subscription list
		XMLitems = new ArrayList<ArrayList<ParsedXML>>();	//parsed xmls of all rss subscriptions
		isParseComplete = new ArrayList<Boolean>();
		
	/*	
		Log.d(TAG, "create file directory");
		File sdCard = Environment.getExternalStorageDirectory();
        filesDir = new File (sdCard.getAbsolutePath() + "/Android/data/com.example.simplectarssreader");
        filesDir.mkdirs();
	*/	
		
		isRefresh = false;
		isLoggedIn = false;
		isAddRSSFeed = false;
		isPrefChanged = false;
        
		loadText = (ImageView) findViewById(R.id.LoadText);
		loginImage = (ImageView) findViewById(R.id.loginImage);

		userNameEditText = (EditText) findViewById(R.id.editTextUserName);
		passwordEditText = (EditText) findViewById(R.id.editTextPassword);
		PasswordText = (TextView) findViewById(R.id.textViewPassword);
		UsernameText = (TextView) findViewById(R.id.textViewUserName);
		loginButton = (Button) findViewById(R.id.buttonLogin);
		loginButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Log.d(TAG, userNameEditText.getText().toString());
				new getPage(context, activity).SimplectaLogIn(userNameEditText.getText().toString(),passwordEditText.getText().toString());
				passwordEditText.setText("");
			}
		});
		createAccButton = (Button) findViewById(R.id.buttonCreateAccount);
		createAccButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				String url = "https://accounts.google.com/SignUp?service=ah&continue=https%3A%2F%2Fappengine.google.com%2F_ah%2Fconflogin%3Fcontinue%3Dhttp%3A%2F%2Fsimplecta.appspot.com%2F";
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});
		
		wvUser = (WebView) findViewById(R.id.MainActivity_WebView_User);
		wvUser.getSettings().setJavaScriptEnabled(true);
		wvUser.setWebViewClient(new RSSReaderClient(this, TAG, activity));
		wvUser.addJavascriptInterface(new JavaScriptInterface(context, TAG), "Android");
		
		wvMain = (WebView) findViewById(R.id.MainActivity_WebView_Main);
		wvMain.getSettings().setJavaScriptEnabled(true);
		wvMain.setWebViewClient(new WebViewClient());
		
		wvManageFeeds = (WebView) findViewById(R.id.MainActivity_WebView_Manage);
		wvManageFeeds.getSettings().setJavaScriptEnabled(true);
		wvManageFeeds.setWebViewClient(new RSSReaderClient(this, TAG, activity));
		manageFeedsDone = (Button) findViewById(R.id.done_button);
		manageFeedsDone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				new ViewSwapper(context).display("main");
			}
		});
		manageFeedsAdd = (Button) findViewById(R.id.addfeed_button);
		manageFeedsAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addRSSFeed();
			}
		});
		/*
		new ViewSwapper(context).display("load");	
		new getPage(context, activity).SimplectaLogIn();
		*/
		new ViewSwapper(context).test();
	}//end of oncreate
		
	public void markReadAll(){
		Log.d(TAG, "markReadAll()");
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
		wvMain.loadUrl("http://simplecta.appspot.com/");		
	}
	
	public void clearAll(){
		Log.d(TAG, "clearAll()");
		new ViewSwapper(context).display("login");
		MainActivity.wvMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (feeds.size() > 0){
					Log.d(TAG, Integer.toString(feeds.size()));
					wvMain.loadUrl("javascript:$(\".ajax_link\").click();");
					clearAll();
				}	
				else {
					String html = new BuildView(context,TAG).buildMainPage();
					wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
					new ViewSwapper(context).display("feeds");
				}
			}		
		});  
		wvMain.loadUrl("http://simplecta.appspot.com/");		
	}
	
	public void newRSSFeed(final String rssURL){
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
					Log.d(TAG, fillForm);
					webview.loadUrl(fillForm + submitForm);
				}
				else if (loaded == true){
					Log.d(TAG, "loaded = true");
					if (url.contains("/addRSS/?")){
						Log.d(TAG, "failed to add rss feed");
						Log.d(TAG, url);
						Toast toast = Toast.makeText(context,"Failed to subscribe to rss feed", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					else {
						Log.d(TAG, "successfully added new rss feed");
						loaded_feeds = false;
						loaded_managefeeds = false;
						Toast toast = Toast.makeText(context, "Subscribe successful", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						if (isAddRSSFeed = true){
							manageFeedsEditText.setText("http://");
						}
					}
				}
			}		
		});
		
		wvMain.loadUrl(getString(R.string.simplecta_managefeeds_URL));	
	}
	
	public void addRSSFeed(){
		Log.d(TAG, "addRSSFeed()");
		isAddRSSFeed = true;
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		manageFeedsEditText = input;
		input.setText("http://"); 
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("New RSS Feed");
    	builder.setMessage("Type in the RSS Feed URL and tap 'Subscribe' to subscribe");
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
    	addRSS.setCanceledOnTouchOutside(false);
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
                input.setText("http://");              
            }
        });
    	addRSS.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){            
            @Override
            public void onClick(View v){
            	isAddRSSFeed = false;
                if (loaded_managefeeds == false){
                	refresh();
                }
                addRSS.dismiss();
            }
        });
	}
	
	public void manageFeeds(){
		Log.d(TAG, "manageFeeds()");
		if (loaded_managefeeds == false){
			Log.d(TAG, "simplecta feeds list needs to be loaded");
			new ViewSwapper(context).display("load");
			new getPage(context, activity).getSimplectaFeedsList();
		}
		else {
			wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
	    	new ViewSwapper(context).display("managefeeds");
		}
	}
	
	public void refresh(){
		Log.d(TAG, "refresh()");	
		if (currPage == "load" || currPage == "login"){
			new getPage(context, activity).SimplectaLogIn();
			return ;
		}
		else {
			prevPage = currPage;
			isRefresh = true;
			new ViewSwapper(context).display("load");
			
			if (prefs.getBoolean("preload_checkbox", true) == true){
				new getPage(context,activity).getSimplectaMain();
			}
			else {
				if (prevPage.equals("main") || prevPage.equals("descMain")){
					new getPage(context, activity).getSimplectaMain();
				}
				else if (prevPage.contains("descFeed-") || prevPage.contains("feed-")){
					String channelTitle = "";
					if (prevPage.contains("descFeed-")){
						channelTitle = MainActivity.prevPage.substring(9);
					}
					else if (prevPage.contains("feed-")){
						channelTitle = MainActivity.prevPage.substring(5);
					}
					
					int removeThis = -1;
					for (int i=0; i<XMLitems.size(); i++){
						if (XMLitems.get(i).get(0).getChannelTitle().equals(channelTitle)){
							removeThis = i;
						}
					}
					
					if (removeThis == -1){
						Log.d(TAG, "error, xml not in xml item");
						new getPage(context, activity).getSimplectaMain();
						return ;
					}
					else {
						XMLitems.remove(removeThis);
					}
					
					Boolean found = false;
					for (int i=0; i<objectList.size(); i++){
						if (objectList.get(i).getChannelTitle().equals(channelTitle)){
							found = true;
							String cleanedChannelXMLLink = new Parse(context, activity).clean(objectList.get(i).getChannelLink().substring(7));
							new RequestTask(context, activity, 2).execute(cleanedChannelXMLLink);
						}
					}
					
					if (found == false){
						Log.d(TAG, "error, xml link not in objeclist");
						new getPage(context, activity).getSimplectaMain();
					}
					
				}
				else if (prevPage.equals("managefeeds")){
					new getPage(context, activity).getSimplectaFeedsList();
				}
				else {
					Log.d(TAG, "unhandled back: " + prevPage);
				}
			}
		}
	}
	
	public void newest(int howMany){
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	Log.d(TAG, "onKeyDown(back)");
	    	if (currPage == "login"){
	    		Log.d(TAG, "confirm exit");
	    		new AlertDialog.Builder(this)
	            .setIcon(android.R.drawable.ic_dialog_alert)
	            .setTitle("Quit the App?")
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    MainActivity.this.finish();    
	                }
	            })
	            .setNegativeButton("No", null)
	            .show();
	    	}
	    	else if (currPage.equals("main")){
	    		Log.d(TAG, "logout or exit");
	    		new AlertDialog.Builder(this)
		        	.setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle("Quit the App?")
		            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		            	public void onClick(DialogInterface dialog, int which) {
		            		MainActivity.this.finish();    
		            	}
		            })
		            .setNegativeButton("No", null)
		            .show();
	    	}
	    	else if (currPage.equals("managefeeds")){
	    		Log.d(TAG, "go back to simplecta main page");
	    		String html = new BuildView(context, TAG).buildMainPage();
    			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
    			new ViewSwapper(context).display("main");
	    	}
	    	else if (currPage.equals("descMain")){
	    		Log.d(TAG, "go back to simplecta main page");
	    		String html = new BuildView(context, TAG).buildMainPage();
    			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
    			new ViewSwapper(context).display("main");
	    	}
	    	else if (currPage.contains("descFeed-")){
	    		String channelTitle = currPage.substring(9);
	    		int xmlIndex = -1;
	    		for (int i=0; i<XMLitems.size(); i++){
	    			if (XMLitems.get(i).get(0).getChannelTitle().equals(channelTitle)){
	    				xmlIndex = i;
	    			}
	    		}
	    		if (xmlIndex != -1){
	    			String html = new BuildView(context, TAG).buildMainPageFeed(xmlIndex);
	    			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
	    			new ViewSwapper(context).display("feed-" + channelTitle);
	    		}
	    		else {
	    			Log.d(TAG, "could not find index for descFeed- in xml");
	    			Toast toast = Toast.makeText(context, "ERROR", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					String html = new BuildView(context, TAG).buildMainPage();
	    			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
	    			new ViewSwapper(context).display("main");
	    		}
	    	}
	    	else if (currPage.contains("feed-")){
	    		new ViewSwapper(context).display("managefeeds");
	    	}
	    	else {
	    		Log.d(TAG, "unhandled event on back, currPage = " + currPage);
	    		String html = new BuildView(context,TAG).buildMainPage();
	    		MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
	    		new ViewSwapper(context).display("main");
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
        switch (item.getItemId()) {
        	case R.id.action_search_icon:
        		;
        	return true;
            case R.id.action_settings:
               Intent settings = new Intent(this, SettingsActivity.class);
               startActivity(settings);
               return true;
            case R.id.action_logout:
            	//new getPage(context, activity).SimplectaLogOut();
            	new getPage(context, activity).SimplectaLogIn();
            	return true; 
            case R.id.action_manage:
            	manageFeeds();
            	return true;
            case R.id.action_reload:
            	refresh();
            	return true;
            case R.id.action_markAllRead:
            	markReadAll();
            	return true;
            case R.id.item1:
            	new ViewSwapper(context).display("login");
            	return true;
            case R.id.item2:
            	new ViewSwapper(context).display("main");
            	return true;
            case R.id.item3:
            	new ViewSwapper(context).display("managefeeds");
            	return true;   
            case R.id.item4:
            	new ViewSwapper(context).display("load");
            	return true; 
            case R.id.item5:
            	new ViewSwapper(context).test();
            	return true; 
            default:
                return super.onOptionsItemSelected(item);
        }
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {/*
		// TODO Auto-generated method stub
		String indexPage = null;
        indexPage = "<head><title>Simplecta RSS</title><link rel=\"stylesheet\" media=\"all\" href=\"style.css\" type=\"text/css\"></head>"
                        +        "<body><div class=\"wrap\"><div class=\"content\">"; 
        //This takes a while to load. Links somehow not working
        for(int i=0; i<MainActivity.feeds.size(); i++){
                //System.out.println(MainActivity.feeds.get(i).getURL());
                if(MainActivity.feeds.get(i).getDesc().contains(query) || MainActivity.feeds.get(i).getItemURL().contains(query) || 
                                MainActivity.feeds.get(i).getCategory().contains(query)){
                indexPage+="<article class=\"underline\"><div class=\"post-content\"><h2><a href=\""+MainActivity.feeds.get(i).getItemURL()
                +"\" /a>" + MainActivity.feeds.get(i).getDesc() +"</a></h2><p>" + MainActivity.feeds.get(i).getCategory()    
                +"</p></div><div class=\"clear\"></div></article>";
                }
        }
        indexPage+="</div></body></html>";
        new ViewSwapper(context).display("feeds");
        MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", indexPage, "text/html", "UTF-8", null);*/
		return false;
	}

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public void onPause(){
	    super.onPause();
	    Log.d(TAG, "onPause()");
	}

	@Override
	public void onResume(){
	    super.onResume();
	    Log.d(TAG, "onResume()");
	    if (isPrefChanged == true){
	    	Log.d(TAG, "pref has been changed");
	    	isPrefChanged = false;
	    	refresh();    	
	    }
	}
	/*
	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		height = loginImage.getMeasuredHeight(); 
	   	width = loginImage.getMeasuredWidth(); 
	   	Log.d(TAG, Integer.toString(height) + "," + Integer.toString(width));
	   	
	   	float editTextWidth = .30f*width;
		
		MainActivity.userNameEditText.getLayoutParams().width = (int) editTextWidth;
		MainActivity.passwordEditText.getLayoutParams().width = (int) editTextWidth;
		
		float editTextLeft = .30f*width;
		float userNameTop = .64f*height;

		RelativeLayout.LayoutParams paramsUserName = (RelativeLayout.LayoutParams) MainActivity.userNameEditText.getLayoutParams();
        paramsUserName.leftMargin = (int) editTextLeft;
        paramsUserName.topMargin = (int) userNameTop;
        MainActivity.userNameEditText.setLayoutParams(paramsUserName);
        
		float passwordTop = .72f*height; 
        RelativeLayout.LayoutParams paramsPassword = (RelativeLayout.LayoutParams) MainActivity.passwordEditText.getLayoutParams();
        paramsPassword.leftMargin = (int) editTextLeft;
        paramsPassword.topMargin = (int) passwordTop;
        MainActivity.passwordEditText.setLayoutParams(paramsPassword);
        
        float loginTop = .78f*height;
        RelativeLayout.LayoutParams paramsLoginButton = (RelativeLayout.LayoutParams) MainActivity.loginButton.getLayoutParams();
        paramsLoginButton.leftMargin = (int) editTextLeft;
        paramsLoginButton.topMargin = (int) loginTop;
        MainActivity.loginButton.setLayoutParams(paramsLoginButton);
	}
*/
	
}//end of mainactivity
