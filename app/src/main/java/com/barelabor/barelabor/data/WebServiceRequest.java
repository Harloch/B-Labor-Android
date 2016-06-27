package com.barelabor.barelabor.data;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import com.barelabor.barelabor.util.NotImplementedException;

public class WebServiceRequest implements Serializable{

	public static final String DPI_H = "h";
	public static final String DPI_M = "m";
	public static final String DPI_XH = "xh";
	public static final String DPI_XXH = "xxh";
	public static final String VARNAME_ALL = "all";

	public static final String METHOD_LOGIN = "login";
	public static final String METHOD_REGISTER = "register";

	public static final String METHOD_GETMAKE = "getMake";
	public static final String METHOD_GETMODEL = "getModel";
	public static final String METHOD_GETFEATURES = "getFeatures";

	public static final String METHOD_GETPRICEBYVEHICLE = "getPriceByVehicle";
	public static final String METHOD_GETPRICEBYSIZE = "getPriceBySize";

	public static final String METHOD_SUBMITEXPERIENCE = "submitExperience";
	public static final String METHOD_SUBMITESTIMATE = "submitEstimate";
	public static final String METHOD_GETESTIMATE = "getEstimates";

	public static final String METHOD_FINDNEARSHOP = "findNearShop";

	public static final String PARAM_METHOD = "method";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_DEVICETOKEN = "device_token";

	public static final String PARAM_YEAR = "year";
	public static final String PARAM_MAKE = "make";
	public static final String PARAM_MODEL = "model";
	public static final String PARAM_FEATURE = "feature";

	public static final String PARAM_WIDTH = "width";
	public static final String PARAM_RATIO = "ratio";
	public static final String PARAM_DIAMETER = "diameter";

	public static final String PARAM_TYPE = "type";
	public static final String PARAM_ANSWERS = "answers";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_SHOPNAME = "shop_name";
	public static final String PARAM_COMMENTS = "comments";

	public static final String PARAM_USERID = "userID";
	public static final String PARAM_ESTIMATEIMAGE = "estimateImage";
	public static final String PARAM_ESTIMATEID = "estimateID";

	public static final String PARAM_LAT = "lat";
	public static final String PARAM_LNG = "lng";

	private static final String SERVICE_API = "barelabor.com/barelabor/api/index.php";


	private HTTPMethod httpMethod;
	private HashMap<String, String> parameters;
	private Bitmap bitmap;
	private boolean sendParamsInURL;
	private WebServiceRequestType type;
	private String method;

	public static enum HTTPMethod {
		Get, Post,
	}

	public WebServiceRequest(HTTPMethod httpMethod, String method) {
		this.httpMethod = httpMethod;
		this.method = method;
		this.parameters = new HashMap<String, String>();
		this.type = determineRequestType(method);
	}

	public HTTPMethod getHttpMethod() {
		return httpMethod;
	}

	public String getMethod() {
		return method;
	}

	public WebServiceRequestType getType() {
		return type;
	}

	public void setSendParamsInURL(boolean sendParamsInURL) {
		this.sendParamsInURL = sendParamsInURL;
	}

	public boolean sendParamsInURL() {
		return sendParamsInURL;
	}

	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public String setParameter(String name, String value) {
		return parameters.put(name, value);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String toURLString(boolean withQuery) throws UnsupportedEncodingException {
		StringBuilder builder;

		builder = new StringBuilder();
		builder.append("http://");

		builder.append(SERVICE_API);

		// add method
		builder.append("?");
		builder.append(PARAM_METHOD);
		builder.append("=");
		builder.append(getMethod());

		if (withQuery) {

			for (String parameter : getParameterNames()) {
				builder.append("&");
				builder.append(parameter);
				if (getParameter(parameter) != null) {
					builder.append("=");
					builder.append(URLEncoder.encode(getParameter(parameter), "UTF-8"));
				}
			}
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return String.format("%1$s: %2$s", getMethod(), parameters);
	}

	private static WebServiceRequestType determineRequestType(String method) {
		WebServiceRequestType type;

		if (method.equals(METHOD_LOGIN)) {
			type = WebServiceRequestType.Login;
		}else if (method.equals(METHOD_REGISTER)) {
			type = WebServiceRequestType.Register;
		}else if (method.equals(METHOD_GETMAKE)) {
			type = WebServiceRequestType.GetMake;
		}else if (method.equals(METHOD_GETMODEL)) {
			type = WebServiceRequestType.GetModel;
		}else if (method.equals(METHOD_GETFEATURES)) {
			type = WebServiceRequestType.GetFeatures;
		}else if (method.equals(METHOD_GETPRICEBYVEHICLE)) {
			type = WebServiceRequestType.GetPriceByVehicle;
		}else if (method.equals(METHOD_GETPRICEBYSIZE)) {
			type = WebServiceRequestType.GetPriceBySize;
		}else if (method.equals(METHOD_FINDNEARSHOP)) {
			type = WebServiceRequestType.FindNearShop;
		}else if (method.equals(METHOD_SUBMITESTIMATE)) {
			type = WebServiceRequestType.SubmitEstimate;
		}else if (method.equals(METHOD_GETESTIMATE)) {
			type = WebServiceRequestType.GetEstimate;
		}else if (method.equals(METHOD_SUBMITEXPERIENCE)) {
			type = WebServiceRequestType.SubmitExperience;
		}else {
			throw new NotImplementedException();
		}

		return type;
	}
};
