package main.server.menu;

import org.json.JSONObject;

import main.plugins.Plugin;
import main.server.content.ContentOnClick;
import main.server.content.ContentPage;

public abstract class ContentMenuSubEntry {

  private String name;
  private int id;
  private int subId;
  
  public ContentMenuSubEntry(String name) {
    this.name = name;
  }
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("name", name);
    return container;
  }
  
  public ContentOnClick getContentOnClickElement(String parameter) {
    return new ContentOnClick(id, subId, parameter);
  }
  
  public void setIds(int id, int subId) {
   this.id = id;
   this.subId = subId;
  }
  
  public abstract ContentPage handleAPIRequest(String parameter);
}
