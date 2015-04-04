package main.server.content;

import org.json.JSONArray;

public class ContentMenu extends JSONArray {

	public void addContentObject(ContentObject object) {
		put(object);
	}
}
