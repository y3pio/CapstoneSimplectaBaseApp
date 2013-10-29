package com.example.simplectarssreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.util.Log;

public class HtmlFile {//this class is only used for debugging/testing purposes
	final static String TAG = "HtmlFile";
	
	String filename;
	
	protected HtmlFile(){
		filename = "HtmlFile";
	}
	protected HtmlFile(String calledFrom){
		filename = MainActivity.URLtoLoad;
		filename = filename.replaceAll("[^A-Za-z0-9]", "");
		if (filename.substring(0,4).matches("http")){
			filename = filename.substring(4);
		}
		if (filename.length() > 40){
			filename = filename.substring(0,39);
		}
		filename = calledFrom.substring(0,1)+filename;
	}
	
	protected void makeHTMLFile(String html){
		try{
		   	Log.d(TAG, "start write html to file :" + filename);
			Log.d(TAG, "opening html file ok");
			File htmlfilehandle = new File(MainActivity.filesDir, filename + ".txt");
			htmlfilehandle.delete(); //replace previous file if it existed
			Log.d(TAG, "htmlfilehandle is: " + htmlfilehandle);
			FileWriter htmlfile = new FileWriter(htmlfilehandle, true);
			PrintWriter out = new PrintWriter(htmlfile);
		    out.printf("%s", html);
		    htmlfile.close();
		    Log.d(TAG, "exiting html file okay");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "failed to write html to file");
		}
	}

}
