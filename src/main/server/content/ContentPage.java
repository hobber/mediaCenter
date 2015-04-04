package main.server.content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentPage {

	private JSONObject page = new JSONObject();	
	private JSONArray content = new JSONArray();
	
	public ContentPage() {	
		try {
			page.put("content", content);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}	
	
	public void addContentGroup(ContentGroup group) {				
		content.put(group);		
	}
	
	public void merge(ContentPage page) {
		try {
			for(int i=0; i<page.content.length(); i++)
				content.put(page.content.get(i));
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void setOptions(JSONObject options) {
		try {
			page.put("options", options);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public JSONObject getPage() {
		return page;
	}
}
