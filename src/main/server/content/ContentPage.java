package main.server.content;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentPage {

	private LinkedList<ContentGroup> groups = new LinkedList<ContentGroup>();
	private ContentOptions options;
	private ContentMenu menu;
	
	public ContentPage() {	
	}	
	
	public void addContentGroup(ContentGroup group) {				
		groups.add(group);
	}
	
	public void merge(ContentPage page) {
	  for(int i=0; i<page.groups.size(); i++)
	    groups.add(page.groups.get(i));
	}
	
	public void setOptions(ContentOptions options) {
		this.options = options;
	}
	
	public void setMenu(ContentMenu menu) {
		this.menu = menu;
	}
	
	public String getContentString() {
	  String s = "{\"options\": " + options + ", \"menu\": " + menu + ", \"page\": [";
	  for(int i=0; i<groups.size(); i++)
	    s += (i > 0 ? ", " : "") + groups.get(i); 
	  return s + "]}";
	}
}
