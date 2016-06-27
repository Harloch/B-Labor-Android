package com.barelabor.barelabor.data.model;

import java.util.HashMap;
import java.util.Map;

public class ValuesMap extends DataObject {
	private HashMap<String, String> secondaryValuesMap = new HashMap<String, String>();
	private HashMap<String, String> valuesMap = new HashMap<String, String>();

	public ValuesMap() {
	}

	public Map<String, String> getMap() {
		return this.valuesMap;
	}

	public HashMap<String, String> getSecondaryValuesMap() {
		return this.secondaryValuesMap;
	}

	public void put(String key, String value, String secondaryValue) {
		valuesMap.put(key, value);
		secondaryValuesMap.put(key, secondaryValue);
	}
}
