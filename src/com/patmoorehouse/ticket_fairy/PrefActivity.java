package com.patmoorehouse.ticket_fairy;

import java.sql.SQLException;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity {
	

	@Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.preferences);
        
        final Context context = getApplicationContext();
        
        final ListPreference listPref = (ListPreference) getPreferenceManager().findPreference("updates_interval");
        listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
		        
				// If active gigs are found, alarm needs to be rescheduled.
				DBAdapter db = new DBAdapter(getApplicationContext());
				try {
					db.open();
					if(db.checkForActiveGigs()){
			        	AlarmControl ac = new AlarmControl(context);
						ac.setRepeatInterval(Long.valueOf(newValue.toString()));
			        	ac.startAlarm();
			        }else{
			        	// if no active gigs, don't do anything.
			        }
					db.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
			}
		});
    }
}