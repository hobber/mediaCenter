package main.plugins.ebay;

import main.plugins.Plugin;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;

public class EbayPlugin implements Plugin {
  
  private EbayAPI api;
  private ContentMenuEntry entry;
  
  public EbayPlugin(ConfigElementGroup config) {
    api = new EbayAPI(config);
    EbayContentPageReport reportPage = new EbayContentPageReport(getName(), api);
    EbayContentPageConfig configPage = new EbayContentPageConfig(getName(), api);
    
    entry = new ContentMenuEntry(this, Plugin.ICON_PATH + "ebay.svg");
    entry.addSubMenuEntry(reportPage);
    entry.addSubMenuEntry(configPage);
  }
  
  @Override
  public String getName() {
    return "eBay";
  }

  @Override
  public void update() {
    api.update();
  }
  
  @Override
  public void saveState() {
    api.saveState();
  }

  @Override
  public ContentMenuEntry getMenuEntry() {    
    return entry;
  }
}
