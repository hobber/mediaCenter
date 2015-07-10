package main.plugins;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import main.server.content.ContentErrorPage;
import main.server.content.ContentItem;
import main.server.menu.ContentMenuEntry;

public class PluginController {

	private static HashMap<String, Plugin> plugins = new HashMap<String, Plugin>();
	private static HashMap<String, ContentMenuEntry> menuMap = new HashMap<String, ContentMenuEntry>();
	private static LinkedList<ContentMenuEntry> menuList = new LinkedList<ContentMenuEntry>();
	
	private PluginController() {		
	}
	
	public static void register(Plugin plugin) {
	  String name = plugin.getName();
		if(plugins.containsKey(name))
			throw new RuntimeException("plugin " + name + " was registered more than once");
		plugins.put(name, plugin);
		
		ContentMenuEntry entry = plugin.getMenuEntry();
		menuMap.put(entry.getName(), entry);
		menuList.add(entry);
	}
	
	public static List<ContentMenuEntry> getMenuEntries() {
	  return menuList;
	}
	
	public static ContentItem handleAPIRequest(String pluginName, String pageName, String parameter) {
	  ContentMenuEntry entry = menuMap.get(pluginName);
	  if(entry == null)
	    return new ContentErrorPage("unknown plugin " + pluginName);
	  return entry.handleAPIRequest(pageName, parameter);
	}
	
	public static void update() {
	  for(Plugin plugin : plugins.values())
      plugin.update();
	}
	
	public static void shutdown() {
	  for(Plugin plugin : plugins.values())
	    plugin.saveState();
	}
}
