package main.spotify.datastructure;

import main.spotify.SpotifyAPIRequest;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class SpotifyElement { 
	
	protected boolean isValid = false;
	protected String spotifyUri;	
	protected SpotifyAPIRequest request;
	
	public String getSpotifyId() {
		return spotifyUri;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	protected void createAPIRequest(String path) {
		request = new SpotifyAPIRequest(path);
	}
	
	protected String getResponseString(JSONObject response, String key) {
		try {
			Object value = response.get(key);
			if(value == null)
				return "";
			return value.toString();				
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	protected String getResponseSubString(JSONObject response, String key) {
		try {
			String []keys = key.split("\\.");
			Object object = response.get(keys[0]);
			for(int i=1; i<keys.length && object != null; i++)
				object = ((JSONObject)object).get(keys[i]);			
			if(object == null)
				return "";			
			return object.toString();				
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	protected boolean getResponseBoolean(JSONObject response, String key, boolean defaultValue) {
		try {
			Object value = response.get(key);
			if(value == null)
				return defaultValue;
			return Boolean.parseBoolean(value.toString());		
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}
	
	protected int getResponseInt(JSONObject response, String key, int defaultValue) {
		try {
			Object value = response.get(key);
			if(value == null)
				return defaultValue;
			return Integer.parseInt(value.toString());		
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}
}
