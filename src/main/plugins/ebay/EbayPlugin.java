package main.plugins.ebay;

import main.plugins.Plugin;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;

public class EbayPlugin implements Plugin {
  
  private EbayAPI api;
  private EbayContentPageReport reportPage;
  private EbayContentPageConfig configPage;
  
  public EbayPlugin(ConfigElementGroup config) {
    api = new EbayAPI(config);
    reportPage = new EbayContentPageReport(api);
    configPage = new EbayContentPageConfig(api);
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
    ContentMenuEntry entry = new ContentMenuEntry(this, Plugin.ICON_PATH + "ebay.svg");
//    entry.addSubMenuEntry(reportPage);
    entry.addSubMenuEntry(configPage);
    return entry;
  }
}
