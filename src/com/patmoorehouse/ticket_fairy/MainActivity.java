package com.patmoorehouse.ticket_fairy;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity implements
		OnGigSelectedListener, OnGetGigsCompleted {

	// Adapter to provide swipe pages
	SwipePageAdapter swipePageAdapter;
	ViewPager viewPager;
	private ArrayList<Gig> gigArrayList;
	ListView gigListView;
	GigViewFragment gigContentFrag;
	OnGigSelectedListener mCallback;
	GetGigs getGigs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		mCallback = this;
		viewPager = (ViewPager) findViewById(R.id.pager);
		PagerTitleStrip pagetitlestrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		gigListView = (ListView) findViewById(R.id.gigListView);

		// If it's an artist search do that
		// Else do swipe page stuff
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			// Encode query
			try {
				query = URLEncoder.encode(query, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			;

			// Execute call to SongKick API
			getGigs = (GetGigs) new GetGigs(MainActivity.this).execute(query);

			pagetitlestrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
			View triangle = (View) findViewById(R.id.view1);

			viewPager.setVisibility(View.GONE);
			pagetitlestrip.setVisibility(View.GONE);
			triangle.setVisibility(View.GONE);
			gigListView.setPadding(0, 20, 0, 0);

		} else {
			viewPager.setVisibility(View.VISIBLE);
			pagetitlestrip.setVisibility(View.VISIBLE);

			// Instantiate adapter to provide swipe pages
			swipePageAdapter = new SwipePageAdapter(
					getSupportFragmentManager(), this);

			// Link the swipe page to the layout file
			// then attach the swipe page to the swipe pages adapter
			viewPager.setAdapter(swipePageAdapter);
		}
	}

	// This is only invoked if in a tablet layout
	public void onGigSelected(Gig passedGig) {
		// The user selected the gig from the list in swipepagepopular/upcoming

		// Capture the gig fragment from the activity layout
		GigViewFragment articleFrag = (GigViewFragment) getSupportFragmentManager()
				.findFragmentById(R.id.gigView_fragment);

		// Call a method in the GigViewFragment to update its content
		articleFrag.updateContents(passedGig);
	}

	@Override
	public void onGetGigsCompleted(ArrayList<Gig> gigArrayList, String queryText) {

		// Check orientation and capture fragment if exists
		getResources().getConfiguration();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			gigContentFrag = (GigViewFragment) this
					.getSupportFragmentManager().findFragmentById(
							R.id.gigView_fragment);
		}
		
		if (!gigArrayList.isEmpty()) {

			// Initialize gigArrayList with returned gigs
			this.gigArrayList = gigArrayList;

			// Set adapter for ListView
			AdapterGig adbGig = new AdapterGig(this, 0, gigArrayList);
			gigListView.setAdapter(adbGig);

			// Set onItemClickListener
			gigListView.setOnItemClickListener(onGigItemListener);

			if (gigContentFrag != null) {
				// Call a method in the GigViewFragment to update its
				// content
				gigContentFrag.updateContents(this.gigArrayList.get(0));
			}
			
		} else {

			// Get rid of list view padding at top of main
			gigListView.setPadding(0, 0, 0, 0);
			
			// For tab layout call method in fragment to set a default layout
			if (gigContentFrag != null) {
				gigContentFrag.setDefaultLayout();
			}

			// Display error message
			TextView t = (TextView) findViewById(R.id.txvw_queryError);
			t.setText(R.string.no_results);	
			
			// Decode query and append to error message
			try {
				queryText = URLDecoder.decode(queryText, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			t.append(" '"+queryText+"'");
		}
	}

	public OnItemClickListener onGigItemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long rowIndex) {

			// Get gig object from gigArrayList corresponding to row index
			// clicked
			int index = (int) rowIndex;
			Gig gigToPass = gigArrayList.get(index);

			if (gigContentFrag != null) {
				mCallback.onGigSelected(gigToPass);
			} else {
				// Create intent to start search activity and pass gig object
				Intent intent = new Intent(getBaseContext(),
						GigViewActivity.class);
				intent.putExtra("gig", gigToPass);
				startActivity(intent);
			}
		}
	};

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
				.getSearchableInfo(getComponentName()));

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

	@Override
	protected void onDestroy() {

		// Cancel AsyncTask if it's running
		if (getGigs != null) {
			getGigs.cancel(true);
		}
		super.onDestroy();
	}
}
