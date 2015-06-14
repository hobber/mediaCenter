package main.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONContainer {

	private JSONObject body;

	public JSONContainer(JSONObject object) {
		body = object;
	}

	private Object getObject(String key) {
		try {
			String []keys = key.split("\\.");
			Object object = body.get(keys[0]);
			for(int i=1; i<keys.length && object != null; i++)
				object = ((JSONObject)object).get(keys[i]);			
			return object;				
		} catch (JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
			return null;
		}
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		Object value = getObject(key);
		if(Boolean.class.isInstance(value))
			return (Boolean)value;
		return defaultValue;						
	}

	public Short getShort(String key, Short defaultValue) {
		Object value = getObject(key);		
		if(Short.class.isInstance(value))
			return (Short)value;
		return defaultValue;					
	}
	
	public Integer getInt(String key, Integer defaultValue) {
		Object value = getObject(key);		
		if(Integer.class.isInstance(value))
			return (Integer)value;
		return defaultValue;					
	}

	public Float getFloat(String key, Float defaultValue) {	
		Object value = getObject(key);
		if(value == null)
			return defaultValue;
		return Float.parseFloat(value.toString());					
	}

	public Double getDouble(String key, Double defaultValue) {
		Object value = getObject(key);
		if(value == null)
			return defaultValue;
		return Double.parseDouble(value.toString());					
	}
	
	public String getString(String key, String defaultValue) {
		Object value = getObject(key);
		if(String.class.isInstance(value))
			return (String)value;
		return defaultValue;			
	}

	public JSONContainer getSubContainer(String key) {
		Object value = getObject(key);
		if(JSONObject.class.isInstance(value))
			return new JSONContainer((JSONObject)value);
		return new JSONContainer(new JSONObject());
	}
	
	public JSONArray getArray(String key) {
		Object value = getObject(key);
		if(org.json.JSONArray.class.isInstance(value))
			return new JSONArray((org.json.JSONArray)value);
		return new JSONArray(new org.json.JSONArray());
	}
	
	public boolean containsObject(String key) {
	  String []keys = key.split("\\.");
	  Object object = body.opt(keys[0]);
	  if(object == null)
	    return false;
	  for(int i=1; i<keys.length && object != null; i++) {
	    object = ((JSONObject)object).opt(keys[i]);
	    if(object == null)
	      return false;
	  }
	  return true;        
	}
	
	public String toString() {
		return body.toString();
	}
}
