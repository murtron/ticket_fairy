package com.patmoorehouse.ticket_fairy;

import java.util.ArrayList;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GigsManager extends ActionBarActivity {

	ArrayList<Gig> gigsBeingTracked = new ArrayList<Gig>();
	DBAdapter dbRead;
	AdapterNotifications adbNot;
	public ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gigs_manager);

		dbRead = new DBAdapter(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		try {
			dbRead.open();

			gigsBeingTracked.addAll(dbRead.getAllRecordsAsObjects());

			if (!gigsBeingTracked.isEmpty()) {
				list = (ListView) findViewById(android.R.id.list);

				// Set custom adapter for ListView
				list.setAdapter(new AdapterNotifications(this, 0,
						gigsBeingTracked, dbRead));

				list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long rowIndex) {

						// Get gig object from gigsBeingTracked
						int index = (int) rowIndex;
						Gig gigToPass = gigsBeingTracked.get(index);
						Intent intent;

						// Get orientation and screen size and decide which
						// activity to start
						Configuration config = getResources()
								.getConfiguration();
						int screenSize = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

						if (config.orientation == Configuration.ORIENTATION_LANDSCAPE
								&& (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
							intent = new Intent(GigsManager.this,
									MainActivity.class);
						} else {
							intent = new Intent(GigsManager.this,
									GigViewActivity.class);
						}

						// Pass gig and start activity
						intent.putExtra("gig", gigToPass);
						startActivity(intent);

					}
				});
			} else {
				setContentView(R.layout.activity_gigs_manager_empty);
			}

			dbRead.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
