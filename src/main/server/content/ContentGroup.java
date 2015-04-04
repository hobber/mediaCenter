package main.server.content;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentGroup extends JSONObject {	
	
	public ContentGroup() {
		try {
			put("type", "group");
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
