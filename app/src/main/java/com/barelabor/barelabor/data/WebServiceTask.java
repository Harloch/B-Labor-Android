package com.barelabor.barelabor.data;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.barelabor.barelabor.data.WebServiceRequest.HTTPMethod;
import com.barelabor.barelabor.data.DataError.ErrorType;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.ServiceError;
import com.barelabor.barelabor.debug.Debug;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.util.NetworkMonitor;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceTask extends DataTask {

	private JsonDataHandler jsonDataHandler;
	private NetworkMonitor networkMonitor;

	public WebServiceTask(IDataTaskCallback callback, NetworkMonitor networkMonitor) {
		super(callback);

		this.jsonDataHandler = new JsonDataHandler();
		this.networkMonitor = networkMonitor;
	}
	

	@Override
	protected ResponseData runTask(WebServiceRequest request) {
		ResponseData responseData = null;

		if (this.networkMonitor.isConnected()) {
			responseData = download(request);
		} else {
			responseData = new ResponseData(request, new DataError(ErrorType.NoNetwork));
		}

		return responseData;
	}

	private ResponseData download(WebServiceRequest request) {
		ResponseData responseData;

		try {
			HttpUriRequest httpRequest;
			HttpResponse httpResponse;
			int code;
			
			Log.i(this, "Send: %1$s", request);

			httpRequest = buildUriRequest(request);
			
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			code = httpResponse.getStatusLine().getStatusCode();
			
			Log.i(this, "%1$d <= %2$s", code, httpRequest.getURI());
			if (code == 200 || code == 201) {
				String response;
				InputStream inputStream;

				inputStream = httpResponse.getEntity().getContent();

				try {
					DataObject dataObject;
					
					response = new String(IOTools.readAll(inputStream), "UTF-8");
					Log.v(this, "%1$s", response);
					
					dataObject = jsonDataHandler.parse(request, response);

					if(dataObject == null){
						responseData = new ResponseData(request, new DataError(ErrorType.NetworkError, "Null error"));
					}else if (dataObject.getClass().equals(ServiceError.class)) {
						ServiceError dataError;
						DataError error;
						
						dataError = (ServiceError) dataObject;
						if (dataError.isInvalidSession()) {
							error = new DataError(ErrorType.InvalidSession);
						} else {
							error = new DataError(ErrorType.ServiceError, dataError.getMessage());
						}
						responseData = new ResponseData(request, error);
					} else {

						if (callback != null) {
							callback.processData(request, dataObject);
						}
						
						responseData = new ResponseData(request, dataObject);
					}
				} catch (DataFormatException e) {
					responseData = new ResponseData(request, new DataError(ErrorType.NetworkError, e.getMessage()));
				} finally {
					inputStream.close();
				}
				
			} else {
				responseData = new ResponseData(request, new DataError(ErrorType.NetworkError, String.format("HTTP error: %1$s", code)));
			}

			if (Debug.isOn) {
				if (Debug.fail()) {
					throw new IOException("Debug failing");
				} else {
					Debug.delayRequest();
				}
			}
		} catch (ClientProtocolException e) {
			responseData = new ResponseData(request, new DataError(ErrorType.NetworkError, e.getMessage()));
		} catch (IOException e) {
			responseData = new ResponseData(request, new DataError(ErrorType.NetworkError, e.getMessage()));
		}

		return responseData;
	}

	private HttpUriRequest buildUriRequest(WebServiceRequest request) throws UnsupportedEncodingException {
		HttpUriRequest httpRequest;


		if (request.getHttpMethod() == HTTPMethod.Get || request.sendParamsInURL()) {
			httpRequest = new HttpGet(request.toURLString(true));
		} else {
			HttpPost httpPost;

			httpPost = new HttpPost(request.toURLString(false));

			if (request.getType() == WebServiceRequestType.SubmitEstimate) {

				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				String userId = request.getParameter(WebServiceRequest.PARAM_USERID);
				reqEntity.addPart(WebServiceRequest.PARAM_USERID, new StringBody(userId));

				Bitmap bitmap = request.getBitmap();
				if (bitmap != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
					byte[] data = bos.toByteArray();
					String imageName = System.currentTimeMillis() + ".jpg";
					ByteArrayBody bab = new ByteArrayBody(data, imageName);
					reqEntity.addPart(WebServiceRequest.PARAM_ESTIMATEIMAGE, bab);
				}

				httpPost.setEntity(reqEntity);

			}else{

				JSONObject parameters;
				StringEntity entity;
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "application/json; charset=utf-8");

				parameters = new JSONObject();

				try {
					//parameters.add(new BasicNameValuePair(WebServiceRequest.PARAM_METHOD, request.getMethod()));
					for (String param : request.getParameterNames()) {
						parameters.put(param, request.getParameter(param));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				entity = new StringEntity(parameters.toString(), HTTP.UTF_8);
				httpPost.setEntity(entity);
			}

			httpRequest = httpPost;

		}

		return httpRequest;
	}
}
