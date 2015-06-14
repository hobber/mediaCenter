package main.plugins.ebay;

import java.util.LinkedList;

import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;
import main.utils.JSONArray;

public class EbayReport extends ContentMenuSubEntry {

  private EbayReporter ebay;
  
  public EbayReport(EbayReporter ebay) {
    super("Report");
    this.ebay = ebay;
  }

  @Override
  public ContentPage handleAPIRequest(String parameter) {
    if(parameter.length() == 0)
      return getMainPage();
    return new ContentPage();
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage();
    
    ContentTitleBar titleBar = new ContentTitleBar();
    page.setTitleBar(titleBar);
    titleBar.addContentItem(new ContentText(5, 5, "Ebay Report", ContentText.TextType.TITLE));
    
    JSONArray items = ebay.findByKeywords("20+Euro+PP+Trias");
    LinkedList<EbayItem> list = new LinkedList<EbayItem>();
    for(int i = 0; i < items.length(); i++)
      list.add(new EbayItem(this, items.getContainer(i)));    
    
    for(EbayItem item : list)
      page.addContentGroup(item.getContentGroup());
    
    return page;
  }

}
