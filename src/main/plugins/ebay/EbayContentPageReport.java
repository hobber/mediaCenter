package main.plugins.ebay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import main.plugins.ebay.EbayAPI.AuctionType;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentItem;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;
import main.utils.Logger;

public class EbayContentPageReport extends ContentMenuSubEntry {

  static final SimpleDateFormat ITEM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  static final SimpleDateFormat PRINT_DATE_FORMAT = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
  
  private EbayAPI api;
  
  public EbayContentPageReport(EbayAPI api) {
    super("Report");
    this.api = api;
  }

  @Override
  public ContentItem handleAPIRequest(String parameter) {
    if(parameter.length() == 0)
      return getMainPage();
    return getDetailPage(parameter);
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage();
    
    ContentTitleBar titleBar = new ContentTitleBar();
    page.setTitleBar(titleBar);
    titleBar.addContentItem(new ContentText(5, 5, "Ebay Report", ContentText.TextType.TITLE));
    
    EbaySearchConfiguration searchConfiguration = new EbaySearchConfiguration(api, "20+Euro+PP+Trias");
    List<EbayListItem> list = searchConfiguration.search();
    for(EbayListItem item : list)
      page.addContentGroup(convertListItemToContentGroup(item));
    
    return page;
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
  
  private ContentGroup convertListItemToContentGroup(EbayListItem item) {
    ContentGroup group = new ContentGroup();
    group.appendLink(getContentOnClickElement(item.getItemId()));
    group.put(new ContentImage(0, 0, 80, 80, item.getImage()));
    group.put(new ContentText(95,  5, item.getTitle() + " - " + item.getPrice() + item.getCurrency() + ", " + 
      getAuctionTypeString(item.getAuctionType())));
    group.put(new ContentText(95, 30, EbayContentPageReport.convertToPrintDate(item.getEndTime())));
    group.put(new ContentText(95, 55, "click", item.getItemUrl()));
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
