package main.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import main.utils.JSONContainer;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPResponse {

	private  boolean isValid = false;
	private JSONObject response;
	private String htmlBody = "";
	
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
			InputStream inputstream = response.getEntity().getContent();
			Scanner scanner = new java.util.Scanner(inputstream);
			Scanner s = scanner.useDelimiter("\\A");
	    htmlBody =  s.hasNext() ? s.next() : "";
	    scanner.close();
	    this.response = new JSONObject(htmlBody);
	    isValid = true;
		} catch(IOException | IllegalStateException | JSONException e) {			
		}
		
 		try {
			this.response = new JSONObject(EntityUtils.toString(response.getEntity()));
			isValid = true;
		} catch(IOException | JSONException e) {			
		}
	}
	
	public boolean hasHTMLBody() {
		return htmlBody.length() > 0;
	}
	
	public String getHTMLBody() {
		return htmlBody;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public JSONContainer getJSONBody() {
		return new JSONContainer(response);
	}
	
	
	
	
	//TODO: remove code below!!
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
	
	public float getResponseFloat(String key, float defaultValue) {
		try {
			Object value = response.get(key);
			if(value == null)
				return defaultValue;
			return Float.parseFloat(value.toString());		
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public double getResponseDouble(String key, double defaultValue) {
		try {
			Object value = response.get(key);
			if(value == null)
				return defaultValue;
			return Double.parseDouble(value.toString());		
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
