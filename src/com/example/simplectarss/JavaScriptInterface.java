package com.example.simplectarss;

import android.R.string;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
	@JavascriptInterface
	public void processHTML(String html){
		//System.out.println(html);
		new parsingThread().execute(html); //Did the parsing inside an AsyncTask thread
	}
	
}
