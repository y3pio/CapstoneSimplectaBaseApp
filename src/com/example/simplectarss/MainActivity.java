package com.example.simplectarss;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;


public class MainActivity extends Activity {
	
	public static WebView wv;
	public static ArrayList<parseMain> feeds = new ArrayList<parseMain>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wv = (WebView) findViewById(R.id.main_webview);
		wv.setWebViewClient(new RSSWebViewClient());
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
		wv.loadUrl("http://simplecta.appspot.com");			//Load the url that is to be parsed
															//Mostly pretty standard procedure to do javascript inject to get html source
															//Webview by default is hidden. Will only show if it hits google login page
															//More datail inside out custom webview client
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
