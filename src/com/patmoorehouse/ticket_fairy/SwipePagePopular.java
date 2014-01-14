package com.patmoorehouse.ticket_fairy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SwipePagePopular extends Fragment implements OnGetGigsCompleted {
	View rootView;
	int position;
	Activity fragmentActivity;

	ListView gigListView;
	ListAdapter adapter;
	private ArrayList<Gig> gigArrayList;
	private String query;
	GigViewFragment gigContentFrag;
	private ProgressBar spinner;

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

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
					+ " must implement OnPopularGigSelectedListener");
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

		// Initialize gigArrayList with sorted returned gigs
		this.gigArrayList = sortGigArrayListByPopularity(gigArrayList);
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

		} else {
			// Set up refresh button and error message
			TextView t = (TextView) rootView.findViewById(R.id.txvw_queryError);
			TextView txvw_refresh = (TextView) rootView
					.findViewById(R.id.txvw_refresh);
			btn_refresh.setVisibility(View.VISIBLE);
			t.setText(R.string.network_error_message);
			txvw_refresh.setText(R.string.tap_to_refresh);

			// Set default image for fragment if exists
			if(gigContentFrag!=null) {
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

	public ArrayList<Gig> sortGigArrayListByPopularity(
			ArrayList<Gig> gigArrayList) {
		Collections.sort(gigArrayList, new Comparator<Gig>() {

			public int compare(Gig gig1, Gig gig2) {
				return Float.compare(gig2.getPopularity(), gig1.getPopularity());
			}
		});
		return gigArrayList;
	}

}
