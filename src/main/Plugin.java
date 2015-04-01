package main;

public abstract class Plugin {

	private String pluginName;
	
	protected Plugin(String pluginName) {
		this.pluginName = pluginName;
		PluginController.register(this);
	}
	
	public String getName() {
		return pluginName;
	}
	
	public abstract void start();
}
