package main.server.menu;

import org.json.JSONObject;

import main.plugins.Plugin;
import main.server.content.ContentPage;

public abstract class ContentMenuSubEntry {

  private String name;
  
  public ContentMenuSubEntry(String name) {
    this.name = name;
  }
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("name", name);
    return container;
  }
  
  public abstract ContentPage handleAPIRequest(String parameter);
}
