package main.plugins.ebay;

import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;

public class EbayContentPageConfig extends ContentMenuSubEntry {

  private EbayAPI api;
  
  public EbayContentPageConfig(EbayAPI api) {
    super("Config");
    this.api = api;
  }

  @Override
  public ContentPage handleAPIRequest(String parameter) {
    return getMainPage();
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage();
    
    ContentTitleBar titleBar = new ContentTitleBar();
    page.setTitleBar(titleBar);
    titleBar.addContentItem(new ContentText(5, 5, "Ebay Config", ContentText.TextType.TITLE));
    
    for(Long id : api.getStorageIdList()) {
      EbayFullItem response = api.findByItemId(id.toString());
      if(response == null)
        continue;
      page.addContentGroup(EbayContentPageReport.createItemGroup(response));
    }
    
    return page;
  }
}
