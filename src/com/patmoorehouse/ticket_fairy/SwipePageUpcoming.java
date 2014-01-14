package com.patmoorehouse.ticket_fairy;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SwipePageUpcoming extends Fragment implements OnGetGigsCompleted {

	// The fragment argument representing the section number for this fragment.
	public static final String ARG_SECTION_NUMBER = "section_number";

	View rootView;
	int position;
	Activity fragmentActivity;
	ListView gigListView;
	ListAdapter adapter;
	private ArrayList<Gig> gigArrayList;
	private String query;
	GigViewFragment gigContentFrag;
	private ProgressBar spinner;
	OnGigSelectedListener mCallback;
	OnGetGigsCompleted getGigsListener;
	GetGigs getGigs;
	ImageButton btn_refresh;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnGigSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGigSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fragmentActivity = this.getActivity();
		getGigsListener = (OnGetGigsCompleted) fragmentActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// View object to handle the fragment layout page
		rootView = inflater.inflate(R.layout.upcoming_page, container, false);
		btn_refresh = (ImageButton) rootView.findViewById(R.id.imgbtn_refresh);
		btn_refresh.setOnClickListener(onRefreshClick);
		gigListView = (ListView) rootView.findViewById(R.id.gigListView);
		spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);

		// Execute call to SongKick API
		getGigs = (GetGigs) new GetGigs(this).execute(query);

		return rootView;
	}

	@Override
	public void onDestroy() {
		// Cancel AsyncTask if it's running
		if (getGigs != null) {
			getGigs.cancel(true);
		}
		super.onDestroy();
	}

	@Override
	public void onGetGigsCompleted(ArrayList<Gig> gigArrayList, String queryText) {

		// Initialize gigArrayList with returned gigs
		this.gigArrayList = gigArrayList;
		spinner.setVisibility(View.GONE);

		// Check orientation and capture fragment if exists
		getResources().getConfiguration();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			gigContentFrag = (GigViewFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.gigView_fragment);
		}

		if (!gigArrayList.isEmpty()) {
			btn_refresh.setVisibility(View.INVISIBLE);

			// Set adapter for ListView
			AdapterGig adbGig = new AdapterGig(getActivity(), 0, gigArrayList);
			gigListView.setAdapter(adbGig);

			// Set onItemClickListener
			gigListView.setOnItemClickListener(onGigItemListener);

			if (gigContentFrag != null) {
				// Check if a gig has been passed to MainActivity
				Gig gigObject = getActivity().getIntent().getParcelableExtra(
						"gig");
				if (gigObject != null) {
					gigContentFrag.updateContents(gigObject);
				} else {
					// Call a method in the GigViewFragment to update its
					// content
					gigContentFrag.updateContents(this.gigArrayList.get(0));
				}
			}

		} else {
			// Set up refresh button and error message
			TextView t = (TextView) rootView.findViewById(R.id.txvw_queryError);
			TextView txvw_refresh = (TextView) rootView
					.findViewById(R.id.txvw_refresh);
			btn_refresh.setVisibility(View.VISIBLE);
			t.setText(R.string.network_error_message);
			txvw_refresh.setText(R.string.tap_to_refresh);

			// Set default image for fragment if exists
			if (gigContentFrag != null) {
				gigContentFrag.setDefaultLayout();
			}
		}
	}

	public OnClickListener onRefreshClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getActivity().finish();
			startActivity(getActivity().getIntent());
		}
	};

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
				Intent intent = new Intent(getActivity(), GigViewActivity.class);
				intent.putExtra("gig", gigToPass);
				startActivity(intent);
			}
		}
	};

}
