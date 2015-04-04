package main.server.content;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ContentItem extends JSONObject {
	public void appendLink(ContentGroup group) {
		try {
			put("link", group);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void appendLink(ContentGroupOnDemand group) {
		try {
			put("link", group);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
