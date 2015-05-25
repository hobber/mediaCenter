package main.plugins.austrianCharts;

import main.plugins.Plugin;
import main.server.content.ContentGroup;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuSubEntry;

class AustrianChartsHome extends ContentMenuSubEntry {
  
  public AustrianChartsHome(Plugin plugin) {
    super(plugin, "Home");
  }

  @Override
  public ContentPage handleAPIRequest(String parameter) {
    ContentPage page = new ContentPage();
    ContentGroup group = new ContentGroup();
    page.addContentGroup(group);
    group.put(new ContentText(5, 5, "Austrian Charts"));
    
    System.out.println("response: " + page.getContentString());
    
    return page;
  }
}
