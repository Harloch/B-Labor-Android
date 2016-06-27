package com.barelabor.barelabor.data.model;

import com.barelabor.barelabor.data.model.DataObject;

public class ServiceError extends DataObject {
	private static final Object INVALID_SESSION_MESSAGE = "invalid session";
	private String message;

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isInvalidSession() {
		return getMessage().equals(INVALID_SESSION_MESSAGE);
	}
}
