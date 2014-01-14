package com.patmoorehouse.ticket_fairy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GigViewFragment extends Fragment implements OnGetTweetsCompleted,
		View.OnClickListener {

	private TextView gigDetailsTextView;
	private TextView locationDetailsTextView;
	private TextView timeDetailsTextView;
	private ListView tweetListView;
	private ArrayList<Tweet> tweetArrayList;
	private Gig gig;
	private TextView reSell;
	private Button btnNotify;
	private ImageView artistImageView;
	View rootView;
	View header;
	View myView;
	private ProgressBar spinner;
	GetImages getImages;
	GetTweets getTweets;
	private DBAdapter dbAdd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// View object to handle the fragment layout page
		rootView = inflater.inflate(R.layout.search_activity, container, false);
		header = inflater.inflate(R.layout.header, container, false);

		tweetListView = (ListView) rootView.findViewById(R.id.tweetListView);
		tweetListView.addHeaderView(header);

		spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);

		return rootView;
	}

	public void updateContents(Gig passedGig) {
		// Initialise gig object
		gig = passedGig;

		// Initialise views
		gigDetailsTextView = (TextView) header
				.findViewById(R.id.gigDetailsTextView);
		locationDetailsTextView = (TextView) header.findViewById(R.id.location);
		timeDetailsTextView = (TextView) header.findViewById(R.id.time);
		artistImageView = (ImageView) header.findViewById(R.id.tfLogo);
		tweetListView = (ListView) rootView.findViewById(R.id.tweetListView);
		reSell = (TextView) header.findViewById(R.id.reseller);

		String ven = passedGig.getVenue();

		// If the location is really long then trim it
		if (ven.length() > 20) {
			ven = ven.substring(0, 19) + "...";
		}

		String dateTime = passedGig.getDateTime();

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

		// Set gig text
		gigDetailsTextView.setText(passedGig.getArtist());
		locationDetailsTextView.setText(ven);
		timeDetailsTextView.setText(Monthchecker(month) + " " + day + hour);

		// Execute AsyncTask to search for artist images
		getImages = (GetImages) new GetImages(artistImageView)
				.execute(passedGig.getArtist());

		// Get query
		String query = "#ticketfairy " + passedGig.getArtist();
		String sinceId = "0";

		// Execute AsyncTask to search for tweets
		getTweets = (GetTweets) new GetTweets(this).execute(query, sinceId);

		btnNotify = (Button) header.findViewById(R.id.button1);
		btnNotify.setBackgroundResource(R.color.button_unselected);

		btnNotify.setOnClickListener(this);
		dbAdd = new DBAdapter(getActivity());
		try {
			dbAdd.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Cursor dataCheck = dbAdd.getRecord(passedGig.getGigId());
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
				getActivity().getPackageManager().getPackageInfo(
						"com.twitter.android", 0);
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

		// Set adapter for ListView
		tweetListView.setAdapter(new AdapterTweet(getActivity(), 0,
				tweetArrayList));

		// Set onItemClickListener only if tweets have been returned
		if (tweetArrayList.get(0).getId() != 0) {
			tweetListView.setOnItemClickListener(onTweetItemListener);
			reSell.setVisibility(View.GONE);
		} else {
			tweetListView.setOnItemClickListener(null);
			reSell.setVisibility(View.VISIBLE);
		}

		// Set onClickListener for SongKick link
		ImageView songKickIcon = (ImageView) getActivity().findViewById(
				R.id.imageView2);
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
			Toast.makeText(getActivity(),
					"No longer Tracking " + gig.getArtist(), Toast.LENGTH_SHORT)
					.show();

			// Checks if there are any active gigs left after deleting this gig
			// If none are found, stops alarm
			if (!dbAdd.checkForActiveGigs()) {
				AlarmControl ac = new AlarmControl(getActivity());
				ac.stopAlarm();
			} else {
			}
		} else {
			// Check for active gigs, if none found, start alarm and add new gig
			// If active gigs found, no need to start alarm(already active),
			// just add gig to DB
			if (!dbAdd.checkForActiveGigs()) {
				AlarmControl ac = new AlarmControl(getActivity());
				ac.startAlarm();
			} else {
			}

			Calendar cal = Calendar.getInstance();
			String time = String.valueOf(cal.getTimeInMillis());

			dbAdd.insertRecord(gig.getGigId(), gig.getArtist(),
					String.valueOf(tweetArrayList.get(0).getId()), time,
					gig.getVenue(), gig.getDateTime());

			btnNotify.setBackgroundResource(R.color.button_selected);
			btnNotify.setText(R.string.tracking);
			Toast.makeText(getActivity(), "Now tracking " + gig.getArtist(),
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

	@Override
	public void onDestroy() {
		// Cancel AsyncTasks if they're running
		if (getImages != null) {
			getImages.cancel(true);
		}
		if (getTweets != null) {
			getTweets.cancel(true);
		}
		super.onDestroy();
	}

	private String Monthchecker(final String month) {
		final String[] months = this.getResources().getStringArray(
				R.array.months_array);

		int m = Integer.parseInt(month);

		return months[m - 1];
	}

	// Alternative method to updateContents - to be called when artist search
	// returns no results
	public void setDefaultLayout() {
		
		spinner.setVisibility(View.GONE);
		rootView.setBackgroundResource(R.drawable.bokeh);

	}
}
