package com.barelabor.barelabor.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.barelabor.barelabor.data.DataError.ErrorType;
import com.barelabor.barelabor.data.DataTask.IDataTaskCallback;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.debug.Debug;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.util.NetworkMonitor;
import com.barelabor.barelabor.util.NetworkMonitor.INetworkAvailabilityChangedListener;
import com.barelabor.barelabor.util.NotImplementedException;

public class DataProvider implements INetworkAvailabilityChangedListener {

	private IOnPrepareListener onPrepareListener;

	@Override
	public void onNetworkConnected() {

	}

	public interface IOnPrepareListener {
		void prepareReady();

		void prepareError();
	}

	public interface IOnOfflineDataSynchronized {
		void onOfflineDataSynchronized();
	}

	private static final String LINKS_FILE = "newsitem.links";
	private static final String VOCABULARY_LINKS_FILE = "vocabulary.links";
	private static final String GRAMMAR_LINKS_FILE = "grammar.links";

	private static final String EMPTY_SESSION = null;

	private NetworkMonitor networkMonitor;
	private List<DataTask> tasks;

	private JsonDataHandler jsonDataHandler;

	public DataProvider(Context context, Locale locale, NetworkMonitor networkMonitor) {

		this.networkMonitor = networkMonitor;
		this.tasks = new ArrayList<DataTask>();
		this.jsonDataHandler = new JsonDataHandler();
	}

	public void setOnPrepareListener(IOnPrepareListener onPrepareListener) {
		this.onPrepareListener = onPrepareListener;
	}

	public void cancelCallback(DataProviderCallback callback) {
		Log.i(this, "Cancel callback. Current tasks: %1$d", tasks.size());

		for (DataTask task : tasks) {
			BaseCallback baseCallback;

			baseCallback = (BaseCallback) task.getCallback();
			if (baseCallback != null && baseCallback.getCallback() == callback) {
				Log.i(this, "Cancel task: %1$s", task);

				task.ignore();
			}
		}
	}

	public void get(WebServiceRequest request, IDataProviderCallback callback, WebServiceTaskCallback webServiceTaskCallback) {
		send(request, webServiceTaskCallback);
	}

