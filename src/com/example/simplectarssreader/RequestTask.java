package com.example.simplectarssreader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
	
	
	protected RequestTask(Context c, String calledFrom){
		context = c;
		fromActivity = calledFrom;
		Log.d(TAG, "---------RequestTask---------");
	}
	

	@Override
    protected String doInBackground(String... uri) {
		Log.d(TAG, "AsyncTask, url = " + uri[0]);
		MainActivity.URLtoLoad = uri[0];
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
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
        Log.d(TAG, "onPostExecute");
       	new HtmlFile(TAG).makeHTMLFile(htmlString);
        MainActivity.html = htmlString; //we should probably use an arraylist instead
    }
    

    
    
    
}
