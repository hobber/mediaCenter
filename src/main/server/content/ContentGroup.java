package main.server.content;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentGroup extends JSONObject {	
	
	public ContentGroup() {
		try {
			append("style", "height: 100%;");			
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}		
	}
	
	public ContentGroup(int height) {
		try {
			append("style", "height: " + height + "px; border-bottom: 1px solid #000000;");			
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}		
	}
	
	public void put(ContentItem item) {		
		try {
			append("items", item);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}	
	}
	
	public void appendSubGroup(ContentGroup group) {
		try {
			append("subgroup", group);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void putContentGroupOnDemand(ContentGroupOnDemand group) {
		try {
			put("subgroup", group);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
