package main.server.menu;

import java.util.Map;

import main.server.content.ContentItem;

import org.json.JSONObject;

public abstract class ContentMenuSubEntry {

  private String name;
  private int id;
  private int subId;
  
  public ContentMenuSubEntry(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("name", name);
    return container;
  }
  
  public void setIds(int id, int subId) {
   this.id = id;
   this.subId = subId;
  }
  
  public abstract ContentItem handleAPIRequest(Map<String, String> parameters);
}
