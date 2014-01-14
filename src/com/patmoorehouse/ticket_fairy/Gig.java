package com.patmoorehouse.ticket_fairy;

import android.os.Parcel;
import android.os.Parcelable;

public class Gig implements Parcelable {

	private int gigId;
	private String artist;
	private String venue;
	private String dateTime;
	private String uri;
	private float popularity;
	private String lastTweetId;
	private String lastUpdated;
	private int isTracking;

	public Gig(int gigId, String artist, String venue, String dateTime,
			String uri, float popularity, String lastTweetId,
			String lastUpdated, int isTracking) {
		super();
		this.gigId = gigId;
		this.artist = artist;
		this.venue = venue;
		this.dateTime = dateTime;
		this.uri = uri;
		this.popularity = popularity;
		this.lastTweetId = lastTweetId;
		this.lastUpdated = lastUpdated;
		this.isTracking = isTracking;
	}

	@Override
	public String toString() {
		return artist + " " + venue + " " + dateTime;
	}

	public int getGigId() {
		return gigId;
	}

	public String getLastTweetId() {
		return lastTweetId;
	}

	public void setLastTweetId(String lastTweetId) {
		this.lastTweetId = lastTweetId;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getIsTracking() {
		return isTracking;
	}

	public void setIsTracking(int isTracking) {
		this.isTracking = isTracking;
	}

	public String getArtist() {
		return artist;
	}
	
	public String getUri() {
		return uri;
	}

	public String getVenue() {
		return venue;
	}

	public String getDateTime() {
		return dateTime;
	}

	public float getPopularity() {
		return popularity;
	}
	

	@Override
	public int describeContents() {
		return 0;
	}

	public Gig(Parcel in) {
		this.gigId = in.readInt();
		this.artist = in.readString();
		this.uri = in.readString();
		this.venue = in.readString();
		this.dateTime = in.readString();
		this.popularity = in.readFloat();
		this.lastTweetId = in.readString();
		this.lastUpdated = in.readString();
		this.isTracking = in.readInt();
		
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.gigId);
		out.writeString(this.artist);
		out.writeString(this.uri);
		out.writeString(this.venue);
		out.writeString(this.dateTime);
		out.writeFloat(this.popularity);
		out.writeString(this.lastTweetId);
		out.writeString(this.lastUpdated);
		out.writeInt(this.isTracking);
		
	}

	public static final Parcelable.Creator<Gig> CREATOR = new Parcelable.Creator<Gig>() {

		public Gig createFromParcel(Parcel in) {
			return new Gig(in);
		}

		public Gig[] newArray(int size) {
			return new Gig[size];
		}
	};
	

}
