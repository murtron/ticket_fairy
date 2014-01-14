package com.patmoorehouse.ticket_fairy;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmControl {
	
	private Context context;
	private long repeatInterval;
	
	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	AlarmControl(Context context){
		this.context = context;
	}
	
	public void startAlarm(){
		Intent intentAlarm = new Intent(context, AlarmReceiver.class);
		
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        
        long startTime = calendar.getTimeInMillis() + 30000; // + 30000 offset so alarm isn't triggered as soon as its set.
        
        if(repeatInterval == 0){
        	repeatInterval = getNewInterval();
        }
		
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, repeatInterval, PendingIntent.getBroadcast(context, 239694542, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));
        //alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, PendingIntent.getBroadcast(context, 239694542, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
	}
	
	public void stopAlarm(){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intentAlarm = new Intent(context, AlarmReceiver.class);
		alarmManager.cancel(PendingIntent.getBroadcast(context, 239694542, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
	}
	
	public long getNewInterval(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String str = sharedPrefs.getString("updates_interval", "10800000");
		long l = Long.valueOf(str);
		return l;
	}

}
