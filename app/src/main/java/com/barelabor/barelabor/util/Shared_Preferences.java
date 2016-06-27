package com.barelabor.barelabor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Shared_Preferences {

	SharedPreferences sharedpreferences;
	String MyPREFERENCES = "barelabor_app";

	private final String PREFERENCE_KEY_USERID = "userId";
	private final String PREFERENCE_KEY_DEVICETOKEN = "device_token";
	private final String PREFERENCE_KEY_ESTIMATEID = "estimateID";

	Editor editor;

	public Shared_Preferences(Context _context) {
		sharedpreferences = _context.getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
	}

	public void storeUserId(String userId){
		editor.putString(PREFERENCE_KEY_USERID, userId);
		editor.commit();
	}

	public String getUserId(){
		return sharedpreferences.getString(PREFERENCE_KEY_USERID, "");
	}

	public void storeDeviceToken(String deviceToken){
		editor.putString(PREFERENCE_KEY_DEVICETOKEN, deviceToken);
		editor.commit();
	}

	public String getDeviceToken(){
		return sharedpreferences.getString(PREFERENCE_KEY_DEVICETOKEN, "");
	}

	public void storeEstimateID(String estimateID){
		editor.putString(PREFERENCE_KEY_ESTIMATEID, estimateID);
		editor.commit();
	}

	public String getEstimateID(){
		return sharedpreferences.getString(PREFERENCE_KEY_ESTIMATEID, "");
	}

	public Boolean isLogin(){
		if (getUserId().length() == 0)
			return false;

		return true;
	}

	public void clearData(){
		editor.putString(PREFERENCE_KEY_USERID, "");
		editor.putString(PREFERENCE_KEY_DEVICETOKEN, "");
		editor.putString(PREFERENCE_KEY_ESTIMATEID, "");
		editor.commit();
	}
}
