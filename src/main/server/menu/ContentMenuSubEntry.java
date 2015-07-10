package main.server.menu;

import main.server.content.ContentItem;
import main.server.content.ContentOnClick;

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
  
  public ContentOnClick getContentOnClickElement(String parameter) {
    return new ContentOnClick(id, subId, parameter);
  }
  
  public void setIds(int id, int subId) {
   this.id = id;
   this.subId = subId;
  }
  
  public abstract ContentItem handleAPIRequest(String parameter);
}
