package main;

import java.util.HashMap;

public class PluginController {

	private static HashMap<String, Plugin> plugins = new HashMap<String, Plugin>();
	
	private PluginController() {		
	}
	
	public static void register(Plugin plugin) {
		if(plugins.containsKey(plugin.getName()))
			throw new RuntimeException("plugin " + plugin.getName() + " was started more than once");
		plugins.put(plugin.getName(), plugin);
	}
	
	public static void startPlugins() {
		for(String name : plugins.keySet())
			plugins.get(name).start();
	}
}
