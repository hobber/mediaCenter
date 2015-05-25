package main.server.content;

import org.json.JSONException;

public class ContentGroupOnDemand extends ContentItem {

	public ContentGroupOnDemand(String context, String query) {		
		try {
			data.put("type", "loadOnDemand");
			data.put("context", context);
			data.put("query", query);				
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
