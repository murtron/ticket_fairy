package com.patmoorehouse.ticket_fairy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DBAdapter class contains the table structure and methods for CRUD operation
 * on the app's SQLite DB Use Edit->Find/Replace with text "currentapi" to see
 * API version check method contained in this class
 */
public class DBAdapter extends Activity {

	// Database: Table Fields
	public static final String KEY_GIGID = "gig_id";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_LAST_TWEET_ID = "last_tweet_id";
	public static final String KEY_LAST_UPDATED = "last_updated"; // Should be
																	// date
																	// field ***
	public static final String KEY_ISTRACKING = "is_tracking";
	public static final String KEY_VENUE = "venue";
	public static final String KEY_DATE_TIME = "date_time";

	// Class tag for reference in Log
	private static final String TAG = "DBAdapter";

	// Database: Top level constants
	public static final String DATABASE_NAME = "ticketfairy_local";
	public static final String DATABASE_TABLE = "notifications";
	public static final int DATABASE_VERSION = 2;

	// CREATE table statement
	public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ DATABASE_TABLE + " (" + KEY_GIGID + " INTEGER PRIMARY KEY, "
			+ KEY_ARTIST + " VARCHAR NOT NULL, " + KEY_LAST_TWEET_ID
			+ " STRING NOT NULL," + KEY_LAST_UPDATED + " VARCHAR NOT NULL,"
			+ KEY_ISTRACKING + " INT NOT NULL," + KEY_VENUE + " STRING, "
			+ KEY_DATE_TIME + " STRING" + ");";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			// try {
			db.execSQL(DATABASE_CREATE);
			// }
			// catch (SQLException e){
			// e.printStackTrace();
			// }
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + " , which will destroy old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public long insertRecord(int gigId, String artist, String last_tweet_id,
			String last_updated, String venue, String date_time) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_GIGID, gigId);
		initialValues.put(KEY_ARTIST, artist);
		initialValues.put(KEY_LAST_TWEET_ID, last_tweet_id);
		initialValues.put(KEY_LAST_UPDATED, last_updated);
		initialValues.put(KEY_ISTRACKING, 1); // tracking by default
		initialValues.put(KEY_VENUE, venue);
		initialValues.put(KEY_DATE_TIME, date_time);

		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteRecord(long gigId) {
		return db.delete(DATABASE_TABLE, KEY_GIGID + "=" + gigId, null) > 0;
	}

	public Cursor getAllRecords() {
		return db.query(DATABASE_TABLE, new String[] { KEY_GIGID, KEY_ARTIST,
				KEY_LAST_TWEET_ID, KEY_LAST_UPDATED, KEY_ISTRACKING, KEY_VENUE,
				KEY_DATE_TIME }, null, null, null, null, null);

	}
	
	public Cursor getAllActiveRecords() {
		return db.query(DATABASE_TABLE, new String[] { KEY_GIGID, KEY_ARTIST,
				KEY_LAST_TWEET_ID, KEY_LAST_UPDATED, KEY_ISTRACKING, KEY_VENUE,
				KEY_DATE_TIME }, KEY_ISTRACKING+"=1", null, null, null, null);
	}

	@SuppressLint("NewApi")
	// Suppress API warning. TODO: Check works on API < 16.
	public Cursor getRecord(long rowId) throws SQLException {

		// *************
		// SDK CHECK: MIN API 4
		// NOTE: SDK_INT below requires Donut (android 1.6 / API4). Android 1.5
		// / API3 WILL CRASH APP!
		// *************
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

			// FOR JELLYBEAN AND ABOVE
			Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
					KEY_GIGID, KEY_ARTIST, KEY_LAST_TWEET_ID, KEY_LAST_UPDATED,
					KEY_ISTRACKING, KEY_VENUE, KEY_DATE_TIME }, KEY_GIGID + "="
					+ rowId, null, null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;

		} else {
			// FOR SDKS BEFORE JELLYBEAN
			Cursor mCursor = db.query(DATABASE_TABLE, new String[] { KEY_GIGID,
					KEY_ARTIST, KEY_LAST_TWEET_ID, KEY_LAST_UPDATED,
					KEY_ISTRACKING, KEY_VENUE, KEY_DATE_TIME }, KEY_GIGID + "="
					+ rowId, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
	}

	// Check record exists
	public boolean checkRecExists(long rowId) throws SQLException {
		Cursor c = db.query(DATABASE_TABLE, new String[] { KEY_GIGID },
				KEY_GIGID + "=" + rowId, null, null, null, null);

		if (c.moveToFirst()) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}

	// Check if there are any active gigs
	public boolean checkForActiveGigs() throws SQLException {
		String[] columns = new String[] { KEY_GIGID, KEY_ISTRACKING };
		Cursor c = db.query(DATABASE_TABLE, columns, KEY_ISTRACKING+"=1", null, null, null, null);
		
		if (c.moveToFirst()) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}
		
	public boolean updateRecord(long gigId, String artist,
			String last_tweet_id, String last_updated, int bool, String venue,
			String date_time) {
		ContentValues args = new ContentValues();
		args.put(KEY_GIGID, gigId);
		args.put(KEY_ARTIST, artist);
		args.put(KEY_LAST_TWEET_ID, last_tweet_id);
		args.put(KEY_LAST_UPDATED, last_updated);
		args.put(KEY_ISTRACKING, bool);
		args.put(KEY_VENUE, venue);
		args.put(KEY_DATE_TIME, date_time);

		return db.update(DATABASE_TABLE, args, KEY_GIGID + "=" + gigId, null) > 0;

	}

	public boolean updateLastTweetId(long gigId, String last_tweet_id) {
		ContentValues args = new ContentValues();
		args.put(KEY_LAST_TWEET_ID, last_tweet_id);

		return db.update(DATABASE_TABLE, args, KEY_GIGID + "=" + gigId, null) > 0;
	}

	public boolean updateIsTracking(long gigId, int isTracking) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISTRACKING, isTracking);

		return db.update(DATABASE_TABLE, args, KEY_GIGID + "=" + gigId, null) > 0;
	}
	
	public boolean updateLastUpdated(long gigId, String last_updated) {
		ContentValues args = new ContentValues();
		args.put(KEY_LAST_UPDATED, last_updated);

		return db.update(DATABASE_TABLE, args, KEY_GIGID + "=" + gigId, null) > 0;
	}

	// Methods for returning data as objects

	public List<Gig> getAllRecordsAsObjects() {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_GIGID, KEY_ARTIST,
				KEY_LAST_TWEET_ID, KEY_LAST_UPDATED, KEY_ISTRACKING, KEY_VENUE,
				KEY_DATE_TIME };
		Cursor c = db.query(DATABASE_TABLE, columns, null, null, null, null,
				null);
		List<Gig> gigsBeingTracked = new ArrayList<Gig>();

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			Gig dbGig = cursorToDBGigEntry(c);
			gigsBeingTracked.add(dbGig);
		}
		c.close();
		return gigsBeingTracked;
	}

	public Gig cursorToDBGigEntry(Cursor c) {
		Gig dbGig = new Gig(c.getInt(c.getColumnIndex(KEY_GIGID)),
						c.getString(c.getColumnIndex(KEY_ARTIST)),
						c.getString(c.getColumnIndex(KEY_VENUE)),
						c.getString(c.getColumnIndex(KEY_DATE_TIME)),
						null,
						0,
						c.getString(c.getColumnIndex(KEY_LAST_TWEET_ID)),
						c.getString(c.getColumnIndex(KEY_LAST_UPDATED)),
						c.getInt(c.getColumnIndex(KEY_ISTRACKING)));
		
		return dbGig;
	}

}
