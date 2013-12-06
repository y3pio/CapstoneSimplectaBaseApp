package com.example.simplectarssreader;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class ViewSwapper {
	final static String TAG = "ViewSwapper";
	
	Context context;
	
	protected ViewSwapper(Context c){
		context = c;
	}
	
	public void display(String view){		
		if (view.equalsIgnoreCase("main")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
			
			MainActivity.wvMain.setVisibility(View.GONE);
			
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.equals("descMain")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
			
			MainActivity.wvMain.setVisibility(View.GONE);
			
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.contains("descFeed-")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
			
			MainActivity.wvMain.setVisibility(View.GONE);
			
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.contains("feed-")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
			
			MainActivity.wvMain.setVisibility(View.GONE);
			
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.equalsIgnoreCase("login")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.GONE);
			
			MainActivity.wvMain.setVisibility(View.VISIBLE);
					
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
		else if (view.equalsIgnoreCase("managefeeds")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.GONE);
			
			MainActivity.wvUser.setVisibility(View.GONE);
					
			MainActivity.wvMain.setVisibility(View.GONE);

			MainActivity.manageFeedsDone.setVisibility(View.VISIBLE);
			MainActivity.manageFeedsAdd.setVisibility(View.VISIBLE);
			MainActivity.wvManageFeeds.setVisibility(View.VISIBLE);
				
		}
		else if (view.equalsIgnoreCase("load")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			MainActivity.loadText.setVisibility(View.VISIBLE);
			
			MainActivity.wvUser.setVisibility(View.GONE);
					
			MainActivity.wvMain.setVisibility(View.GONE);
					
			MainActivity.wvManageFeeds.setVisibility(View.GONE);
			MainActivity.manageFeedsDone.setVisibility(View.GONE);
			MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		}
	}
	

}
