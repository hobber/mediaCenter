package main.server.content;

import java.util.LinkedList;

import org.json.JSONException;

public class ContentGroup extends ContentItem {	
	
  private LinkedList<ContentItem> items = new LinkedList<ContentItem>();
  private ContentOptions options;
  
	public ContentGroup() {
		try {
		  data.put("type", "group");
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}	  
	}
	
	public void put(ContentItem item) {		
	  items.add(item);
	}
	
	public void setOptions(ContentOptions options) {
	  this.options = options;
	}
	
	@Override
	public String getContentString() {
	  String s = data.toString();
	  s = s.substring(0, s.length() - 1) + ", \"items\": [";
	  for(int i = 0; i< items.size(); i++)
	    s += (i > 0 ? ", " : "") + items.get(i).getContentString(); 
	  return s + "], \"options\": " + options + "}";
	}
}
