package main.server.content;

import org.json.JSONException;

public class ContentBackButton extends ContentItem {	
	
	public ContentBackButton(int x) {
		try {
		  data.put("type", "backButton");
		  data.put("x", x);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
