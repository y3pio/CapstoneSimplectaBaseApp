package com.example.simplectarss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;

public class parsingThread extends AsyncTask<String, Void, Integer> {
	
	@SuppressLint("NewApi")
	@Override
	protected Integer doInBackground(String... htmls) {

		MainActivity.wv.setBackground(null);
		// TODO Auto-generated method stub
		int count = htmls.length;
		for(int i=0; i<count; i++){
			//Do parsing here
			//System.out.println(htmls[i]);
			parseMainHtml(htmls[i]); //For every urls passed, start parsing.
									 //For the time being, only main will be parsed.
		}
		return 1;
	}
	
	protected void onPostExecute(Integer result){
		System.out.println("HELLO POST EXECUTE!");
		/*for(int i=0; i<MainActivity.feeds.size(); i++)
			System.out.println(MainActivity.feeds.get(i).getURL());*/
		//Above code to test if it actually parsed. Test passed.
		//Build template below to start building the parsed items into the new html templates
		buildTemplate();
	}
	
	//This is Jason's parsing function.
	public void parseMainHtml(String html){
		String lines[] = html.split("\\r?\\n");
        String url = null, key = null, description = null, category = null, rssUrl = null;
        for(int i=0; i<lines.length; i++){                        
                if(lines[i].contains("class=\"feedlink\" href=\"")){
                        lines[i] = lines[i].substring(lines[i].indexOf("class=\"feedlink\" href=\"")+23);
                        int mark = lines[i].indexOf("\">");
                        int mark2 = lines[i].indexOf("</a>");
                        rssUrl = lines[i].substring(0,mark);
                        category = lines[i].substring(mark+2, mark2);                                
                }
                else if(lines[i].contains("class=\"read_link\" href=\"")){
                        lines[i] = lines[i].substring(lines[i].indexOf("class=\"read_link\" href=\"")+24);
                        int mark = lines[i].indexOf("\">");
                        key = lines[i].substring(0,mark);
                        int mark2 = lines[i].indexOf("</a>");
                        description = lines[i].substring(mark+2,mark2);
                        lines[i] = lines[i].substring(mark2);
                        lines[i] = lines[i].substring(lines[i].indexOf("class=\"peek\" href=\"")+19);
                        mark = lines[i].indexOf("\">");
                        url = lines[i].substring(0,mark);
                        MainActivity.feeds.add(new parseMain(url, key, description, category, rssUrl));
                }
        }
	}
	
	//Basically makes a new html string and loads it with the base directory that contains the CSS.
	public void buildTemplate(){
		MainActivity.wv.setVisibility(View.VISIBLE);
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
		MainActivity.wv.loadDataWithBaseURL("file:///android_asset/", indexPage, "text/html", "UTF-8", null);
	}
}
