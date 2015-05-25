package main.server.content;

import org.json.JSONException;

public class ContentSearchField extends ContentItem {	
	
	public ContentSearchField(String context, int x) {
		try {
		  data.put("type", "searchField");
		  data.put("context", context);
		  data.put("x", x);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

}
