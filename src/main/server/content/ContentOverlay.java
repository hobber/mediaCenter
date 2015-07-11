package main.server.content;

import java.util.LinkedList;

import org.json.JSONException;

public class ContentOverlay extends ContentItem {
  
  private LinkedList<ContentItem> items = new LinkedList<ContentItem>();
  
  public ContentOverlay(String caption, int width, int height) {
    try {
      data.put("type", "overlay");
      data.put("caption", caption);
      data.put("width", width);
      data.put("height", height);
    } catch(JSONException e) {
      System.err.println("ERROR: " + e.getMessage());
    }   
  }
  
  public void put(ContentItem item) {   
    items.add(item);
  }
  
  @Override
  public String getContentString() {
    String s = data.toString();
    s = s.substring(0, s.length() - 1) + ", \"items\": [";
    for(int i = 0; i< items.size(); i++)
      s += (i > 0 ? ", " : "") + items.get(i).getContentString(); 
    return s + "]}";
  }
}
