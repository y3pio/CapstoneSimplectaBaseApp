package com.example.simplectarssreader;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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
			
			hideAll();
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
		}
		else if (view.equals("descMain")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
		}
		else if (view.contains("descFeed-")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
		}
		else if (view.contains("feed-")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();
			
			MainActivity.wvUser.setVisibility(View.VISIBLE);
		}
		else if (view.equalsIgnoreCase("loginWV")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();
			
			MainActivity.wvMain.setVisibility(View.VISIBLE);
		}
		else if (view.equalsIgnoreCase("login")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();
			
			test();
		}
		else if (view.equalsIgnoreCase("managefeeds")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();

			MainActivity.manageFeedsDone.setVisibility(View.VISIBLE);
			MainActivity.manageFeedsAdd.setVisibility(View.VISIBLE);
			MainActivity.wvManageFeeds.setVisibility(View.VISIBLE);
		}
		else if (view.equalsIgnoreCase("load")){
			Log.d(TAG, "display - " + view);
			MainActivity.currPage = view;
			
			hideAll();
			
			MainActivity.loadText.setVisibility(View.VISIBLE);
		}
	}
	
	public void test(){
		hideAll();
		
		MainActivity.loginImage.setVisibility(View.VISIBLE);
		MainActivity.userNameEditText.setVisibility(View.VISIBLE);
		MainActivity.passwordEditText.setVisibility(View.VISIBLE);
		MainActivity.loginButton.setVisibility(View.VISIBLE);
		MainActivity.createAccButton.setVisibility(View.VISIBLE);		
		MainActivity.UsernameText.setVisibility(View.VISIBLE);	
		MainActivity.PasswordText.setVisibility(View.VISIBLE);	
	}
	
	public void hideAll(){
		MainActivity.loadText.setVisibility(View.GONE);
		
		MainActivity.wvUser.setVisibility(View.GONE);
				
		MainActivity.wvMain.setVisibility(View.GONE);
				
		MainActivity.wvManageFeeds.setVisibility(View.GONE);
		MainActivity.manageFeedsDone.setVisibility(View.GONE);
		MainActivity.manageFeedsAdd.setVisibility(View.GONE);
		
		MainActivity.loginImage.setVisibility(View.GONE);
		MainActivity.userNameEditText.setVisibility(View.GONE);
		MainActivity.passwordEditText.setVisibility(View.GONE);
		MainActivity.loginButton.setVisibility(View.GONE);
		MainActivity.createAccButton.setVisibility(View.GONE);	
		MainActivity.UsernameText.setVisibility(View.GONE);	
		MainActivity.PasswordText.setVisibility(View.GONE);	
	}
}
