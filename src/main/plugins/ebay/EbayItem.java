package main.plugins.ebay;

import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentText;
import main.utils.JSONContainer;

public class EbayItem {

  private EbayReport report;
  private String title;
  private float price;
  private String currency;
  private String itemUrl;
  private String image;
  
  public EbayItem(EbayReport report, JSONContainer definition) {
    this.report = report;
    
    title = definition.getArray("title").getString(0, "");
    
    JSONContainer currentPrice = definition.getArray("sellingStatus").getContainer(0).getArray("currentPrice").getContainer(0);
    price = Float.parseFloat(currentPrice.getString("__value__", ""));
    currency = currentPrice.getString("@currencyId", "EUR");
    if(currency.equals("EUR"))
      currency = "€";
    
    itemUrl = definition.getArray("viewItemURL").getString(0, ""); 
    
    if(definition.containsObject("galleryPlusPictureURL"))
      image = definition.getArray("galleryPlusPictureURL").getString(0, "");
    else
      image = definition.getArray("galleryURL").getString(0, "");
  }
  
  public ContentGroup getContentGroup() {
    ContentGroup group = new ContentGroup();
    group.appendLink(report.getContentOnClickElement("id"));
    group.put(new ContentImage(0, 0, 80, 80, image));
    group.put(new ContentText(85, 5, title + " - " + price + currency));
    return group;
  }
}
