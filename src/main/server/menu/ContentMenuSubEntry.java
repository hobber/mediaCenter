package main.server.menu;

import main.server.RequestParameters;
import main.server.content.ContentItem;

import org.json.JSONObject;

public abstract class ContentMenuSubEntry {

  private String name;
  
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
  
  public abstract ContentItem handleAPIRequest(RequestParameters parameters);
}
