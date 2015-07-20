package main.plugins.ebay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.plugins.ebay.EbayAPI.AuctionType;
import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuSubEntry;
import main.utils.Logger;

public class EbayContentPageReport extends ContentMenuSubEntry {

  private static class Statistic {
    
    private String name;
    private float minimum;
    private float average;
    private float maximum;
    
    private Statistic(String name, float minimum, float average, float maximum) {
      this.name = name;
      this.minimum = minimum;
      this.average = average;
      this.maximum = maximum;
    }
    
    public static Statistic create(String name, List<EbayMinimalItem> terms) {
      Float minimum = 0.0f;
      Float sum = 0.0f;
      Float maximum = 0.0f;
      for(EbayMinimalItem term : terms) {
        float price = term.getPrice();
        if(minimum == 0.0 || price < minimum)
          minimum = price;
        if(price > maximum)
          maximum = price;
        sum += price;
      }
      return new Statistic(name, minimum, sum / terms.size(), maximum);
    }
    
    @Override
    public String toString() {
      return name + ": " + minimum + " - " + maximum + " (" + average + ")";
    }
  }
  
  static final SimpleDateFormat ITEM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  static final SimpleDateFormat PRINT_DATE_FORMAT = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
  
  private EbayAPI api;
  private EbaySearchTermHistory history;
  private List<Statistic> statistic = new LinkedList<Statistic>();
  
  public EbayContentPageReport(EbayAPI api) {
    super("Report");
    this.api = api;
    this.history = api.getSearchTermHistory();
  }

  @Override
  public ContentItem handleAPIRequest(Map<String, String> parameters) {
    if(parameters.size() == 0)
      return getMainPage();
    return getDetailPage(parameters.get("itemId"));
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage("Ebay Report");
    statistic.clear();
    updateAnalysis(history.getTerms());
    
    for(Statistic entry : statistic) {
      ContentGroup group = page.createContentGroup();
      group.put(new ContentText(5, 5, entry.toString()));
    }
    
    return page;
  }
  
  private void updateAnalysis(EbaySearchTermGroup group) {
    for(EbaySearchTermBase term : group.getTerms()) {
      if(term instanceof EbaySearchTermGroup)
        updateAnalysis((EbaySearchTermGroup)term);
      else
        statistic.add(Statistic.create(term.getName(), api.getItemsForSearchTerm(((EbaySearchTerm)term).getId())));
    }
  }
  
  public static ContentGroup createItemGroup(EbayFullItem item) {
    ContentGroup group = new ContentGroup();
    group.put(new ContentText(5, 5, item.getItemId() + ", " + getAuctionTypeString(item.getAuctionType())));
    group.put(new ContentText(5, 45, item.getTitle() + ", " + item.getPrice() + item.getCurrency() + ", " + 
      EbayContentPageReport.convertToPrintDate(item.getEndTime())));
    group.put(new ContentText(5, 85, "click", item.getItemUrl()));
    return group;
  }
  
  private ContentPage getDetailPage(String id) {
    EbayFullItem item = api.findByItemId(id);
    ContentPage page = new ContentPage();
    page.addContentGroup(createItemGroup(item));
    return page;
  }
  
  static Calendar convertItemDate(String date) {
    Calendar calendar = Calendar.getInstance();
    try {
      calendar.setTime(ITEM_DATE_FORMAT.parse(date));
    } catch(ParseException e) {
      Logger.error(e);
    }
    return calendar;
  }
  
  static String convertToPrintDate(Calendar date) {
    return PRINT_DATE_FORMAT.format(date.getTime());
  }
  
  static AuctionType getAuctionType(String name) {
    if(name.equalsIgnoreCase("FIXEDPRICEITEM"))
      return AuctionType.FIXEDPRICE;
    if(name.equalsIgnoreCase("CHINESE"))
      return AuctionType.AUCTIONWITHBIN;
    try {
      return AuctionType.valueOf(name.toUpperCase());
    } catch(IllegalArgumentException e) {
      Logger.error(e);
      return AuctionType.OTHER;
    }
  }
  
  static String getAuctionTypeString(AuctionType type) {
    switch(type) {
      case AUCTION:        return "Auction";
      case AUCTIONWITHBIN: return "Fixed price or auction";
      case FIXEDPRICE:     return "Fixed price";
      case STOREINVENTORY: return "Fixed price or suggestion";
      default:             return "Other";
    }
  }
}
