package com.barelabor.barelabor.data;

public class DataError {
	public static final DataError NO_NETWORK = new DataError(ErrorType.NoNetwork);

	private String message;
	private ErrorType type;

	public static enum ErrorType {
		NoNetwork,
		NetworkError,
		ServiceError,
		InvalidSession,
		Cache,
		UnstableConnection
	}

	public DataError(ErrorType errorType, String message) {
		this.type = errorType;
		this.message = message;
	}

	public DataError(ErrorType errorType) {
		this(errorType, errorType.name());
	}

	public String getMessage() {
		return this.message;
	}

	public ErrorType getType() {
		return this.type;
	}

	public String toString() {
		return String.format("%1$s: %2$s", this.type, this.message);
	}

}
