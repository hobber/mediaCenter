package main.server.content;

import org.json.JSONArray;

public class ContentMenu extends JSONArray {

	public void addContentObject(ContentObject object) {
		put(object);
	}
	
	public static ContentMenu createBackMenu() {
		ContentMenu menu = new ContentMenu();		
		menu.put(new ContentBackButton(8));
		return menu;
	}
	
	public static ContentMenu createSearchMenu(String context) {
		ContentMenu menu = new ContentMenu();		
		menu.put(new ContentSearchField(context, 4));
		return menu;
	}
}
