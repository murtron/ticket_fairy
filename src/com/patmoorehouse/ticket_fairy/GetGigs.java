package com.patmoorehouse.ticket_fairy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class GetGigs extends AsyncTask<String, Void, String> {

	private String metroAreaURL = "http://api.songkick.com/api/3.0/metro_areas/29314/calendar.json?apikey=wYnKWcjvdpwImb7q";
	private String artistSearchURL = "http://api.songkick.com/api/3.0/events.json?apikey=wYnKWcjvdpwImb7q&artist_name=";
	AdapterGig adbGig;
	private ArrayList<Gig> gigArrayList = new ArrayList<Gig>();
	private int gigId;
	private String artist;
	private String venue;
	private String uri;
	private String dateTime;
	private float popularity;
	private String queryText = null;
	
	private OnGetGigsCompleted listener;

	public GetGigs(OnGetGigsCompleted listener){
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String... query) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = null;
		
		// Check if it is an artist search or not
		if(query[0] == null) {
			// Use default metro area URL
			get = new HttpGet(metroAreaURL);
		} else {
			// Store query to send back to main 
			queryText = query[0];
			
			// Construct full artist request URL
			get = new HttpGet(artistSearchURL + query[0] + "&location=sk:29314");
		}

		String result = null;

		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();

			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null && !isCancelled()) {
				sb.append(line + "\n");
			}
			result = sb.toString();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	protected void onPostExecute(String result) {

		try {
			// This line throws JSONException if result is null
			JSONObject jObject = new JSONObject(result);

			JSONArray jArray = jObject.getJSONObject("resultsPage")
					.getJSONObject("results").getJSONArray("event");

			// Loop through JSONArray and put gigs into array
			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);

				// Search for id
				if (oneObject.has("id")) {
					gigId = Integer.parseInt(oneObject.getString("id"));
				}
				// Search for time and date of gig
				if (oneObject.has("start")) {
					
					JSONObject startObject = oneObject.getJSONObject("start");
					String time = startObject.getString("time");
					String date = startObject.getString("date");
					
					// API can return null times
					if(time=="null") {
						dateTime = date;
					} else {
						dateTime =  time + " " + date;
					}
			
				}
				// Search for artist name
				if (oneObject.has("performance")) {
					JSONArray performanceArray = oneObject.getJSONArray("performance");
					
					// Array may be empty
					if (performanceArray.length()!=0)
					artist = performanceArray.getJSONObject(0).getString("displayName");
				} 
				if (oneObject.has("venue")) {
					venue = oneObject.getJSONObject("venue").getString(
							"displayName");
				}
				if (oneObject.has("popularity")) {
					popularity = Float.parseFloat(oneObject
							.getString("popularity"));
				}
				
				if (oneObject.has("uri")) {
					uri = (oneObject
							.getString("uri"));
				}

				// Add new gig object to array
				gigArrayList.add(new Gig(gigId, artist, venue, dateTime, uri,
						popularity, null, null, 0));
			}	
		 	

		} catch (JSONException e) {
			// Thrown when no results returned from API
			e.printStackTrace();
		
		} catch (NullPointerException n){
			// Thrown when no internet connection
			n.printStackTrace();
		}	 
		
		listener.onGetGigsCompleted(gigArrayList, queryText);
	}

}
