package main.server.content;

import org.json.JSONException;

public class ContentText extends ContentItem {

	public ContentText(int x, int y, String text) {
		try {
			put("type", "text");
			put("x", x);
			put("y", y);		
			put("text", text);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public ContentText(int x, int y, int fontSize, int fontWeight, String text) {
		try {
			put("type", "text");
			put("x", x);
			put("y", y);
			put("style", "font-size: " + fontSize + "px; font-weight: " + fontWeight + ";");			
			put("text", text);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

}
