package com.barelabor.barelabor.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.barelabor.barelabor.BLApplication;
import com.barelabor.barelabor.data.DataError;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.model.ShopObject;
import com.barelabor.barelabor.debug.Log;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Support {

	private static final String PREFERENCE = "barelabor_pref";

	/** The Constant DENSITY_XXHDPI. - because we are using min API 14. */
	private static final float DENSITY_XXHDPI = 3;

	public static final TextView findTextView(View view, int idResource) {
		return (TextView) view.findViewById(idResource);
	}

	public static final EditText findEditText(View view, int idResource) {
		return (EditText) view.findViewById(idResource);
	}

	public static final TextView findTextView(Activity activity, int idResource) {
		return (TextView) activity.findViewById(idResource);
	}

	public static final EditText findEditText(Activity activity, int idResource) {
		return (EditText) activity.findViewById(idResource);
	}

	public static boolean isConnection(Activity activity) {
		return getApp(activity).getNetworkMonitor().isConnected();
	}

	public static void handleError(Activity activity, DataError error, boolean finish) {
		switch (error.getType()) {

		case UnstableConnection:

			if (activity instanceof BackToDefaultActivityInterface) {

				ActivityBackCallback backCallback = new ActivityBackCallback((BackToDefaultActivityInterface) activity);

				getMessageService(activity).showError(activity, error.getMessage(), backCallback);

			}

			break;
		default:
			getMessageService(activity).showError(activity, error, finish);
			break;
		}
	}

	public interface BackToDefaultActivityInterface {

		public abstract void backToDefaultActivty();

	}

	public static BLApplication getApp(Activity activity) {
		return (BLApplication) activity.getApplication();
	}

	public static DataProvider getDataProvider(Activity activity) {
		return getApp(activity).getDataProvider();
	}

	public static DataProviderCallback getDataProviderCallback(Activity activity) {
		return getApp(activity).getDataProviderCallback();
	}

	public static MessageService getMessageService(Activity activity) {
		return getApp(activity).getMessageService();
	}

	public static Shared_Preferences getSharedPreference(Activity activity) {
		return getApp(activity).getSharedPreferences();
	}

	public static String getText(Activity activity, int defaultResource) {
		return activity.getResources().getString(defaultResource);
	}

	private static class ActivityBackCallback implements DialogInterface.OnClickListener {
		private BackToDefaultActivityInterface activity;

		public ActivityBackCallback(BackToDefaultActivityInterface activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			this.activity.backToDefaultActivty();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDateShort(Activity activity, Date date) {
		return new SimpleDateFormat("dd.MM.yyyy").format(date);
	}

	public static boolean isExtraLarge(Context context) {
		return (0xF & context.getResources().getConfiguration().screenLayout) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	public static boolean isHDPI(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_HIGH;
	}

	public static boolean isLDPI(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_LOW;
	}

	public static boolean isLarge(Context context) {
		return (0xF & context.getResources().getConfiguration().screenLayout) == Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isMDPI(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_MEDIUM;
	}

	public static boolean isNormal(Context context) {
		return (0xF & context.getResources().getConfiguration().screenLayout) == Configuration.SCREENLAYOUT_SIZE_NORMAL;
	}

	public static boolean isSmall(Context context) {
		return (0xF & context.getResources().getConfiguration().screenLayout) == Configuration.SCREENLAYOUT_SIZE_SMALL;
	}

	public static boolean isXHDPI(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_XHIGH;
	}

	public static boolean isXXHDPI(Context context) {
		return (context.getResources().getDisplayMetrics().density == DENSITY_XXHDPI);
	}

	public static String getUserInfos(Activity activity){
		String s="Debug-infos:";
		s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
		s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
		s += "\n Device: " + android.os.Build.DEVICE;
		s += "\n MANUFACTURER: " + Build.MANUFACTURER;
		s += "\n ScreenResolution: " + getScreenResolution(activity);
		s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
		return s;
	}

	public static String getScreenResolution(Activity activity){
		int width, height;
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			width = activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
			height = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
		}else{
			Display display = activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		}

		return width + "x" + height;
	}

	public static String getApplicationVersion(Activity activity){
		try {
			PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(activity, e);
		}

		return "0.0";
	}

	public static String loadJSONFromAsset(Context context, String fileName) {
		String json = null;
		try {

			InputStream is = context.getAssets().open(fileName);

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new String(buffer, "UTF-8");


		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}

	public static String trimPhoneNumber(String phoneNumber){
		String number = phoneNumber;
		number = number.replace(" ", "");
		number = number.replace("_", "");
		number = number.replace("(", "");
		number = number.replace(")", "");

		return number;
	}
	public static String getFullAddress(ShopObject data){
		String fullAddress = "";

		fullAddress += data.getZipCode();
		fullAddress += data.getAddress();

		String distance = "";
		if(data.getDistance() > 0) {
			distance = String.format("%.2f", data.getDistance()) + " mi";

			if(fullAddress.length() > 0){
				fullAddress += " - " + distance;
			}else{
				fullAddress = distance;
			}
		}

		String city = data.getCity();
		if (city.length() == 0)
			city = data.getState();
		else
			city += ", " + data.getState();

		if(city.length() > 0) {
			if (fullAddress.length() > 0) {
				fullAddress += " - " + city;
			} else {
				fullAddress = city;
			}
		}

		return fullAddress;
	}
}