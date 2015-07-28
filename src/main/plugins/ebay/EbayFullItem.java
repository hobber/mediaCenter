package main.plugins.ebay;

import java.util.Calendar;

import main.plugins.ebay.EbayAPI.AuctionType;
import main.utils.JSONContainer;

public class EbayFullItem {   
  
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
  
  public EbayFullItem(JSONContainer definition) {
    title = definition.getString("Title", "");
    JSONContainer currentPrice = definition.getSubContainer("CurrentPrice");
    price = currentPrice.getFloat("Value", 0.0f);
    currency = currentPrice.getString("CurrencyID", "EUR");
    if(currency.equals("EUR"))
      currency = "€"; 
    itemId = definition.getString("ItemID", "");
    endTime = EbayContentPageReport.convertItemDate(definition.getString("EndTime", ""));
    type = EbayContentPageReport.getAuctionType(definition.getString("ListingType", ""));
    itemUrl = definition.getString("ViewItemURLForNaturalSearch", "");
    image = definition.getArray("PictureURL").getString(0, null);
    if(image == null)
      image = definition.getString("GalleryURL", null);
    categoryId = definition.getString("PrimaryCategoryID", "");
    categoryName = definition.getString("PrimaryCategoryName", "");
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
  
  @Override
  public String toString() {
    return title + ": " + price + currency + " until " + EbayContentPageReport.convertToPrintDate(endTime) + " (" + itemUrl + ")";
  }
}
