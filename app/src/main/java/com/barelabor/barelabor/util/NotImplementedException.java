package com.barelabor.barelabor.util;

public class NotImplementedException extends IllegalStateException {
	private static final long serialVersionUID = 1L;

	public NotImplementedException() {
		super("Not implemented");
	}
}
