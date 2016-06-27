package com.barelabor.barelabor.debug;

import com.barelabor.barelabor.debug.Log;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// TODO remove me
public class LogActivity extends AppCompatActivity {//ActionBarActivity {

	protected void onCreate(Bundle savedInstanceState) {
		Log.i(this, "onCreate: %1$s. State: %2$s", this, savedInstanceState);

		super.onCreate(savedInstanceState);
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		Log.d(this, "onPostCreate: %1$s. State: %2$s", this, savedInstanceState);

		super.onPostCreate(savedInstanceState);
	}

	protected void onDestroy() {
		Log.i(this, "onDestroy: %1$s", this);

		super.onDestroy();
	}

	public void onConfigurationChanged(Configuration configuration) {
		Log.i(this, "onConfigurationChanged: %1$s", this);

		super.onConfigurationChanged(configuration);
	}

	protected void onSaveInstanceState(Bundle bundle) {
		Log.d(this, "onSaveInstanceState: %1$s. State: %2$s", this, bundle);

		super.onSaveInstanceState(bundle);
	}

	protected void onRestoreInstanceState(Bundle bundle) {
		Log.d(this, "onRestoreInstanceState: %1$s. State: %2$s", this, bundle);

		super.onRestoreInstanceState(bundle);
	}

	protected void onResume() {
		Log.i(this, "onResume: %1$s", this);

		super.onResume();
	}

	protected void onResumeFragments() {
		Log.d(this, "onResumeFragments: %1$s", this);
		super.onResumeFragments();
	}

	protected void onPostResume() {
		Log.d(this, "onPostResume: %1$s", this);

		super.onPostResume();
	}

	protected void onPause() {
		Log.i(this, "onPause: %1$s", this);

		super.onPause();
	}

	protected void onStart() {
		Log.d(this, "onStart: %1$s", this);

		super.onStart();
	}

	protected void onStop() {
		Log.d(this, "onStop: %1$s", this);
		
		super.onStop();
	}

	protected void onRestart() {
		Log.d(this, "onRestart: %1$s", this);

		super.onRestart();
	}

}
