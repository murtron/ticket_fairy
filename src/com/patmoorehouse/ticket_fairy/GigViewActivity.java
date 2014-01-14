package com.patmoorehouse.ticket_fairy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GigViewActivity extends ActionBarActivity implements
		OnGetTweetsCompleted, View.OnClickListener {

	private TextView gigDetailsTextView;
	private TextView locationDetailsTextView;
	private TextView timeDetailsTextView;
	private ListView tweetListView;
	private TextView reSell;
	private ArrayList<Tweet> tweetArrayList;
	private Gig gig;
	private Button btnNotify;
	private ImageView artistImageView;
	private ProgressBar spinner;
	View header;
	GetImages getImages;
	GetTweets getTweets;
	private DBAdapter dbAdd;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		// Initialise views
		header = (View) getLayoutInflater().inflate(R.layout.header, null);
		tweetListView = (ListView) findViewById(R.id.tweetListView);
		tweetListView.addHeaderView(header);
		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		gigDetailsTextView = (TextView) header
				.findViewById(R.id.gigDetailsTextView);
		locationDetailsTextView = (TextView) header.findViewById(R.id.location);
		reSell = (TextView) header.findViewById(R.id.reseller);
		timeDetailsTextView = (TextView) header.findViewById(R.id.time);
		artistImageView = (ImageView) header.findViewById(R.id.tfLogo);

		// Get passed gig from intent
		gig = getIntent().getParcelableExtra("gig");

		String ven = gig.getVenue();

		// If the location is really long then trim it
		if (ven.length() > 20) {
			ven = ven.substring(0, 19) + "...";
		}

		String dateTime = gig.getDateTime();

		String hour;
		String day;
		String month;

		// Some gigs don't have time values, so check if they do
		if (dateTime.contains(":")) {
			hour = dateTime.substring(0, 2);
			month = dateTime.substring(14, 16);
			day = dateTime.substring(17, 19);

			if (Integer.parseInt(hour) >= 12) {
				hour = ", " + Integer.toString((Integer.parseInt(hour) - 12))
						+ "pm";
			}

			else {
				hour = ", " + hour + "am";
			}
		}

		else {
			month = dateTime.substring(5, 7);
			day = dateTime.substring(8, 10);
			hour = "";
		}

		if (day.startsWith("0")) {
			day = day.substring(1, 2);
		}

		setTitle(gig.getArtist());

		// Set gig text
		gigDetailsTextView.setText(gig.getArtist());
		locationDetailsTextView.setText(ven);
		timeDetailsTextView.setText(Monthchecker(month) + " " + day + hour);

		// Execute AsyncTask to search for artist images
		getImages = (GetImages) new GetImages(artistImageView).execute(gig
				.getArtist());

		// Get query
		String query = "#ticketfairy " + gig.getArtist();
		String sinceId = "0";

		// Execute AsyncTask to search for tweets
		getTweets = (GetTweets) new GetTweets(this).execute(query, sinceId);

		btnNotify = (Button) header.findViewById(R.id.button1);
		btnNotify.setOnClickListener(this);

		dbAdd = new DBAdapter(GigViewActivity.this);
		try {
			dbAdd.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			Cursor dataCheck = dbAdd.getRecord(gig.getGigId());
			if ((dataCheck != null) && (dataCheck.getCount() > 0)) {
				btnNotify.setBackgroundResource(R.color.button_selected);
				btnNotify.setText(R.string.tracking);
				dataCheck.close();
			} else {
				btnNotify.setText(R.string.notify_me);
				dataCheck.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbAdd.close();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
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

	// Do UI stuff here when tweets have been returned
	@Override
	public void onGetTweetsCompleted(ArrayList<Tweet> tweetArrayList) {
		spinner.setVisibility(View.GONE);

		// Initialize tweetArrayList with returned tweets
		this.tweetArrayList = tweetArrayList;

		// Set custom adapter for ListView
		AdapterTweet adbTweet = new AdapterTweet(this, 0, tweetArrayList);
		tweetListView.setAdapter(adbTweet);

		// Set onItemClickListener only if tweets have been returned
		if (tweetArrayList.get(0).getId() != 0) {
			tweetListView.setOnItemClickListener(onTweetItemListener);
			reSell.setVisibility(View.GONE);

		}


		// Set onClickListener for SongKick link
		ImageView songKickIcon = (ImageView) findViewById(R.id.imageView2);
		songKickIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(gig.getUri()));
				startActivity(browserIntent);
			}
		});
		
		reSell.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(gig.getUri()));
				startActivity(browserIntent);
			}
		});

	}

	// Add gig to database
	public void addGig() throws SQLException {

		// DBAdapter dbAdd = new DBAdapter(GigViewActivity.this);
		dbAdd.open();
		if (dbAdd.checkRecExists(gig.getGigId())) {
			dbAdd.deleteRecord(gig.getGigId());

			btnNotify.setBackgroundResource(R.color.button_unselected);
			btnNotify.setText(R.string.notify_me);
			Toast.makeText(this, "No longer Tracking " + gig.getArtist(),
					Toast.LENGTH_SHORT).show();
			
			// Checks if there are any active gigs left after deleting this gig
			// If none are found, stops alarm
			if(!dbAdd.checkForActiveGigs()){
				AlarmControl ac = new AlarmControl(this);
				ac.stopAlarm();
			}else{}
		} else {
			// Chek for active gigs, if none found, start alarm and add new gig
			// If active gigs found, no need to start alarm(already active), just add gig to DB
			if(!dbAdd.checkForActiveGigs()){
				AlarmControl ac = new AlarmControl(this);
				ac.startAlarm();
			}else{}
			
			Calendar cal = Calendar.getInstance();
			String time = String.valueOf(cal.getTimeInMillis());
			
			dbAdd.insertRecord(gig.getGigId(), gig.getArtist(),
					String.valueOf(tweetArrayList.get(0).getId()),
					time, gig.getVenue(),
					gig.getDateTime());

			btnNotify.setBackgroundResource(R.color.button_selected);
			btnNotify.setText(R.string.tracking);
			Toast.makeText(this, "Now tracking " + gig.getArtist(),
					Toast.LENGTH_SHORT).show();
		}
		dbAdd.close();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button1:
			try {
				addGig();

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

	}

	private String Monthchecker(final String month) {
		final String[] months = this.getResources().getStringArray(
				R.array.months_array);

		int m = Integer.parseInt(month);

		return months[m - 1];
	}

	@Override
	protected void onDestroy() {
		// Cancel AsyncTasks if they're running
		if (getImages != null) {
			getImages.cancel(true);
		}
		if (getTweets != null) {
			getTweets.cancel(true);
		}
		super.onDestroy();
	}

	// ACTION BAR CODE
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(new ComponentName(getApplicationContext(),
						MainActivity.class)));

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
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
