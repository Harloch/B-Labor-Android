package com.barelabor.barelabor.base;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.barelabor.barelabor.data.DataError;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.IDataProviderCallback;
import com.barelabor.barelabor.data.WebServiceRequestType;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.debug.LogActivity;
import com.barelabor.barelabor.util.Support;

import java.util.Date;

public abstract class BaseActivity<T extends DataObject> extends LogActivity implements IDataProviderCallback {
	private static final String STATE_IS_LOADING_DATA = "is_loading_data";
	private static final String STATE_IS_POSTING_DATA = "is_posting_data";
	private static final String STATE_IS_POSTING_MODE = "is_posting_mode";
	private static final String STATE_IS_CONFIG_CHANGED = "is_config_changed";
	private static final String STATE_IS_LOADING_FROM_SAVED_INSTANCE = "is_loading_from_saved_instance";

	private boolean loadedFromSavedInstance;
	private boolean visible;
	private boolean reloadNeeded;
	private boolean isLoadingData;
	private boolean isPostingData;
	private boolean isPostingMode;
	private boolean suppressExitOnError;
	private boolean suppressProgressDialog;
	private T data;

	protected Date startDate;

	public void setSuppressProgressDialog(boolean suppressProgressDialog) {
		this.suppressProgressDialog = suppressProgressDialog;
	}

	public void setReloadNeeded(boolean reloadNeeded){
		this.reloadNeeded = reloadNeeded;
	}
	
	@Override
	public void finish() {
		super.finish();

		Log.i(this, "Finish: %1$s", this);

		Support.getDataProviderCallback(this).reset();
	}

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		startDate = new Date();

		if (savedInstance != null && savedInstance.containsKey(STATE_IS_CONFIG_CHANGED)) {
			this.loadedFromSavedInstance = true;
		}

		Log.i(this, "Restoring: %1$s", this.loadedFromSavedInstance);

		if (isLoadedFromSavedInstance()) {
			this.isLoadingData = savedInstance.getBoolean(STATE_IS_LOADING_DATA);
			this.isPostingData = savedInstance.getBoolean(STATE_IS_POSTING_DATA);
			this.isPostingMode = savedInstance.getBoolean(STATE_IS_POSTING_MODE);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);


		/*
		if (!isLoadedFromSavedInstance()) {
			loadData();
			reloadNeeded = false;
		}
		*/
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);

		bundle.putBoolean(STATE_IS_LOADING_DATA, this.isLoadingData);
		bundle.putBoolean(STATE_IS_POSTING_DATA, this.isPostingData);
		bundle.putBoolean(STATE_IS_POSTING_MODE, this.isPostingMode);
		bundle.putBoolean(STATE_IS_LOADING_FROM_SAVED_INSTANCE, true);
		if (isChangingConfigurations()) {
			bundle.putBoolean(STATE_IS_CONFIG_CHANGED, true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();

		this.visible = true;
		this.reloadNeeded = false;

//		Log.i(this, "Loaded from saved instance: %1$s. Reload needed: %2$s", isLoadedFromSavedInstance(), reloadNeeded);
//
//		Support.getDataProviderCallback(this).setListener(this);
//
//		if (reloadNeeded && !this.isLoadingData) {
//			Log.i(this, "Data out of date. Reload");
//
//			loadData();
//		}
//
//		if ((this.isLoadingData && !this.suppressProgressDialog) || this.isPostingData) {
//			Support.getMessageService(this).showProgressDialog(this, this.isLoadingData);
//		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Support.getDataProviderCallback(this).setListener(null);
		Support.getMessageService(this).hideAllDialogs();
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.visible = false;
		this.reloadNeeded = !isChangingConfigurations();

		Support.getDataProviderCallback(this).setListener(null);
		Support.getMessageService(this).hideAllDialogs();
	}

	public void setSuppressExitOnError(boolean suppressExitOnError) {
		this.suppressExitOnError = suppressExitOnError;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isLoadingData() {
		return isLoadingData;
	}

	public boolean isPostingData() {
		return isPostingData;
	}

	public boolean isLoadedFromSavedInstance() {
		return loadedFromSavedInstance;
	}

	public final void loadData() {
		Support.getDataProviderCallback(this).reset();
		Support.getDataProviderCallback(this).setListener(this);

		this.data = null;
		this.isPostingMode = false;
		this.isLoadingData = loadData(Support.getDataProvider(this), Support.getDataProviderCallback(this));

		if (!this.isLoadingData) {
			Support.getDataProviderCallback(this).setListener(null);
		}
		Log.i(this, "Load data: %1$s", this.isLoadingData);

		if (this.visible && this.isLoadingData) {
			Support.getMessageService(this).showProgressDialog(this, true);
		}
	}

	public final void postData() {
		InputMethodManager imm;

		if (getCurrentFocus() != null) {
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		Support.getDataProviderCallback(this).reset();
		Support.getDataProviderCallback(this).setListener(this);

		this.isPostingMode = true;
		this.isPostingData = postData(Support.getDataProvider(this), Support.getDataProviderCallback(this));

		if (!this.isPostingData) {
			Support.getDataProviderCallback(this).setListener(null);
		}

		Log.i(this, "Post data: %1$s", this.isPostingData);

		if (this.visible && this.isPostingData) {
			Support.getMessageService(this).showProgressDialog(this, false);
		}
	}

	@Override
	public final void onDataProviderError(WebServiceRequestType requestType, DataError error) {
		Log.i(this, "Error received: %1$s. Request: %2$s. Visible: %3$s", requestType, error, this.visible);

		if (this.visible) {
			// ignore failed update if data from cache were loaded
			if (data == null || error.getType() == DataError.ErrorType.InvalidSession) {
				onError(error);
				Support.handleError(this, error, !this.isPostingMode && !suppressExitOnError);
			} else {
				Log.i(this, "Ignore error. Data not null: %1$s", this.data);
			}
		}

		this.isLoadingData = false;
		this.isPostingData = false;

	}

	@Override
	@SuppressWarnings("unchecked")
	public final void onDataReceived(WebServiceRequestType requestType, DataObject dataObject) {
		Log.i(this, "Data received: %1$s. Request: %2$s ... "+isLoadingFinished(), dataObject, requestType);

		if (this.visible) {
			if (this.isPostingMode) {
				Support.getMessageService(this).hideProgressDialog();

				Log.i(this, "Handle post response: %1$s", dataObject);
				handlePostResponse(dataObject);
			} else {
				setData(requestType, (T) dataObject);

				if (isLoadingFinished()) {
					Support.getMessageService(this).hideProgressDialog();
				}

				if (!isLoadingFinished()) {
					loadData();
				}
			}
		}

		this.isLoadingData = false;
		this.isPostingData = false;
	}

	protected void setData(WebServiceRequestType requestType, T dataObject) {
		this.data = (T) dataObject;

		Log.i(this, "Update view: %1$s", dataObject);

		updateView((T) dataObject);
	}

	protected void onError(DataError error) {
	}

	protected boolean isLoadingFinished() {
		return true;
	}

	protected boolean loadData(DataProvider dataProvider, DataProviderCallback callback) {
		return false;
	}

	protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {
		return false;
	}

	public T getData() {
		return data;
	}

	protected void updateView(T dataObject) {
	}

	protected void handlePostResponse(DataObject dataObject) {

	}

}
