package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.utils.ConfigElementGroup;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.JSONArray;
import main.utils.JSONContainer;
import main.utils.Logger;

public class EbayAPI {
  
  public enum AuctionType {
    AUCTION,        //classic competitive-bid online auction format
    AUCTIONWITHBIN, //auction, where also Buy It Now is enabled
    FIXEDPRICE,     //Buy it now for a fixed price
    STOREINVENTORY, //Buy for fixed price or send suggestion 
    OTHER           //?
  }; 
  
  private final static String SEARCH_URL = "http://svcs.ebay.com/services/search/FindingService/v1";
  private final static String SHOP_URL = "http://open.api.ebay.com/shopping";
  private final static int API_VERSION = 897;
  private String appId; 
  private String globalId;
  private String databaseFileName;
  private int itemIdCounter = 0;
  private EbayItemStorage storage;
  private EbaySearchTermHistory history;
  
  public EbayAPI(ConfigElementGroup config) {
    appId = config.getString("appID", null);
    globalId = config.getString("globalID", null);
    databaseFileName = config.getString("file", null);
    
    if(appId == null)
      throw new RuntimeException("Please store your ebay appID in the config file");
    if(globalId == null)
      throw new RuntimeException("Please store your ebay globalID in the config file");
    if(databaseFileName == null)
      throw new RuntimeException("Please store your ebay database file name in the config file");
    
    try {
      FileReader file = new FileReader(databaseFileName);
      storage = new EbayItemStorage(file);
      history = new EbaySearchTermHistory(this, file);
    } catch(IOException e) {
      Logger.error(e);
      storage = new EbayItemStorage();
      history = new EbaySearchTermHistory(this);
    }
    
    /*
    history.addSearchTerm(new Path(), createSearchTermGroup("10 Euro Silbermünzen"));
    history.addSearchTerm(new Path(), createSearchTermGroup("20 Euro Silbermünzen"));
    history.addSearchTerm(new Path(), createSearchTermGroup("25 Euro Niobmünzen"));
    history.addSearchTerm(new Path(), createSearchTermGroup("Philharmoniker"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen"), createSearchTermGroup("Schlösser"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen"), createSearchTermGroup("Republiksjubiläum 2005"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen"), createSearchTermGroup("Stifte und Klöster in Österreich"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen"), createSearchTermGroup("Sagen und Legenden in Österreich"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen"), createSearchTermGroup("Österreich aus Kinderhand"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen").add("Schlösser"), createSearchTerm("Schloss Ambras"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen").add("Schlösser"), createSearchTerm("Schloss Eggenberg"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen").add("Schlösser"), createSearchTerm("Schloss Hof"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen").add("Schlösser"), createSearchTerm("Schloss Schönbrunn"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen").add("Schlösser"), createSearchTerm("Schloss Hellbrunn"));
    history.addSearchTerm(new Path().add("10 Euro Silbermünzen").add("Schlösser"), createSearchTerm("Schloss Artstetten"));
    /**/
  }
  
  public void update() {
    history.update();
    storage.update();
  }
  
  public void saveState() {
    try {
      FileWriter file = new FileWriter(databaseFileName);
      storage.writeValue(file);
      history.writeValue(file);
    } catch(IOException e) {
      Logger.error(e);
    }
  }
  
  public EbaySearchTermHistory getSearchTermHistory() {
    return history;
  }
  
  LinkedList<EbayListItem> findByKeywords(String keywords) {
    String keywordsEencoded = HTTPUtils.replaceSpaces(keywords);
    Logger.log("Ebay: find by keywords " + keywordsEencoded);
    String url = String.format(SEARCH_URL + "?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + 
                 appId + "&GLOBAL-ID=" + globalId + "&RESPONSE-DATA-FORMAT=JSON&keywords=" + keywordsEencoded);
    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    if(response.failed()) {
      Logger.error("EbayAPI: failed to send request");
      return new LinkedList<EbayListItem>();
    }
    
    JSONContainer container = response.getJSONBody();
    container = container.getArray("findItemsByKeywordsResponse").getContainer(0);
    if(container.getArray("ack").getString(0, "").equals("Success") == false) {
      Logger.error("EbayAPI: failed to send request");
      return new LinkedList<EbayListItem>();
    }
    
    JSONArray items = container.getArray("searchResult").getContainer(0).getArray("item");
    LinkedList<EbayListItem> list = new LinkedList<EbayListItem>();
    for(int i = 0; i < items.length(); i++)
      list.add(new EbayListItem(items.getContainer(i)));
    
    return list;
  }
  
  EbayFullItem findByItemId(String id) {
    String url = SHOP_URL + "?callname=GetSingleItem&responseencoding=JSON&appid=" + appId + "&version=" + API_VERSION + "&ItemID=" + id + "&IncludeSelector=Details";
    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    if(response.failed()) {
      Logger.error("EbayAPI: failed to send request");
      return null;
    }
    
    JSONContainer container = response.getJSONBody();
    if(container.getString("Ack", "").equals("Success") == false) {
      Logger.error("EbayAPI: failed to send request");
      return null;
    }
      
    return new EbayFullItem(container.getSubContainer("Item"));
  }
  
  EbayCategory loadCategory(long categoryId) {
    String url = SHOP_URL + "?callname=GetCategoryInfo&responseencoding=JSON&appid=" + appId + "&version=" + API_VERSION + "&CategoryID=" + categoryId + "&IncludeSelector=ChildCategories";    
    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    if(response.failed()) {
      Logger.error("EbayAPI: failed to send request");
      Logger.error(url);
      return null;
    }

    JSONContainer container = response.getJSONBody();
    if(container.getString("Ack", "").equals("Failure")) {
      Logger.error("EbayAPI: failed to send request");
      Logger.error(url);
      Logger.error(container.toString());
      return null;
    }

    JSONArray categoryArray = container.getSubContainer("CategoryArray").getArray("Category");
    EbayCategory category = new EbayCategory(categoryArray.getContainer(0));

    for(int i = 1; i < categoryArray.length(); i++)
      category.addChild(new EbayCategory(categoryArray.getContainer(i)));  
        
    return category;
  }
  
  public EbaySearchTerm createSearchTerm(String searchTerm) {
    return new EbaySearchTerm(this, searchTerm, itemIdCounter++);
  }
  
  public EbaySearchTermGroup createSearchTermGroup(String groupName) {
    return new EbaySearchTermGroup(this, groupName);
  }
  
  public EbaySearchTermBase readSearchTermBase(FileReader file) throws IOException {
    byte type = file.readByte();
    EbaySearchTermBase term;
    if(type == 0) {
      term = new EbaySearchTerm(this, file);
      itemIdCounter = ((EbaySearchTerm)term).getId() + 1;
    }
    else
      term = new EbaySearchTermGroup(this, file);
    System.out.println("read " + term);
    return term;
  }
  
  public void registerSearchTermResult(EbaySearchTerm searchTerm, EbayMinimalItem item) {
    storage.add(searchTerm, item);
  }
  
  public List<Long> getStorageIdList() {
    return storage.getIdList();
  }
}
