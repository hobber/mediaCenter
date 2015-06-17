package main.plugins.ebay;

import java.util.LinkedList;

public class EbaySearchTerm extends EbaySearchTermBase {

  private EbayReporter reporter;
  private String searchTerm;  

  public EbaySearchTerm(EbayReporter reporter, String searchTerm) {
    this.reporter = reporter;
    this.searchTerm = searchTerm;
  }
  
  public void update() {
    LinkedList<EbayListItem> list = reporter.findByKeywords(searchTerm);
    float minimumFixed = 0.0f;
    float minimumAuction = 0.0f;
    float sumFixed = 0.0f;
    float sumAuction = 0.0f;
    float averageFixed = 0.0f;
    float averageAuction = 0.0f;
    int counterFixed = 0;
    int counterAuction = 0;
    
    for(EbayListItem item : list) {
      float price = item.getPrice();
      EbayReporter.AuctionType type = item.getAuctionType();
      if(type == EbayReporter.AuctionType.FIXEDPRICE) {
        sumFixed += price;
        if(minimumFixed == 0.0f || price < minimumFixed)
          minimumFixed = price;
        counterFixed++;
      }
      else if(type == EbayReporter.AuctionType.AUCTION || type == EbayReporter.AuctionType.AUCTIONWITHBIN) {
        sumAuction += price;
        if(minimumAuction == 0.0f || price < minimumAuction)
          minimumAuction = price;
        counterAuction++;
      }
    }
        
    if(counterFixed > 0)
      averageFixed = sumFixed / counterFixed;
    if(counterAuction > 0)
      averageAuction = sumAuction / counterAuction;   
  }
}
