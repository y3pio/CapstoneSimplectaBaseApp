package com.example.simplectarssreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

class RequestTask extends AsyncTask<String, String, String>{
	final static String TAG = "RequestTask";
	Context context;
	Activity activity;
	int task;
	
	protected RequestTask(Context c, Activity a){
		context = c;
		activity = a;
		task = -1;
	}
	protected RequestTask(Context c, Activity a, int t){
		context = c;
		activity = a;
		task = t;
	}
	
	@Override
    protected String doInBackground(String... uri) {
		Log.d(TAG, "AsyncTask, url = " + uri[0]);
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
        
        return responseString;    
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute()");
        
		new Parse(context, activity).XMLParse(result);
			
		if (task == 1){//called by xmlgetall() = preload pref = true
			boolean marked = false;
			for (int i=0; i<MainActivity.isParseComplete.size(); i++){
				if (marked == false){
					if (MainActivity.isParseComplete.get(i) == false){
						MainActivity.isParseComplete.set(i, true);
						marked = true;
					}
				}
			}
			
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
				activity.runOnUiThread(new Runnable() {
					public void run() {
						if (MainActivity.isRefresh == false){
							Log.d(TAG, "task = 1, refresh = false, display main page");
							String html = new BuildView(context,TAG).buildMainPage();
							MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
							new ViewSwapper(context).display("main");
						}
						else if (MainActivity.isRefresh == true){
							Log.d(TAG, "task = 1, refresh = TRUE, display prev page = " + MainActivity.prevPage);
							MainActivity.isRefresh = false;
							if (MainActivity.prevPage.equals("main") || MainActivity.prevPage.equals("descMain")){
								String html = new BuildView(context,TAG).buildMainPage();
								MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
								new ViewSwapper(context).display("main");
							}
							else if (MainActivity.prevPage.contains("descFeed-") || MainActivity.prevPage.contains("feed-")){
								String channelTitle = "";
								if (MainActivity.prevPage.contains("descFeed-")){
									channelTitle = MainActivity.prevPage.substring(9);
								}
								else if (MainActivity.prevPage.contains("feed-")){
									channelTitle = MainActivity.prevPage.substring(5);
								} 
					    		int xmlIndex = -1;
					    		for (int i=0; i<MainActivity.XMLitems.size(); i++){
					    			if (MainActivity.XMLitems.get(i).get(0).getChannelTitle().equals(channelTitle)){
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
					    			Toast toast = Toast.makeText(context, "ERROR, not subscribed to feed", Toast.LENGTH_LONG);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
									String html = new BuildView(context, TAG).buildMainPage();
					    			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
					    			new ViewSwapper(context).display("main");
					    		}
							}
							else if (MainActivity.prevPage.equals("managefeeds")){
								String html = new BuildView(context,TAG).buildManageFeedsPage();
								MainActivity.wvManageFeeds.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
								new ViewSwapper(context).display("managefeeds");
							}
						}
					}
				});
			}
		}
		else if (task == 2){
			Log.d(TAG, "start load single feed page");
			int XMLpos = MainActivity.XMLitems.size()-1;
			String html = new BuildView(context, TAG).buildMainPageFeed(XMLpos);
			String channelTitle = MainActivity.XMLitems.get(XMLpos).get(0).getChannelTitle();
			MainActivity.wvUser.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
			new ViewSwapper(context).display("feed-" + channelTitle);
		}
		else if (task == 3){
			
			
		}
		
    }
  
    
}
