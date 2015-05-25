package main.server.content;

import org.json.JSONException;

public class ContentGroup extends ContentItem {	
	
	public ContentGroup() {
		try {
		  data.put("type", "group");
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}	  
	}
	
	public void put(ContentItem item) {		
		try {
		  data.append("items", item.data);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}	
	}
	
	@Override
	public String toString() {
	  return data.toString();
	}
}
