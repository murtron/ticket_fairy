package com.patmoorehouse.ticket_fairy;

import java.util.ArrayList;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;

class GetTweets extends AsyncTask<String, Void, QueryResult> {
	
	private static final String consumerKey = "T0DLDgdlWIQnuWpagXnjIw";
	private static final String consumerSecret = "a2eDRBkkcpItpJrsnUtX27WGEqjIGVpq1akoat6Ahc";
	private ConfigurationBuilder builder;
	private ArrayList<Tweet> tweetArrayList = new ArrayList<Tweet>();
	private OnGetTweetsCompleted listener;

	public GetTweets(OnGetTweetsCompleted listener){
		this.listener = listener;
	
	}
	
	@Override
	protected QueryResult doInBackground(String... searchQuery) {

		builder = new ConfigurationBuilder();
		builder.setUseSSL(true);
		builder.setApplicationOnlyAuthEnabled(true);

		// The factory instance is re-useable and thread safe.
		Twitter twitter = new TwitterFactory(builder.build()).getInstance();

		try {
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			OAuth2Token token = twitter.getOAuth2Token();

			// Make sure token == bearer
			if (token.getTokenType().equals("bearer")) {
			}

			Query query = new Query(searchQuery[0] + " +exclude:retweets");			
			query.setSinceId(Long.parseLong(searchQuery[1]));
			
			QueryResult result = twitter.search(query);
			return result;

		} catch (TwitterException e2) {
			e2.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(QueryResult result) {

		if (result != null) {
			// For each status add user and text to array
			for (twitter4j.Status status : result.getTweets()) {
				tweetArrayList.add(new Tweet(status.getId(), "@"
						+ status.getUser().getScreenName(), status
						.getText(), status.getCreatedAt().toString()));
			}

			// Handles case when no tweets are returned
			if (tweetArrayList.isEmpty()) {
				tweetArrayList.add(new Tweet(0l, "Sorry",
						"No tickets found, try again later!", ""));
			}
			
			// Callback method
			listener.onGetTweetsCompleted(tweetArrayList);
		
		} else {
			tweetArrayList.add(new Tweet(0l, "Sorry",
					"No internet connection!", ""));
			
			// Callback method
			listener.onGetTweetsCompleted(tweetArrayList);
		}
	}

}