package main.server.content;

import org.json.JSONException;

public class ContentGroup extends ContentItem {	
	
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
}
