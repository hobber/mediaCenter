package main.plugins.ebay;

import java.util.Calendar;

import main.plugins.ebay.EbayAPI.AuctionType;
import main.utils.JSONContainer;

public class EbayListItem {

  private String title;
  private float price;
  private String currency;
  private String itemId;
  private String image;
  private Calendar endTime;
  private AuctionType type;
  private String itemUrl;
  private String categoryId;
  private String categoryName;
  
  public EbayListItem(JSONContainer definition) {
    title = definition.getArray("title").getString(0, "");
    
    JSONContainer currentPrice = definition.getArray("sellingStatus").getContainer(0).getArray("currentPrice").getContainer(0);
    price = Float.parseFloat(currentPrice.getString("__value__", ""));
    currency = currentPrice.getString("@currencyId", "EUR");
    if(currency.equals("EUR"))
      currency = "€";
    
    itemId = definition.getArray("itemId").getString(0, ""); 
    
    if(definition.containsObject("galleryPlusPictureURL"))
      image = definition.getArray("galleryPlusPictureURL").getString(0, "");
    else
      image = definition.getArray("galleryURL").getString(0, "");
    
    JSONContainer listingInfo = definition.getArray("listingInfo").getContainer(0);
    endTime = EbayContentPageReport.convertItemDate(listingInfo.getArray("endTime").getString(0,  ""));
    type = EbayContentPageReport.getAuctionType(listingInfo.getArray("listingType").getString(0, "").toUpperCase());
    itemUrl = definition.getArray("viewItemURL").getString(0, "");
    
    categoryId = definition.getArray("primaryCategory").getContainer(0).getString("categoryId", null);
    categoryName = definition.getArray("primaryCategory").getContainer(0).getString("categoryName", null);
  }
  
  public String getTitle() {
    return title;
  }
  
  public float getPrice() {
    return price;
  }
  
  public String getCurrency() {
    return currency;
  }
  
  public String getItemId() {
    return itemId;
  }
  
  public String getImage() {
    return image;
  }
  
  public Calendar getEndTime() {
    return endTime;
  }
  
  public AuctionType getAuctionType() {
    return type;
  }
  
  public String getItemUrl() {
    return itemUrl;
  }
  
  public String getCategoryId() {
    return categoryId;
  }
  
  public String getCategoryName() {
    return categoryName;
  }
  
  public EbayMinimalItem toMinimalItem(int imageId) {
    return new EbayMinimalItem(itemId, title, type, endTime, price, imageId);
  }
}
