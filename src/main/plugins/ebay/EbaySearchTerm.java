package main.plugins.ebay;

import java.util.LinkedList;

public class EbaySearchTerm extends EbaySearchTermBase {

  private EbayReporter reporter;
  private String searchTerm;

  public EbaySearchTerm(EbayReporter reporter, String searchTerm) {
    this.reporter = reporter;
    this.searchTerm = searchTerm;
  }
  
  private LinkedList<EbayListItem> getCurrentItems() {
    reporter.findByKeywords(searchTerm);
  }
}
