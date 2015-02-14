package main.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPResponse {

	private  boolean isValid = false;
	private JSONObject response;
	
	public HTTPResponse(JSONObject body, boolean isValid) {
		this.isValid = isValid;
		response = body;
	}
	
	public HTTPResponse(HttpResponse response) {

		int responseCode = response.getStatusLine().getStatusCode(); 
		if(responseCode != 200)
		{
			System.err.println("Error: sending request failed (" + responseCode +
					" - " + response.getStatusLine().getReasonPhrase() + ")!");
			return;
		}		
		
		try {
			this.response = new JSONObject(EntityUtils.toString(response.getEntity()));
			isValid = true;
		} catch(IOException | JSONException e) {
			System.err.println("ERROR: Could not read response!");
			return;
		}
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public String getResponseString(String key) {
		try {
			Object value = response.get(key);
			if(value == null)
				return "";
			return value.toString();				
		} catch (JSONException e) {
			return "";
		}
	}
	
	public String getResponseSubString(String key) {
		try {
			String []keys = key.split("\\.");
			Object object = response.get(keys[0]);
			for(int i=1; i<keys.length && object != null; i++)
				object = ((JSONObject)object).get(keys[i]);			
			if(object == null)
				return "";			
			return object.toString();				
		} catch (JSONException e) {
			return "";
		}
	}
	
	//TODO: use getSubstring to get value before parsing
	public boolean getResponseBoolean(String key, boolean defaultValue) {
		try {
			Object value = response.get(key);
			if(value == null)
				return defaultValue;
			return Boolean.parseBoolean(value.toString());		
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public int getResponseInt(String key, int defaultValue) {
		try {
			Object value = response.get(key);
			if(value == null)
				return defaultValue;
			return Integer.parseInt(value.toString());		
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public JSONArray getJSONArray(String key) {
		try {
			return response.getJSONArray(key);
		} catch(JSONException e) {
			return new JSONArray();
		}
	}
	
	@Override
	public String toString() {
		return response.toString();
	}
}
