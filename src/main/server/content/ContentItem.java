package main.server.content;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ContentItem {
  
  protected JSONObject data = new JSONObject();
  
	public void appendLink(ContentGroup group) {
		try {
			data.put("link", group.data);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void appendLink(ContentOnClick onClick) {
		try {
			data.put("onClick", onClick.getParameter());
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public String getContentString() {
	  return data.toString();
	}
}
