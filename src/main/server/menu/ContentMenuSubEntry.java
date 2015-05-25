package main.server.menu;

import org.json.JSONObject;

import main.plugins.Plugin;

public abstract class ContentMenuSubEntry {

  private Plugin plugin;
  private String name;
  
  public ContentMenuSubEntry(Plugin plugin, String name) {
    this.plugin = plugin;
    this.name = name;
  }
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("name", name);
    return container;
  }
  
  public abstract JSONObject handleAPIRequest(String parameter);
}
