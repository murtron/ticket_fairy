package com.patmoorehouse.ticket_fairy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class AdapterTweet extends ArrayAdapter<Tweet> {
    private ArrayList<Tweet> lTweet;
    private LayoutInflater inflater = null;

    // our custom array adapter to manipulate the Tweet object
    public AdapterTweet (Activity activity, int textViewResourceId,ArrayList<Tweet> _lTweet) {
        super(activity, textViewResourceId, _lTweet);
        try {
            this.lTweet = _lTweet;
            // call the inflater service
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }
    
    public class ViewHolder {
    	// store the textviews here for later
        public TextView tweetName;
        public TextView tweetText;
        public TextView tweetTime;


    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.tweet_row, null);
                holder = new ViewHolder();
                // inflate the view
                holder.tweetName = (TextView) vi.findViewById(R.id.tweetName);
                holder.tweetText = (TextView) vi.findViewById(R.id.tweetText);
                holder.tweetTime = (TextView) vi.findViewById(R.id.tweetTime);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
                        
            
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date gmt = formatter.parse(lTweet.get(position).getTimestamp());
            long millisecondsSinceEpoch = gmt.getTime();
            
            CharSequence stamp = DateUtils .getRelativeTimeSpanString (millisecondsSinceEpoch, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            
            vi.setVisibility(convertView.VISIBLE);
            holder.tweetName.setText(lTweet.get(position).getUser());
            holder.tweetText.setText(lTweet.get(position).getText());
            holder.tweetTime.setText(stamp); // The middot char didnt work, space already between views, so only adding the t-stamp.

  


        } catch (Exception e) {


        }
        return vi;
    }
    


}
