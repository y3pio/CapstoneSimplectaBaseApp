package com.example.simplectarssreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/*
 * code by Konstantin Burov
 * taken from: http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
 */
class RequestTask extends AsyncTask<String, String, String>{
	final static String TAG = "RequestTask";
	String htmlString, fromActivity;
	boolean isLoggedIn;
	Context context;
	Activity activity;
	String chTitle;
	
	protected RequestTask(Context c, String calledFrom, Activity a){
		context = c;
		fromActivity = calledFrom;
		activity = a;
		//Log.d(TAG, "---------RequestTask---------");
	}
	
	@Override
    protected String doInBackground(String... uri) {
		Log.d(TAG, "AsyncTask, url = " + uri[0]);
		MainActivity.URLtoLoad = uri[0];
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = "";
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        	Log.d(TAG, "Failed: ClientProtocolException");
        } catch (IOException e) {
            //TODO Handle problems..
        	Log.d(TAG, "Failed: IOException");
        }
        
        htmlString = responseString;
        return responseString;    
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute()");
        
		XMLParse(htmlString);
		
		if (MainActivity.isLoadXML == true) {
			MainActivity.isLoadXML = false;
			MainActivity.isRefresh = false;
			Log.d(TAG, "get and load xml");
			activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
				public void run() {
					MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context,TAG).buildMainPageFeed(chTitle), "text/html", "UTF-8", null);
					new getPage(context, TAG).display("feeds");
				}
			});
		}
		else{
			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", false) == true){
				if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("detailed_mainpage_checkbox", false) == true){
					boolean finishedWait = false;
					
					int counter = 0;
					for (int i=0; i<MainActivity.isParseComplete.size(); i++){
						if (MainActivity.isParseComplete.get(i) == true){
							counter++;
						}
					}
					if (counter >= MainActivity.isParseComplete.size()){
						finishedWait = true;
					}		
					if (finishedWait == true){
						activity.runOnUiThread(new Runnable() {//use this because webview should be run on ui threads
							public void run() {
								if (MainActivity.isRefresh == false){
									Log.d(TAG, "refresh = false, build mainpagedetailed");
									MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildMainPageDetailed(), "text/html", "UTF-8", null);
									new getPage(context, TAG, activity).display("feeds");
								}
								else if (MainActivity.isRefresh == true){
									Log.d(TAG, "isRefresh");
									MainActivity.isRefresh = false;
									if (MainActivity.prevPage == "feeds"){
										if (MainActivity.feedsPage.contains("feed-")){
											MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildMainPageFeed(MainActivity.feedsPage.substring(5)), "text/html", "UTF-8", null);
										}
										else {
											MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildMainPageDetailed(), "text/html", "UTF-8", null);										
										}
										new getPage(context, TAG, activity).display("feeds");
									}
									if (MainActivity.prevPage == "managefeeds"){
										MainActivity.wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", new BuildView(context, TAG).buildManageFeedsPage(), "text/html", "UTF-8", null);
										new getPage(context, TAG, activity).display("managefeeds");
									}
								}
							}
						});			   
					}
				}
			}
		}
		
		
    }
    
    public void XMLParse(String html){//used to parse individual rss feeds
		String htmlString = html;
		
		int channelstart = htmlString.indexOf("<channel>");
		int channelend = htmlString.indexOf("<item>");
		
		String channelInfo = htmlString.substring(channelstart,channelend);
		String channelTitle = channelInfo.substring(channelInfo.indexOf("<title>")+7, channelInfo.indexOf("</title>"));
		chTitle = channelTitle;
		
		ArrayList<ParsedXML> xmlItems = new ArrayList<ParsedXML>();
		
		//System.out.println(channelTitle);
	/*	String channelLink = channelInfo.substring(channelInfo.indexOf("<link>")+6, channelInfo.indexOf("</link>"));
		System.out.println(channelLink);
		String channelDesc = channelInfo.substring(channelInfo.indexOf("<description>")+13, channelInfo.indexOf("</description>"));
		System.out.println(channelDesc);*/
		//System.out.println("--------------------");
		
		int bookmark = channelend;
		String title = "", link = "", desc = "";
		while (bookmark > -1){
			bookmark = htmlString.indexOf("<item>");
			if (bookmark > -1){
				int bookmarkend = htmlString.indexOf("</item>")+7;
				String item = htmlString.substring(bookmark,bookmarkend);
				
				String iTitle = item.substring(item.indexOf("<title>")+7, item.indexOf("</title>")).replace("\n", "");
				String iLink = item.substring(item.indexOf("<link>")+6, item.indexOf("</link>")).replace("\n", "");
				String iDesc = item.substring(item.indexOf("<description>")+13, item.indexOf("</description>")).replace("\n", "");
				
				while(iDesc.indexOf("&lt;") != -1){
					iDesc = iDesc.replace("&lt;", "<");
				}
				while(iDesc.indexOf("&#34;") != -1){
					iDesc = iDesc.replace("&#34;", "\"");
				}
				while(iDesc.indexOf("&gt;") != -1){
					iDesc = iDesc.replace("&gt;", ">");
				}
				while(iDesc.indexOf("%3a") != -1){
					iDesc = iDesc.replace("%3a", ":");
				}
				while(iDesc.indexOf("%2f") != -1){
					iDesc = iDesc.replace("%2f", "/");
				}

				xmlItems.add(new ParsedXML(iLink, iDesc, iTitle, channelTitle));
				
				htmlString = htmlString.substring(bookmarkend);
			}
		}	
		MainActivity.XMLitems.add(xmlItems);
		
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preload_checkbox", false) == true){
			//checklist for all requesttasks, keeps track of which request tasks are finished for loadMainPageDetailed()
			boolean marked = false;
			for (int i=0; i<MainActivity.isParseComplete.size(); i++){
				if (marked == false){
					if (MainActivity.isParseComplete.get(i) == false){
						MainActivity.isParseComplete.set(i, true);
						marked = true;
					}
				}
			}
		}
		
		MainActivity.loaded_individualfeed.add(channelTitle);
		
		Log.d(TAG, "XMLParse() done");
	}
    
    
    
}
