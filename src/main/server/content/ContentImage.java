package main.server.content;

import org.json.JSONException;

public class ContentImage extends ContentItem {

	public ContentImage(int x, int y, int width, int height, String src) {
		try {
			put("type", "img");
			put("x", x);
			put("y", y);
			put("width", width);
			put("height", height);
			put("src", src);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
