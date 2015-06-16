package main.plugins.ebay;

import java.util.LinkedList;

public class EbaySearchTermGroup extends EbaySearchTermBase {

  private LinkedList<EbaySearchTermBase> terms = new LinkedList<EbaySearchTermBase>();
  
  public void add(EbaySearchTermBase term) {
    terms.add(term);
  }
}
