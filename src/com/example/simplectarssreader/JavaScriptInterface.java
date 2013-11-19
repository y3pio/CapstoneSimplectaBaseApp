package com.example.simplectarssreader;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
	final static String TAG = "JavaScriptInterface";
	
	Context context;
	String calledFrom, function;
	
	protected JavaScriptInterface(){
		context = null;
		calledFrom = "";
		function = "";
	}
	
	protected JavaScriptInterface(Context c, String fromActivity){
		context = c;
		calledFrom = fromActivity;
		function = "";
	}
	
	protected JavaScriptInterface(Context c, String fromActivity, String fn){
		context = c;
		calledFrom = fromActivity;
		function = fn;
	}
	
	@SuppressWarnings("unused")
	@JavascriptInterface
    public void processHTML(String html){
		
    }
	
	@JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
	
	
}


