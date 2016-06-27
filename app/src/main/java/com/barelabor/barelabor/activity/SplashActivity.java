package com.barelabor.barelabor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.gcm.QuickstartPreferences;
import com.barelabor.barelabor.gcm.RegistrationIntentService;
import com.barelabor.barelabor.util.Constants;
import com.barelabor.barelabor.util.Support;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MixpanelAPI mixPanel =  MixpanelAPI.getInstance(this, Constants.MIXPANEL_TOKEN);

        mixPanel.track("App launched");

        Support.getMessageService(this).showProgressDialog(this, true);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(SplashActivity.this, getString(R.string.gcm_send_message), Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(SplashActivity.this, getString(R.string.token_error_message), Toast.LENGTH_SHORT);
                }

                Support.getMessageService(SplashActivity.this).hideProgressDialog();

                String deviceToken = sharedPreferences.getString(QuickstartPreferences.TOKEN, "");
                Support.getSharedPreference(SplashActivity.this).storeDeviceToken(deviceToken);

                showMainScreen();
            }
        };

        // Registering BroadcastReceiver

        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }else{
            showMainScreen();
        }

        /*
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

            }
        }, Constants.SPLASH_TIME_OUT);
        */
    }

    private  void showMainScreen(){
        Intent i;
        if(Support.getSharedPreference(SplashActivity.this).isLogin()){
            i = new Intent(SplashActivity.this, MenuActivity.class);
        }else{
            i = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(i);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("SplashActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
