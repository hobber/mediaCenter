package main.plugins.ebay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.plugins.ebay.EbayAPI.AuctionType;
import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentItem;
import main.server.content.ContentOptions;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuSubEntry;
import main.utils.Logger;

public class EbayContentPageReport extends ContentMenuSubEntry {

  private static class Statistic {
    
    private int searchTermId;
    private String name;
    private float minimum;
    private float average;
    private float maximum;
    
    private Statistic(int searchTermId, String name, float minimum, float average, float maximum) {
      this.searchTermId = searchTermId;
      this.name = name;
      this.minimum = minimum;
      this.average = average;
      this.maximum = maximum;
    }
    
    public static Statistic create(int searchTermId, String name, List<EbayMinimalItem> terms) {
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
      return new Statistic(searchTermId, name, minimum, sum / terms.size(), maximum);
    }
    
    public int getSearchTermId() {
      return searchTermId;
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
    if(parameters.containsKey("action") && parameters.get("action").equals("setCategory"))
      return setCategory(parameters.get("parameter"), parameters.get("categoryID"));
    return getDetailPage(parameters.get("parameter"));
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage("Ebay Report");
    statistic.clear();
    updateAnalysis(history.getTerms());
    
    for(Statistic entry : statistic) {
      ContentGroup group = page.createContentGroup();
      group.setOptions(new ContentOptions("onClickParameter", Integer.toString(entry.getSearchTermId())));
      group.put(new ContentText(5, 5, entry.toString()));
    }
    
    return page;
  }
  
  private ContentPage getDetailPage(String parameter) {
    ContentPage page = new ContentPage("Ebay Report");
    int id = 0;
    try {
      id = Integer.parseInt(parameter);
    } catch(NumberFormatException e) {
      Logger.error(e);
      return page;
    }
    
    List<EbayMinimalItem> items = api.getItemsForSearchTerm(id);
    for(EbayMinimalItem item : items) {
      EbayFullItem fullItem = api.findByItemId(Long.toString(item.getId()));
      if(fullItem != null)
        page.addContentGroup(createItemGroup(fullItem, parameter));
    }
    
    return page;
  }
  
  private ContentPage setCategory(String parameter, String categoryId) {
    api.filterResultsForCategory(Integer.parseInt(parameter), Long.parseLong(categoryId));
    return getDetailPage(parameter);
  }
  
  private void updateAnalysis(EbaySearchTermGroup group) {
    for(EbaySearchTermBase term : group.getTerms()) {
      if(term instanceof EbaySearchTermGroup)
        updateAnalysis((EbaySearchTermGroup)term);
      else {
        int id = ((EbaySearchTerm)term).getId();
        statistic.add(Statistic.create(id, term.getName(), api.getItemsForSearchTerm(id)));
      }
    }
  }
  
  public static ContentGroup createItemGroup(EbayFullItem item, String parameter) {
    ContentGroup group = new ContentGroup();
    String image = item.getImage();
    if(image != null)
      group.put(new ContentImage(5, 5, 100, 100, item.getImage()));
    group.put(new ContentText(105, 5, item.getTitle() + " (" + item.getPrice() + item.getCurrency() + " - " + 
      EbayContentPageReport.convertToPrintDate(item.getEndTime()) + ")"));
    group.put(new ContentText(105, 45, item.getItemId() + ", " + getAuctionTypeString(item.getAuctionType()) + ", " +
      item.getCategoryName() + " (" + item.getCategoryId() + ")"));
    group.put(new ContentText(105, 85, "open", item.getItemUrl()));
    group.put(new ContentButton(150, 85, "set Category as filter", parameter + "&action=setCategory&categoryID=" + item.getCategoryId()));
    group.setOptions(new ContentOptions("groupBoarder", "true"));
    return group;
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
