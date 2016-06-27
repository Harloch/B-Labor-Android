package com.barelabor.barelabor.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.barelabor.barelabor.BLApplication;
import com.barelabor.barelabor.debug.Log;

public class NetworkMonitor extends BroadcastReceiver {
	public static abstract interface INetworkAvailabilityChangedListener {
		public abstract void onNetworkConnected();
	}

	private Context context;
	private INetworkAvailabilityChangedListener networkAvailabilityChangedListener;

	public NetworkMonitor(Context context) {
		this.context = context;
	}

	public NetworkMonitor() {
	}
	
	public void setNetworkAvailabilityChangedListener(
			INetworkAvailabilityChangedListener networkAvailabilityChangedListener) {
		this.networkAvailabilityChangedListener = networkAvailabilityChangedListener;
	}
	
	public boolean isConnected() {
		return isConnected(this.context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (isConnected(context)) {
			BLApplication application;
			
			application = (BLApplication) context.getApplicationContext();
			application.getNetworkMonitor().fireConnectivityChanged();
		}
	}

	private void fireConnectivityChanged() {
		if (this.networkAvailabilityChangedListener != null) {
			Log.i(this, "Fire connnectivity changed");

			if (this.networkAvailabilityChangedListener != null) {
				this.networkAvailabilityChangedListener.onNetworkConnected();
			}
		}
	}

	private boolean isConnected(Context context) {
		NetworkInfo networkInfo;
		ConnectivityManager connectivityManager;
		
		connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
		networkInfo = connectivityManager.getActiveNetworkInfo();

		return (networkInfo != null) && (networkInfo.isConnectedOrConnecting());
	}

}
