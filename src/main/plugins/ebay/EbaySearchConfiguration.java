package main.plugins.ebay;

import java.util.List;

public class EbaySearchConfiguration {

  private EbayAPI api;
  private String searchTerm;
  
  public EbaySearchConfiguration(EbayAPI api, String searchTerm) {
    this.api = api;
    this.searchTerm = searchTerm;
  }
  
  public List<EbayListItem> search() {
    List<EbayListItem> list = api.findByKeywords(searchTerm);
    return list;
  } 
}
