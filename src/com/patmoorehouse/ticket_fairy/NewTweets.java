package com.patmoorehouse.ticket_fairy;


import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class NewTweets extends Activity {
	
	// Initializations
	private ListView tweetResultsList;
	private ArrayList<Tweet> tweetArrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_tweets);
		tweetResultsList = (ListView) findViewById(R.id.tweetResultsList);
		
		tweetArrayList = getIntent().getParcelableArrayListExtra("tweetArrayList");
		//tweetArrayList = getIntent().get
		
		AdapterTweet adbTweet = new AdapterTweet(this, 0, tweetArrayList);
		tweetResultsList.setAdapter(adbTweet);
		
		// Set onItemClickListener
		tweetResultsList.setOnItemClickListener(onTweetItemListener);
	}
	
	public OnItemClickListener onTweetItemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long rowIndex) {

			// Get tweet object from tweetArrayList corresponding to row index
			// clicked
			int index = (int) rowIndex;
			Tweet tweetToPass = tweetArrayList.get(index);

			// Create intent to initialize twitter app or on browser
			Intent intent = null;
			try {
				// get the Twitter app if possible
				getPackageManager().getPackageInfo("com.twitter.android", 0);
				intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://twitter.com/intent/tweet?text="
								+ tweetToPass.getUser() + " &url="));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			} catch (Exception e) {
				// no Twitter app, revert to browser
				intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://mobile.twitter.com/compose/tweet"));
			}
			startActivity(intent);
		}
	};
	
	// ACTION BAR CODE
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_tweets, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, PrefActivity.class));
			return true;
		case R.id.notifications:
			startActivity(new Intent(this, GigsManager.class));
			return true;
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
