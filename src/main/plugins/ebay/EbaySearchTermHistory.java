package main.plugins.ebay;

import java.util.LinkedList;

public class EbaySearchTermHistory {

  private EbayReporter reporter;
  private LinkedList<EbaySearchTermBase> terms = new LinkedList<EbaySearchTermBase>();
  
  public EbaySearchTermHistory(EbayReporter reporter) {
    this.reporter = reporter;
    
    //TODO: load from DB file
    terms.add(new EbaySearchTerm(reporter, "20+Euro+PP+Trias"));
    terms.add(new EbaySearchTerm(reporter, "20+Euro+PP+Jura"));
    terms.add(new EbaySearchTerm(reporter, "20+Euro+PP+Kreide"));
  }
}
