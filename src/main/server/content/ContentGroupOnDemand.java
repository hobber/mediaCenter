package main.server.content;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentGroupOnDemand extends JSONObject {

	public ContentGroupOnDemand(String context, String query) {		
		try {
			put("type", "loadOnDemand");
			put("context", context);
			put("query", query);				
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