	public void send(WebServiceRequest request, IDataTaskCallback callback) {
		WebServiceTask webServiceTask;

		webServiceTask = new WebServiceTask(callback, networkMonitor);
		tasks.add(webServiceTask);

		Log.i(this, "Add web service task: %1$s", tasks.size());

		webServiceTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, request);
	}

	public boolean login(String username, String password, String deviceToken, IDataProviderCallback callback) {
		if (networkMonitor.isConnected()) {
			WebServiceRequest request;

			Log.i(this, "Login: %1$s", username);

			//request = createLoginRequest(username, password);

			request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_LOGIN);
			request.setParameter(WebServiceRequest.PARAM_USERNAME, username);
			request.setParameter(WebServiceRequest.PARAM_PASSWORD, password);
			request.setParameter(WebServiceRequest.PARAM_DEVICETOKEN, deviceToken);

			send(request, new WebServiceTaskCallback(callback));

			return true;
		} else {
			callback.onDataProviderError(WebServiceRequestType.Login, DataError.NO_NETWORK);
			return false;
		}
	}

	public boolean signup(String username, String password, String email, String deviceToken, IDataProviderCallback callback) {

		if (networkMonitor.isConnected()) {
			WebServiceRequest request;

			Log.i(this, "Signup: %1$s", username);

			request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_REGISTER);
			request.setParameter(WebServiceRequest.PARAM_USERNAME, username);
			request.setParameter(WebServiceRequest.PARAM_PASSWORD, password);
			request.setParameter(WebServiceRequest.PARAM_EMAIL, email);
			request.setParameter(WebServiceRequest.PARAM_DEVICETOKEN, deviceToken);

			send(request, new WebServiceTaskCallback(callback));

			return true;
		} else {
			callback.onDataProviderError(WebServiceRequestType.Register, DataError.NO_NETWORK);
			return false;
		}
	}

	/**

	 GET MAKE

	 - parameter year From year's make search
	 */

	public void getMakeFromYear(String year, IDataProviderCallback callback) {
		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_GETMAKE);
		request.setParameter(WebServiceRequest.PARAM_YEAR, year);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	/**
	 GET MODEL

	 - parameter year From year's make search
	 - parameter make Get's make from year
	 */

	public void getModel(String year, String make, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_GETMODEL);
		request.setParameter(WebServiceRequest.PARAM_YEAR, year);
		request.setParameter(WebServiceRequest.PARAM_MAKE, make);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	/**
	 GET FEATURES

	 - parameter year From year's make search
	 - parameter make Get's make from year
	 - parameter model Get's model from make
	 */

	public void getFeatures(String year, String make, String model, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_GETFEATURES);
		request.setParameter(WebServiceRequest.PARAM_YEAR, year);
		request.setParameter(WebServiceRequest.PARAM_MAKE, make);
		request.setParameter(WebServiceRequest.PARAM_MODEL, model);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	/**
	 GET PRICE BY VEHICLE

	 - parameter year From year's make search
	 - parameter make Get's make from year
	 - parameter model Get's model from make
	 - parameter feature Get's feature from model
	 */

	public void getPriceByVehicle(String year, String make, String model, String feature, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_GETPRICEBYVEHICLE);
		request.setParameter(WebServiceRequest.PARAM_YEAR, year);
		request.setParameter(WebServiceRequest.PARAM_MAKE, make);
		request.setParameter(WebServiceRequest.PARAM_MODEL, model);
		request.setParameter(WebServiceRequest.PARAM_FEATURE, feature);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	/**
	 GET PRICE BY SIZE

	 - parameter width Width of searched object
	 - parameter ratio Ratio of searched object
	 - parameter diameter Diameter of searched object
	 */

	public void getPriceBySize(String width, String ratio, String diameter, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_GETPRICEBYSIZE);
		request.setParameter(WebServiceRequest.PARAM_WIDTH, width);
		request.setParameter(WebServiceRequest.PARAM_RATIO, ratio);
		request.setParameter(WebServiceRequest.PARAM_DIAMETER, diameter);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	/**
	 SUBMIT EXPERIENCE

	 - parameter userID Logged user ID
	 - parameter type Can be positive or negative
	 - parameter answers User answers
	 - parameter name User name
	 - parameter email User Email
	 - parameter shop_name User shop name
	 - parameter comments User comments
	 */

	public void sumbitExperience(String userId, String type, String answer, String name, String email, String shopName, String comment, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_SUBMITEXPERIENCE);

		request.setParameter(WebServiceRequest.PARAM_USERID, userId);
		request.setParameter(WebServiceRequest.PARAM_TYPE, type);
		request.setParameter(WebServiceRequest.PARAM_ANSWERS, answer);
		request.setParameter(WebServiceRequest.PARAM_NAME, name);
		request.setParameter(WebServiceRequest.PARAM_EMAIL, email);
		request.setParameter(WebServiceRequest.PARAM_SHOPNAME, shopName);
		request.setParameter(WebServiceRequest.PARAM_COMMENTS, comment);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	public void submitEstimateImage(String userId, Bitmap estimateImage, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_SUBMITESTIMATE);
		request.setParameter(WebServiceRequest.PARAM_USERID, userId);
		request.setBitmap(estimateImage);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	public void getEstimate(String userId, String estimateId, IDataProviderCallback callback) {

		WebServiceRequest request;

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_GETESTIMATE);
		request.setParameter(WebServiceRequest.PARAM_USERID, userId);
		request.setParameter(WebServiceRequest.PARAM_ESTIMATEID, estimateId);
		get(request, callback, new WebServiceTaskCallback(callback));
	}

	/**
	 GET NEAREST LOCATIONS BY USER LOCATION
	 - parameter latitude User location latitude
	 - parameter longitude User location longitude
	 */

	public void getNearestLocationWithLocationLatitude(String lat, String lng, IDataProviderCallback callback) {

		WebServiceRequest request;

		Log.d("Latitude=", lat);
		Log.d("Longitude=", lng);

		request = new WebServiceRequest(WebServiceRequest.HTTPMethod.Post, WebServiceRequest.METHOD_FINDNEARSHOP);
		request.setParameter(WebServiceRequest.PARAM_LAT, lat);
		request.setParameter(WebServiceRequest.PARAM_LNG, lng);

		send(request, new FindShopCallback(callback));
	}

	private class ErrorCallback extends WebServiceTaskCallback {

		public ErrorCallback() {
			super(null);
		}

		@Override
		public void finishRequest(DataTask task, ResponseData responseData) {
			tasks.remove(task);
		}
	}

	/*
	private class RegisterCallback extends WebServiceTaskCallback {
		private RegistrationData registrationData;

		public RegisterCallback(IDataProviderCallback callback) {
			super(callback);
		}

		@Override
		protected void invokeCallbackback(DataTask task, ResponseData responseData) {
			if (responseData.getError() != null) {
				if (callback != null) {
					callback.onDataProviderError(WebServiceRequestType.Subscribe, responseData.getError());
				}
			} else {
				switch (responseData.getRequest().getType()) {
				case Subscribe:
					WebServiceRequest request;

					registrationData = (RegistrationData) responseData.getDataObject();
					request = createLoginRequest(registrationData.getUserName(), registrationData.getPassword(), registrationData.getLanguage(), registrationData.getUserInfo());

					send(request, this);
					break;
				case Login:
					if (callback != null) {
						callback.onDataReceived(WebServiceRequestType.Subscribe, registrationData);
					}
					break;
				default:
					throw new NotImplementedException();
				}
			}
		}
	}
	*/

	private class FindShopCallback extends WebServiceTaskCallback {

		public FindShopCallback(IDataProviderCallback callback) {
			super(callback);
		}


		@Override
		protected void invokeCallbackback(DataTask task, ResponseData responseData) {
			if (responseData.getError() != null) {
				if (callback != null) {
					//callback.onDataProviderError(responseData.getRequest().getType(), responseData.getError()); // TODO
					callback.onDataReceived(responseData.getRequest().getType(), new DataObject());
				}
			} else {
				// crazy chain of requests

				if (callback != null && !task.isIgnored()) {
					callback.onDataReceived(responseData.getRequest().getType(), responseData.getDataObject());
				}
			}
		}

	}

	private class WebServiceTaskCallback extends BaseCallback {
		public WebServiceTaskCallback(IDataProviderCallback callback) {
			super(callback);
		}

		@Override
		public void processData(WebServiceRequest request, DataObject dataObject) {
			super.processData(request, dataObject);
		}
	}

	private class BaseCallback implements IDataTaskCallback {
		protected IDataProviderCallback callback;

		public BaseCallback(IDataProviderCallback callback) {
			this.callback = callback;
		}

		public IDataProviderCallback getCallback() {
			return callback;
		}

		@Override
		public void processData(WebServiceRequest request, DataObject dataObject) {
			Log.i(DataProvider.this, "Process data: %1$s. Request %2$s", dataObject, request.getType());

			switch (request.getType()) {

				case Login:
					//DataProvider.this.userData = (UserData) dataObject;
					break;
			default:
				break;
			}
		}

		@Override
		public void finishRequest(DataTask task, ResponseData responseData) {
			DataError error;

			tasks.remove(task);

			error = responseData.getError();
			if (error != null) {
				Log.e(DataProvider.this, error.getMessage());
			}

			Log.i(DataProvider.this, "Finish: %1$s", task);
			Log.i(DataProvider.this, "Response: %1$s", responseData);
			Log.i(DataProvider.this, "Ignored: %1$s", task.isIgnored());

			invokeCallbackback(task, responseData);

			Log.i(DataProvider.this, "Tasks left: %1$s", tasks.size());
		}

		protected void invokeCallbackback(DataTask task, ResponseData responseData) {
			
			if (!task.isIgnored()) {
				if (callback != null) {
					if (responseData.getError() != null) {
						callback.onDataProviderError(responseData.getRequest().getType(), responseData.getError());
					} else {
						callback.onDataReceived(responseData.getRequest().getType(), responseData.getDataObject());
					}
				}
			} else {
				Log.i(DataProvider.this, "Task ignored: %1$s", task);
			}
		}

		private boolean shouldSendError(WebServiceRequest request, DataError error) {
			return false;
		}

	}
}
