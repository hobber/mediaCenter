package main.server.content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentPage extends JSONObject {

	private JSONArray items = new JSONArray();
	
	public ContentPage() {	
		try {			
			put("items", items);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}	
	
	public void addContentGroup(ContentGroup group) {				
		items.put(group);
	}
	
	public void merge(ContentPage page) {
		try {
			for(int i=0; i<page.items.length(); i++)
				items.put(page.items.get(i));
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void setOptions(ContentOptions options) {
		try {
			put("options", options);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void setMenu(ContentMenu menu) {
		try {
			put("menu", menu);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
