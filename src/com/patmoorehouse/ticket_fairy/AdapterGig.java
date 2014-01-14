package com.patmoorehouse.ticket_fairy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class AdapterGig extends ArrayAdapter<Gig> {
    private ArrayList<Gig> lGig;
    private final Activity mActivity;
    private LayoutInflater inflater = null;
    // our custom array adapter to manipulate the gig object
    public AdapterGig (Activity activity, int textViewResourceId,ArrayList<Gig> _lGig) {
        super(activity, textViewResourceId, _lGig);
        mActivity = activity;
        try {
            this.lGig = _lGig;
            // call the inflater service
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }
    
    public class ViewHolder {
    	// store the textviews here for later
        public TextView gigBandName;
        public TextView gigVenue; 
        public TextView gigTime; 
        public TextView gigMonth; 
        public TextView gigDay; 

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.gig_row, null);
                holder = new ViewHolder();
                // inflate the view
                holder.gigBandName = (TextView) vi.findViewById(R.id.gigBandName);
                holder.gigVenue = (TextView) vi.findViewById(R.id.gigVenue);
                holder.gigTime = (TextView) vi.findViewById(R.id.gigTime);
                holder.gigDay = (TextView) vi.findViewById(R.id.gigDay);
                holder.gigMonth = (TextView) vi.findViewById(R.id.gigMonth);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            String dateTime = lGig.get(position).getDateTime();
            
           // Log.w("datetime1",dateTime);
            
            String hour = "";
            String day;
            String month;
            
            
            // Some gigs don't have time values, so check if they do
	        if (dateTime.contains(":")){
	             hour = dateTime.substring(0,2);
	             month = dateTime.substring(14,16);
	             day = dateTime.substring(17,19);
	             
	             if (Integer.parseInt(hour) >= 12)
	             {
	            	 hour = Integer.toString((Integer.parseInt(hour) - 12)) + "pm";
	             }
	             
	             else 
	             {
	            	 hour = hour + "am";
	             }
	        }
	        
	        else {
	        	month = dateTime.substring(5,7);
	            day = dateTime.substring(8,10);
	        }
	        
	        if (day.startsWith("0")) 
	        {
	        	day = day.substring(1,2);
	        }
            
	        String ven = lGig.get(position).getVenue();
	        
	        
	        // If the location is really long then trim it
	        if (ven.length() > 20)
	        {
	        	ven = ven.substring(0,19) + "...";
	        }
	        
	        if (ven.toLowerCase().contains("unknown venue"))
	        {
	        	ven = "";
	        	holder.gigTime.setPadding(0, 0, 0, 0);
	        	
	        }
	        
	        else
	        {
	        	holder.gigTime.setPadding(10, 0, 0, 0);
	        }
           		        
            //Log.w("hour",hour);
            //Log.w("month",month);
            //Log.w("day",day);

            holder.gigTime.setText(hour);
            holder.gigDay.setText(day);
            holder.gigBandName.setText(lGig.get(position).getArtist());
            holder.gigVenue.setText(ven);
            holder.gigMonth.setText(Monthchecker(month).toUpperCase().substring(0,3));


        } catch (Exception e) {


        }
        return vi;
    }
    
	 private String Monthchecker(final String month){
		    final String[] months = mActivity.getResources().getStringArray(R.array.months_array);
		    
		    int m = Integer.parseInt(month);
		    	
		    return months[m-1];    
	}

}
