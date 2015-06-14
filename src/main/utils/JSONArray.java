package main.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONArray {

	private org.json.JSONArray body;
	
	public JSONArray() {
	  body = new org.json.JSONArray();
	}
	
	public JSONArray(org.json.JSONArray array) {
		body = array;
	}
	
	public int length() {
		return body.length();
	}
	
	public Boolean getBoolean(int index, Boolean defaultValue) {
		try {
			return body.getBoolean(index);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return defaultValue;
		}						
	}
	
	public Short getShort(int index, Short defaultValue) {
		try {
			return (short)body.getInt(index);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return defaultValue;
		}				
	}

	public Integer getInt(int index, Integer defaultValue) {
		try {
			return body.getInt(index);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return defaultValue;
		}				
	}

	public Float getFloat(int index, Float defaultValue) {		
		try {
			return (float)body.getDouble(index);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return defaultValue;
		}						
	}

	public Double getDouble(int index, Double defaultValue) {
		try {
			return body.getDouble(index);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return defaultValue;
		}				
	}
	
	public String getString(int index, String defaultValue) {
		try {
			return body.getString(index);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return defaultValue;
		}				
	}

	public JSONContainer getContainer(int index) {
		try {
			return new JSONContainer(body.getJSONObject(index));
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return new JSONContainer(new JSONObject());
		}	
	}
	
	public JSONArray getArray(int index) {
		try {
			return new JSONArray(body.getJSONArray(index));
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return new JSONArray(new org.json.JSONArray());
		}
	}
	
	public String toString() {
		return body.toString();
	}
}
