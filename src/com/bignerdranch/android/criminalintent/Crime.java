package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {

	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	public static final String JSON_PHOTO = "photo";
	public static final String JSON_SUSPECT = "suspect";
	public static final String JSON_SUSPECT_NUMBER = "number";

	private UUID mId;
	private String mTitle;
	private Date mDate;
	private Photo mPhoto;
	private boolean mSolved;
	private String mSuspect;
	private String mSuspectNumber;

	public Crime() {
		// Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
	}

	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		if (json.has(JSON_TITLE)) {
			mTitle = json.getString(JSON_TITLE);
		}
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));

		if (json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
		if (json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
		}
		if(json.has(JSON_SUSPECT_NUMBER)){
			mSuspectNumber = json.getString(JSON_SUSPECT_NUMBER);
		}
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		if (mPhoto != null){
			json.put(JSON_PHOTO, mPhoto.toJSON());
		}
		json.put(JSON_SUSPECT, mSuspect);
		json.put(JSON_SUSPECT_NUMBER, mSuspectNumber);
		return json;
	}

	@Override
	public String toString() {
		return mTitle;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public UUID getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo p) {
		mPhoto = p;
	}
	
	public String getSuspect() {
		return mSuspect;
	}
	
	public void setSuspect(String suspect){
		mSuspect = suspect;
		
	}
	public String getSuspectNumber() {
		return mSuspectNumber;
	}

	public void setSuspectNumber(String suspectNumber) {
		mSuspectNumber = suspectNumber;
	}
	
	
}//class end
