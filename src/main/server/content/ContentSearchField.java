package main.server.content;

import org.json.JSONException;

public class ContentSearchField extends ContentItem {	
	
	public ContentSearchField(String context, int x) {
		try {
			put("type", "searchField");
			put("context", context);
			put("x", x);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

}
