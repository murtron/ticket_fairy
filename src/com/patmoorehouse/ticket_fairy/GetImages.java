package com.patmoorehouse.ticket_fairy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class GetImages extends AsyncTask<String, String, String> {

	private String APIKey = "W5YXO9WLDIJ0WEXPX";
	private String baseURL = "http://developer.echonest.com/api/v4/artist/images?api_key=";
	private String imageURL;
	private ImageView artistImageView;
	private DownloadImageTask downloadImageTask;

	@Override
	protected void onCancelled(String s) {
		// Cancel image download if it's running
		if (downloadImageTask != null) {
			downloadImageTask.cancel(true);
		}

		super.onCancelled();
	}

	public GetImages(ImageView artistImageView) {
		this.artistImageView = artistImageView;
		artistImageView.setImageResource(R.drawable.bokeh);

	}

	@Override
	protected String doInBackground(String... queries) {

		String query = null;
		// Encode query
		try {
			query = URLEncoder.encode(queries[0], "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		;

		HttpClient httpClient = new DefaultHttpClient();

		// Construct URL here
		String fullURL = baseURL + APIKey + "&name=" + query
				+ "&format=json&results=1&start=0&license=unknown";
		HttpGet get = new HttpGet(fullURL);

		String result = null;
		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
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
			// Convert string to JSON object and parse
			JSONObject jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONObject("response").getJSONArray(
					"images");

			// Loop through JSONArray and put images into array
			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);

				// Search for URL
				if (oneObject.has("url")) {
					imageURL = oneObject.getString("url");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		catch (NullPointerException n) {
			n.printStackTrace();
		}

		if (imageURL != null) {
			// Use another AsyncTask to download the selected image and set it
			new DownloadImageTask(artistImageView).execute(imageURL);
		}

	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView artistImageView;

		public DownloadImageTask(ImageView artistImageView) {
			this.artistImageView = artistImageView;
		}

		protected Bitmap doInBackground(String... urls) {
			String urlDisplay = urls[0];
			Bitmap bImage = null;
			try {
				InputStream in = new java.net.URL(urlDisplay).openStream();

				// First decode with inJustDecodeBounds=true to check dimensions
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(in, null, options);

				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, 600, 600);

				InputStream newIn = new java.net.URL(urlDisplay).openStream();
				options.inJustDecodeBounds = false;
				bImage = BitmapFactory.decodeStream(newIn, null, options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bImage;
		}

		protected void onPostExecute(Bitmap result) {
			if(result != null) {
				artistImageView.setImageBitmap(result);
			}

		}

		private int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {

			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				final int halfHeight = height / 2;
				final int halfWidth = width / 2;

				// Calculate the largest inSampleSize value that is a power of 2
				// and keeps both
				// height and width larger than the requested height and width.
				while ((halfHeight / inSampleSize) > reqHeight
						&& (halfWidth / inSampleSize) > reqWidth) {
					inSampleSize *= 2;
				}
			}

			return inSampleSize;
		}
	}

}
