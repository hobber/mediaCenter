package main.server.menu;

import java.util.HashMap;
import java.util.LinkedList;

import main.plugins.Plugin;
import main.server.RequestParameters;
import main.server.content.ContentErrorPage;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.utils.Logger;

import org.json.JSONObject;

public class ContentMenuEntry {
  
  private String pluginName;
  private String iconPath;
  private HashMap<String, ContentMenuSubEntry> subEntryMap = new HashMap<String, ContentMenuSubEntry>();  
  private LinkedList<ContentMenuSubEntry> subEntryList = new LinkedList<ContentMenuSubEntry>();
  
  public ContentMenuEntry(Plugin plugin, String iconPath) {
    this.pluginName = plugin.getName();
    this.iconPath = iconPath;
  }
  
  public void addSubMenuEntry(ContentMenuSubEntry entry) {
    if(subEntryMap.containsKey(entry.getName())) {
      Logger.error("Plugin " + pluginName + " already contains a page with name " + entry.getName());
      return;
    }
    subEntryMap.put(entry.getName(), entry);
    subEntryList.add(entry);
  }
  
  public String getName() {
    return pluginName;
  } 
  
  public JSONObject toJSON() {
    JSONObject container = new JSONObject();
    container.put("icon", iconPath);
    for(ContentMenuSubEntry subEntry : subEntryList)
      container.append("subentries", subEntry.toJSON());    
    return container;
  }
  
  public ContentItem handleAPIRequest(RequestParameters parameters) {
    String pageName = parameters.get("page");
    ContentMenuSubEntry entry = subEntryMap.get(pageName);
    if(entry == null) {
      Logger.error("invalid page name " + pageName + " for plugin " + pluginName);
      return new ContentErrorPage(new ContentLocation(pluginName, pageName), "invalid page name " + pageName + " for plugin " + pluginName);
    }
    return entry.handleAPIRequest(parameters);
  }
}
