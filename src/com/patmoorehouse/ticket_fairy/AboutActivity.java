package com.patmoorehouse.ticket_fairy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AboutActivity extends Activity {

	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about);
	       
	        }		
	 
	 public void googleLink(View v) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/110574785397644350087/posts"));
			startActivity(browserIntent);
		}
	 
	 public void twitterLink(View v) {
		// Currently displaying blank twitter page. User Agent problem ?
		 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/TicketFairy_App"));
			startActivity(browserIntent);
		}
	 
	 public void fbLink(View v) {
			// Currently displaying blank twitter page. User Agent problem ?
			 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/pages/Ticket-Fairy/583157985072665"));
				startActivity(browserIntent);
			}
	 
	public void emailLink(View v) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "ticketfairyapp@gmail.com" });
		intent.putExtra(Intent.EXTRA_SUBJECT, "TicketFairy Feedback/Suggestions");
		
		try {
		    startActivity(Intent.createChooser(intent, getResources().getString(R.string.sending_email)));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

}