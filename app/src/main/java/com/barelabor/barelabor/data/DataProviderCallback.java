package com.barelabor.barelabor.data;

import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.data.DataError.ErrorType;

public class DataProviderCallback implements IDataProviderCallback {
	private DataObject dataObject;
	private DataProvider dataProvider;
	private DataError error;
	private IDataProviderCallback listener;
	private WebServiceRequestType requestType;

	public DataProviderCallback(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void reset() {
		Log.i(this, "Reset");

		this.dataProvider.cancelCallback(this);
		this.error = null;
		this.dataObject = null;
	}

	public void setListener(IDataProviderCallback callback) {
		Log.i(this, "Set listener: %1$s", callback);

		this.listener = callback;

		if (this.listener != null && (this.dataObject != null || this.error != null)) {
			callCallback();
		}
	}

	private void callCallback() {
		Log.i(this, "Call callback: %1$s. Has data: %2$s. Has error: %3$s", this.listener, this.dataObject, this.error);

		if (this.error != null) {
			this.listener.onDataProviderError(this.requestType, this.error);
		} else if (this.dataObject != null) {
			this.listener.onDataReceived(this.requestType, this.dataObject);
		}
	}

	public void onDataProviderError(WebServiceRequestType requestType, DataError error) {
		Log.i(this, "Error received: %1$s (%2$s)", new Object[] { requestType, error });

		if (this.dataObject == null || error.getType() != ErrorType.NoNetwork) {
			this.requestType = requestType;
			this.dataObject = null;
			this.error = error;

			if (this.listener != null) {
				callCallback();
			}
		} else {
			Log.i(this, "Ignore error: %1$s", error);
		}
	}

	public void onDataReceived(WebServiceRequestType requestType, DataObject dataObject) {
		Log.i(this, "Data received: %1$s (%2$s)", new Object[] { requestType, dataObject });

		this.requestType = requestType;
		this.dataObject = dataObject;
		this.error = null;

		if (this.listener != null) {
			callCallback();
		}
	}

}
