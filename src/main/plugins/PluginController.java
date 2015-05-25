package main.plugins;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import main.server.content.ContentErrorPage;
import main.server.content.ContentPage;
import main.server.menu.ContentMenuEntry;

public class PluginController {

	private static HashMap<String, Plugin> plugins = new HashMap<String, Plugin>();
	private static HashMap<Integer, ContentMenuEntry> menuMap = new HashMap<Integer, ContentMenuEntry>();
	private static LinkedList<ContentMenuEntry> menuList = new LinkedList<ContentMenuEntry>();
	private static int MenuIdCounter = 0;
	
	private PluginController() {		
	}
	
	public static void register(Plugin plugin) {
	  String name = plugin.getName();
		if(plugins.containsKey(name))
			throw new RuntimeException("plugin " + name + " was registered more than once");
		plugins.put(name, plugin);
		
		int id = MenuIdCounter++;
		ContentMenuEntry entry = plugin.getMenuEntry();
		entry.setId(id);
		menuMap.put(id, entry);
		menuList.add(entry);
	}
	
	public static List<ContentMenuEntry> getMenuEntries() {
	  return menuList;
	}
	
	public static ContentPage handleAPIRequest(int id, int subId, String parameter) {
	  ContentMenuEntry entry = menuMap.get(id);
	  if(entry == null)
	    return new ContentErrorPage("invalid id " + id);
	  return entry.handleAPIRequest(subId, parameter);
	}
}
