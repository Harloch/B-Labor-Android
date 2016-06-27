package com.barelabor.barelabor.data;

import android.os.AsyncTask;
import com.barelabor.barelabor.data.WebServiceRequest;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.debug.Log;


public abstract class DataTask extends AsyncTask<WebServiceRequest, Long, ResponseData> {
	private WebServiceRequest request;
	private boolean isIgnored;
	
	public static abstract interface IDataTaskCallback {
		public abstract void processData(WebServiceRequest request, DataObject dataObject);

		public abstract void finishRequest(DataTask task, ResponseData responseData);
	}

	protected IDataTaskCallback callback;

	public DataTask(IDataTaskCallback callback) {
		this.callback = callback;
	}

	public IDataTaskCallback getCallback() {
		return callback;
	}

	public boolean isIgnored() {
		return isIgnored;
	}
	
	public void ignore() {
		Log.i(this, "Ignore: %1$s. Task: %2$s", isIgnored, this);

		isIgnored = true;
	}

	protected abstract ResponseData runTask(WebServiceRequest request);

	@Override
	protected final ResponseData doInBackground(WebServiceRequest... params) {
		ResponseData responseData = null;

		request = params[0];

		Log.i(this, "Run in  background: %1$s", this);
		responseData = runTask(request);
		Log.i(this, "Run in  background finished: %1$s", this);

		return responseData;
	}

	@Override
	protected final void onPostExecute(ResponseData result) {
		Log.i(this, "Post execute: %1$s", this);
		if (callback != null) {
			callback.finishRequest(this, result);
		}
	}

	@Override
	public String toString() {
		return String.format("%1$s:%2$s:%3$s", super.toString(), request, callback);
	}
}
