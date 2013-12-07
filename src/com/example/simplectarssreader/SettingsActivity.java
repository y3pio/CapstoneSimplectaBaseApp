package com.example.simplectarssreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class SettingsActivity extends Activity {

	static Context c;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, true);
		c = this;	
	}
	
	public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
					
			// set texts correctly
	        onSharedPreferenceChanged(null, "");
	        
	        final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
	        
		}//end of oncreate
		
		@Override
	    public void onResume() {
	        super.onResume();
	        // Set up a listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        // Set up a listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    }

	    //@Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {  

	    }

	}//end of preffragment
}//end of settingsactivity