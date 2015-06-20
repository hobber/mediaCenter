package main.plugins.ebay;

import main.plugins.Plugin;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;

public class EbayPlugin implements Plugin {
  
  private EbayAPI reporter;
  
  public EbayPlugin(ConfigElementGroup config) {
    reporter = new EbayAPI(config);
  }
  
  @Override
  public String getName() {
    return "eBay";
  }

  @Override
  public void update() {
    reporter.update();
  }
  
  @Override
  public void saveState() {
    reporter.saveState();
  }

  @Override
  public ContentMenuEntry getMenuEntry(int id) {    
    ContentMenuEntry entry = new ContentMenuEntry(this, Plugin.ICON_PATH + "ebay.svg", id);
    entry.addSubMenuEntry(reporter.getReport());
    return entry;
  }
}
