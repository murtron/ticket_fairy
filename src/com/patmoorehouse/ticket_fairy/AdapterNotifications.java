package com.patmoorehouse.ticket_fairy;

import java.util.Calendar;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterNotifications extends ArrayAdapter<Gig> {
	private List<Gig> lNot;
	DBAdapter dbRead;
	private LayoutInflater inflater = null;
	long currentTime = Calendar.getInstance().getTimeInMillis();

	// our custom array adapter to manipulate the gig object
	public AdapterNotifications(Activity activity, int textViewResourceId,
			List<Gig> _lNot, DBAdapter dbRead) {
		super(activity, textViewResourceId, _lNot);
		try {
			this.lNot = _lNot;
			this.dbRead = dbRead;
			// call the inflater service
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ViewHolder {
		// store the textviews here for later
		public TextView artist;
		public TextView toggleIcon;
		public TextView deleteIcon;
		public TextView lastUpdated;

	}

	public View getView(int pos, View convertView, ViewGroup parent) {

		View vi = convertView;
		final int position = pos;
		final ViewHolder holder;

		try {
			if (convertView == null) {
				vi = inflater.inflate(R.layout.manager_row, null);
				holder = new ViewHolder();

				// inflate the view
				holder.artist = (TextView) vi.findViewById(R.id.DBGigEntryName);
				holder.toggleIcon = (TextView) vi.findViewById(R.id.toggleIcon);
				holder.deleteIcon = (TextView) vi.findViewById(R.id.deleteIcon);
				holder.lastUpdated = (TextView) vi.findViewById(R.id.lastUpdated);

				vi.setTag(holder);

			} else {
				holder = (ViewHolder) vi.getTag();
			}
			// Set artist name
			String artist = lNot.get(position).getArtist();
			holder.artist.setText(artist);

			// Set Last Updated
			String lstUpdtd = lNot.get(position).getLastUpdated();
			holder.lastUpdated.setText(timeSince(lstUpdtd));
			
			// Set on/off text in toggle icon
			if (lNot.get(position).getIsTracking() == 1) {
				holder.toggleIcon.setText(R.string.on);
			} else {
				holder.toggleIcon.setText(R.string.off);
			}


			// Set onClickListeners for delete/toggle icons
			holder.deleteIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getContext());

					// set title
					alertDialogBuilder.setTitle(lNot.get(position).getArtist());
					
					String sure = getContext().getResources().getString(R.string.alert_dialog_sure);
					String cancel = getContext().getResources().getString(R.string.alert_dialog_cancel);
					String delete = getContext().getResources().getString(R.string.alert_dialog_delete);

					// set message
					alertDialogBuilder
							.setMessage(sure)
							.setCancelable(true)
							.setPositiveButton(delete,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											try {
												dbRead.open();
												dbRead.deleteRecord(lNot.get(
														position).getGigId());
												
												if(!dbRead.checkForActiveGigs()){
													AlarmControl ac = new AlarmControl(getContext());
													ac.stopAlarm();
												}else{}
												dbRead.close();

												lNot.remove(position);
												if(lNot.isEmpty()){
													Activity activity = (Activity) getContext();
													activity.setContentView(R.layout.activity_gigs_manager_empty);
												}

												notifyDataSetChanged();

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									})
							.setNegativeButton(cancel,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();

				}

			});

			holder.toggleIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int active = lNot.get(position).getIsTracking();
					try {
						if (active == 1) {
							dbRead.open();
							dbRead.updateIsTracking(lNot.get(position)
									.getGigId(), 0);
							lNot.get(position).setIsTracking(0);
							
							if(!dbRead.checkForActiveGigs()){
								AlarmControl ac = new AlarmControl(getContext());
								ac.stopAlarm();
							}else{}
							dbRead.close();
						} else {
							dbRead.open();
							if(!dbRead.checkForActiveGigs()){
								AlarmControl ac = new AlarmControl(getContext());
								ac.startAlarm();
							}else{}
							
							dbRead.updateIsTracking(lNot.get(position)
									.getGigId(), 1);
							lNot.get(position).setIsTracking(1);
							dbRead.close();
						}
						notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vi;
	}
	
	public String timeSince(String time){
		
		long t = currentTime - Long.parseLong(time);
		String updated = getContext().getResources().getString(R.string.last_updated);
		
		if(t>86400000){
			time = updated + " " + String.valueOf(t/86400000) + " " + getContext().getResources().getString(R.string.last_updated_day);
		}else if (t>3600000) {
			time = updated + " " + String.valueOf(t/3600000) + " " + getContext().getResources().getString(R.string.last_updated_hour);
		}else if (t>60000) {
			time = updated + " " + String.valueOf(t/60000) + " " + getContext().getResources().getString(R.string.last_updated_minute);
		}else{
			time = updated + " " + getContext().getResources().getString(R.string.last_updated_secs);
		}
		return time;
	}

}
