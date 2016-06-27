package com.barelabor.barelabor.data.model;

import java.io.Serializable;

public class DataObject implements Serializable
{
	private String status;
	private String data;

	public DataObject() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
