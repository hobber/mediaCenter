package main.server.content;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentOptions extends JSONObject {
	public ContentOptions() {
		try {
			put("type", "options");
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}	
	}
}
