package main.plugins.creator;

import main.plugins.Plugin;
import main.server.menu.ContentMenuEntry;

public class CreatorPlugin implements Plugin {

  private ContentMenuEntry menuEntry;
  private CreatorContentPageCreate createPage;
  
  public CreatorPlugin() {
    menuEntry = new ContentMenuEntry(this, Plugin.ICON_PATH + "creator.svg");
    createPage = new CreatorContentPageCreate(getName());
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
     
  }

  @Override
  public ContentMenuEntry getMenuEntry() {
    return menuEntry;
  }

}
