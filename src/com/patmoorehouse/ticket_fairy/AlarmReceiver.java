package com.patmoorehouse.ticket_fairy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver implements OnGetTweetsCompleted
{
	// Initializing variables
	private ArrayList<Tweet> tweetArrayList = new ArrayList<Tweet>();
	String str = new String();
	Context context;
	Cursor c;
	DBAdapter dbRead;
	String currentTime = String.valueOf(Calendar.getInstance().getTimeInMillis());
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		try{
			getGigs(context);
		}catch(SQLException e){
			e.printStackTrace();
		}
			
	}

	@Override
	public void onGetTweetsCompleted(ArrayList<Tweet> tweetArrayList) {
		// Initialize tweetArrayList with returned tweets
			if (tweetArrayList.size() > 0){
				if(!tweetArrayList.get(0).getUser().equals("Sorry")){
					this.tweetArrayList.addAll(tweetArrayList);
					str += c.getString(1) + ", ";
					dbRead.updateLastTweetId(c.getLong(0), String.valueOf(tweetArrayList.get(0).getId()));
				}else{}
			}else{
				// No Action Required
			}
			
			dbRead.updateLastUpdated(c.getLong(0),
					currentTime);
			
			c.moveToNext();
			if(!c.isAfterLast()){
				getGigTweets(c.getString(1), c.getString(2));
			}else{
				if(str.length()>0) str = str.substring(0, str.length() - 2);
				c.close();
				dbRead.close();
				if(this.tweetArrayList.size() > 0){
					createNotification();
				}
			}
	}
	
	public void getGigs(Context context) throws SQLException{
		dbRead = new DBAdapter(context);
		dbRead.open();
		c = dbRead.getAllActiveRecords();
		if(c.getCount() > 0){
			c.moveToFirst();
			getGigTweets(c.getString(1), c.getString(2));
		}else{
			c.close();
			dbRead.close();
			AlarmControl ac = new AlarmControl(context);
			ac.stopAlarm();
		}
	}
	
	public void getGigTweets(String artist, String tweetId){
		// Get query
		String query = "#ticketfairy " + artist;
		
		// Execute AsyncTask to search for tweets
		GetTweets gt = new GetTweets(this);
		gt.execute(query, tweetId);
	}
	
	public void createNotification(){
		
		Intent intent = new Intent(context, NewTweets.class);
		intent.putParcelableArrayListExtra("tweetArrayList", tweetArrayList);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				694542239, intent,
		        PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager nm = (NotificationManager) context
		        .getSystemService(Context.NOTIFICATION_SERVICE);

		Resources res = context.getResources();
		//NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.snd_3);
		
		builder.setContentIntent(contentIntent)
		            .setSmallIcon(R.drawable.ic_launcher)
		            .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
		            .setTicker("TicketFairy Found Tweets!"/*res.getString(R.string.your_ticker)*/)
		            .setWhen(System.currentTimeMillis())
		            .setAutoCancel(true)
		            .setContentTitle("Ticket Fairy found " + tweetArrayList.size() + " tweets!"/*res.getString(R.string.your_notif_title)*/)
		            .setContentText("Tweets found for: " + str/*res.getString(R.string.your_notif_text)*/)
		            .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
		            .setSound(sound);
		Notification n = builder.build();

		nm.notify(694542239, n);
	}
      
}
