package main.server.menu;

import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;

import main.plugins.Plugin;
import main.server.content.ContentErrorPage;
import main.server.content.ContentPage;
import main.utils.Logger;

public class ContentMenuEntry {
  
  private Plugin plugin;
  private String iconPath;
  private HashMap<String, ContentMenuSubEntry> subEntryMap = new HashMap<String, ContentMenuSubEntry>();  
  private LinkedList<ContentMenuSubEntry> subEntryList = new LinkedList<ContentMenuSubEntry>();
  
  public ContentMenuEntry(Plugin plugin, String iconPath) {
    this.plugin = plugin;
    this.iconPath = iconPath;
  }
  
  public void addSubMenuEntry(ContentMenuSubEntry entry) {
    if(subEntryMap.containsKey(entry.getName())) {
      Logger.error("Plugin " + plugin.getName() + " already contains a page with name " + entry.getName());
      return;
    }
    subEntryMap.put(entry.getName(), entry);
    subEntryList.add(entry);
  }
  
  public String getName() {
    return plugin.getName();
  } 
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("icon", iconPath);
    for(ContentMenuSubEntry subEntry : subEntryList)
      container.append("subentries", subEntry.toJSON());    
    return container;
  }
  
  public ContentPage handleAPIRequest(String pageName, String parameter) {
    ContentMenuSubEntry entry = subEntryMap.get(pageName);
    if(entry == null) {
      Logger.error("invalid page name " + pageName + " for plugin " + plugin.getName());
      return new ContentErrorPage("invalid page name " + pageName + " for plugin " + plugin.getName());
    }
    return entry.handleAPIRequest(parameter);
  }
}
