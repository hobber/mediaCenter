package main.server.content;

import org.json.JSONException;

public class ContentImage extends ContentItem {

	public ContentImage(int x, int y, int width, int height, String src) {
		try {
		  data.put("type", "img");
		  data.put("x", x);
		  data.put("y", y);
		  data.put("width", width);
		  data.put("height", height);
		  data.put("src", src);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
