package main.server.content;

import org.json.JSONException;

public class ContentImage extends ContentItem {

	public ContentImage(int x, int y, int width, int height, String src) {
		try {
			this.put("type", "img");
			this.put("x", x);
			this.put("y", y);
			this.put("width", width);
			this.put("height", height);
			this.put("src", src);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
