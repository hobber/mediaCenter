package main.server.content;

import org.json.JSONException;

public class ContentText  extends ContentItem {

	public ContentText(int x, int y, String text) {
		try {
			this.put("type", "text");
			this.put("x", x);
			this.put("y", y);		
			this.put("text", text);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public ContentText(int x, int y, int fontSize, int fontWeight, String text) {
		try {
			this.put("type", "text");
			this.put("x", x);
			this.put("y", y);
			this.put("style", "font-size: " + fontSize + "px; font-weight: " + fontWeight + ";");			
			this.put("text", text);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

}
