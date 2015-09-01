package main.plugins.creator;

import java.io.IOException;

import main.plugins.Plugin;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;

public class CreatorPlugin implements Plugin {

  private String databaseFileName;
  private ContentMenuEntry menuEntry;
  private CreatorContentPageCreate createPage;
  
  public CreatorPlugin(ConfigElementGroup config) {
    databaseFileName = config.getString("file", null);
    
    if(databaseFileName == null)
      throw new RuntimeException("Please store your creator database file name in the config file");
    
    try {
      FileReader file = new FileReader(databaseFileName);
      createPage = new CreatorContentPageCreate(getName(), file);
    } catch(IOException e) {
      Logger.error(e);
      createPage = new CreatorContentPageCreate(getName());
    }
    
    menuEntry = new ContentMenuEntry(this, Plugin.ICON_PATH + "creator.svg");
    menuEntry.addSubMenuEntry(createPage);
  }
  
  @Override
  public String getName() {
    return "Creator";
  }

  @Override
  public void update() {
    
  }

  @Override
  public void saveState() {
    try {
      FileWriter file = new FileWriter(databaseFileName);
      createPage.saveState(file);
    } catch(IOException e) {
      Logger.error(e);
    }
  }

  @Override
  public ContentMenuEntry getMenuEntry() {
    return menuEntry;
  }

}
