package com.patmoorehouse.ticket_fairy;

import java.sql.SQLException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		DBAdapter dbRead = new DBAdapter(arg0); 
        
		// If DB has active gigs, alarm needs to be rescheduled.
		try {
			dbRead.open();
			if(dbRead.checkForActiveGigs()){
				AlarmControl ac = new AlarmControl(arg0);
				ac.startAlarm();
			}
			dbRead.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
	}
}
