package com.barelabor.barelabor.data;

import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.ServiceError;

public class ResponseData {
	private long cacheTime;
	private DataObject dataObject;
	private DataError error;
	private WebServiceRequest request;

	public ResponseData(WebServiceRequest request, DataError dataError) {
		this.request = request;
		this.error = dataError;
	}

	public ResponseData(WebServiceRequest request, DataObject dataObject) {
		this.request = request;

		if (ServiceError.class.isInstance(dataObject)) {
			if (((ServiceError) dataObject).getMessage().equals("invalid session"))
				this.error = new DataError(DataError.ErrorType.InvalidSession);
			return;
		}

		this.dataObject = dataObject;
	}

	public long getCacheTime() {
		return this.cacheTime;
	}

	public DataObject getDataObject() {
		return this.dataObject;
	}

	public DataError getError() {
		return this.error;
	}

	public WebServiceRequest getRequest() {
		return this.request;
	}

	public String toString() {
		String result;
		
		if (getError() != null) {
			result = String.format("%1$s: %2$s", request.getType(), error);
		} else {
			result = String.format("%1$s: %2$s", request.getType(), dataObject);
		}
		
		return result;
	}
}
