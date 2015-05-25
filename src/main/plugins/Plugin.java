package main.plugins;

import main.server.menu.ContentMenuEntry;

public interface Plugin {

  public static final String ICON_PATH = "content/";
  
	public String getName();
	public void saveState();
	
	public ContentMenuEntry getMenuEntry();
}
