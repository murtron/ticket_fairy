package com.patmoorehouse.ticket_fairy;

import android.os.Parcel;
import android.os.Parcelable;

public class Tweet implements Parcelable{
	private Long id;
	private String user;
	private String text;
	private String timestamp;
	 
	public Long getId() {
		return id;
	}
	public String getUser() {
		return user;
	}
	public String getText() {
		return text;
	}
	public String getTimestamp() {
		return timestamp;
	}
	 
	@Override
	public String toString() {
	    return this.user + ": " + this.text + "\n";
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeLong(id);
		arg0.writeString(user);
		arg0.writeString(text);
		arg0.writeString(timestamp);
	}
	
	public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
	    public Tweet createFromParcel(Parcel in) {
	        return new Tweet(in);
	    }
	
	    public Tweet[] newArray(int size) {
	        return new Tweet[size];
	    }
	};
	
	private Tweet(Parcel in) {
	    this.id = in.readLong();
	    this.user = in.readString();
	    this.text = in.readString();
	    this.timestamp = in.readString();
	}
	
	public Tweet(Long tweetId, String user, String text, String ts) {
		super();
		this.id = tweetId;
		this.user = user;
		this.text = text;
		this.timestamp = ts;
	}
	
	public Tweet(){
		super();
	}

}