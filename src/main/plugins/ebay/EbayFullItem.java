package main.plugins.ebay;

import java.util.Calendar;

import main.plugins.ebay.EbayReporter.AuctionType;
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
  
  public EbayFullItem(JSONContainer definition) {
    title = definition.getString("Title", "");
    JSONContainer currentPrice = definition.getSubContainer("CurrentPrice");
    System.out.println("currentPrice: " + currentPrice);
    price = currentPrice.getFloat("Value", 0.0f);
    currency = currentPrice.getString("CurrencyID", "EUR");
    if(currency.equals("EUR"))
      currency = "€"; 
    itemId = definition.getString("ItemID", "");
    endTime = EbayReport.convertItemDate(definition.getString("EndTime", ""));
    type = EbayReport.getAuctionType(definition.getString("ListingType", ""));
    itemUrl = definition.getString("ViewItemURLForNaturalSearch", "");
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
}
