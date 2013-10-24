package com.example.simplectarssreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/*
 * code by : shridutt kothari
 * from : http://stackoverflow.com/questions/5264162/how-to-retrieve-html-content-from-webview-as-a-string
 */
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
		
		//save html to a text file for debugging purposes
		HtmlFile writer = new HtmlFile(TAG);
		writer.makeHTMLFile(html);
		
		//parse here, please replace "your url here" with your url
		System.out.println("LOADING URL: " + MainActivity.URLtoLoad);
		/*if (MainActivity.URLtoLoad.contains("feeds")){
			System.out.println("HELLO BRIAN!!");
			new Parse(context, TAG, html).BrianParse();
		}
		else if (!(MainActivity.URLtoLoad.contains("feeds"))){
			System.out.println("HELLO JASON!!");
			new Parse(context, TAG, html).JasonParse();
		}
		
		else if (MainActivity.URLtoLoad == "your url here"){
			new Parse(context, TAG, html).YeParse();
		}*/
		
		if(MainActivity.URLtoLoad.compareTo("http://simplecta.appspot.com/feeds/")==0){
			System.out.println("HELLO BRIAN!!");
			new Parse(context, TAG, html).BrianParse();
		}
		else if(MainActivity.URLtoLoad.contains("simplecta.appspot.com")){
			System.out.println("HELLO JASON!!");
			new Parse(context, TAG, html).JasonParse();
		}
		
	
    }
	
	
}


