package main.server.menu;

import java.util.LinkedList;

import org.json.JSONObject;

import main.plugins.Plugin;
import main.server.content.ContentErrorPage;
import main.server.content.ContentPage;
import main.utils.Logger;

public class ContentMenuEntry {
  
  private Plugin plugin;
  private String iconPath;
  private LinkedList<ContentMenuSubEntry> subEntries = new LinkedList<ContentMenuSubEntry>();
  private int id;
  
  public ContentMenuEntry(Plugin plugin, String iconPath, int id) {
    this.plugin = plugin;
    this.iconPath = iconPath;
    this.id = id;
  }
  
  public void addSubMenuEntry(ContentMenuSubEntry entry) {
    entry.setIds(id, subEntries.size());
    subEntries.add(entry);
  }
  
  public String getName() {
    return plugin.getName();
  } 
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("id", id);
    container.put("icon", iconPath);
    for(ContentMenuSubEntry subEntry : subEntries)
      container.append("subentries", subEntry.toJSON());    
    return container;
  }
  
  public ContentPage handleAPIRequest(int subId, String parameter) {
    if(subId < 0 || subId >= subEntries.size()) {
      Logger.error("invalid subID " + subId + " for plug in " + getName());
      return new ContentErrorPage("invalid subID " + subId + " for plug in " + getName());
    }
    return subEntries.get(subId).handleAPIRequest(parameter);
  }
}
