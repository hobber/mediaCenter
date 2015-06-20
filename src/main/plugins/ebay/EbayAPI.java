package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedList;

import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.plugins.Plugin;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuEntry;
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
  private EbayReport report;
  
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
    
    report = new EbayReport(this);
  }
  
  public void update() {
    history.update();
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
  
  public EbayReport getReport() {
    return report;
  }
  
  LinkedList<EbayListItem> findByKeywords(String keywords) {
    String url = String.format(SEARCH_URL + "?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + appId + "&GLOBAL-ID=" + globalId + "&RESPONSE-DATA-FORMAT=JSON&keywords=" + keywords);
    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    if(response.failed()) {
      Logger.error("EbayReporter: failed to send request");
      return new LinkedList<EbayListItem>();
    }
    
    JSONContainer container = response.getJSONBody();
    container = container.getArray("findItemsByKeywordsResponse").getContainer(0);
    if(container.getArray("ack").getString(0, "").equals("Success") == false) {
      Logger.error("EbayReporter: failed to send request");
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
      Logger.error("EbayReporter: failed to send request");
      return null;
    }
    
    JSONContainer container = response.getJSONBody();
    if(container.getString("Ack", "").equals("Success") == false) {
      Logger.error("EbayReporter: failed to send request");
      return null;
    }
      
    return new EbayFullItem(container.getSubContainer("Item"));
  }
  
  public EbaySearchTerm createSearchTerm(String searchTerm) {
    return new EbaySearchTerm(this, searchTerm, itemIdCounter++);
  }
  
  public EbaySearchTermGroup createSearchTermGroup() {
    return new EbaySearchTermGroup(this);
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
}
