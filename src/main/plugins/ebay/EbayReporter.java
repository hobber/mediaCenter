package main.plugins.ebay;

import java.util.LinkedList;
import java.util.List;

import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.plugins.Plugin;
import main.server.content.ContentOnClick;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;
import main.utils.JSONArray;
import main.utils.JSONContainer;
import main.utils.Logger;

public class EbayReporter implements Plugin {

  private final static String API_URL = "http://svcs.ebay.com/services/search/FindingService/v1";
  private String appId; 
  private String globalId;
  
  public EbayReporter(ConfigElementGroup config) {
    appId = config.getString("appID", null);
    globalId = config.getString("globalID", null);
    
    if(appId == null)
      throw new RuntimeException("Please store your ebay appID in the config file");
    if(globalId == null)
      throw new RuntimeException("Please store your ebay globalID in the config file");
  }
  
  @Override
  public String getName() {
    return "eBay";
  }

  @Override
  public void saveState() {
    // TODO Auto-generated method stub
  }

  @Override
  public ContentMenuEntry getMenuEntry() {
    ContentMenuEntry entry = new ContentMenuEntry(this, ICON_PATH + "ebay.svg");
    entry.addSubMenuEntry(new EbayReport(this));
    return entry;
  }
  
  JSONArray findByKeywords(String keywords) {
    LinkedList<EbayItem> list = new LinkedList<EbayItem>();
    String url = String.format(API_URL + "?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + appId + "&GLOBAL-ID=" + globalId + "&RESPONSE-DATA-FORMAT=JSON&keywords=" + keywords);
    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    if(response.failed()) {
      Logger.error("EbayReporter: failed to send request");
      return new JSONArray();
    }
    JSONContainer container = response.getJSONBody();
    container = container.getArray("findItemsByKeywordsResponse").getContainer(0);
    return container.getArray("searchResult").getContainer(0).getArray("item");
  }
}
