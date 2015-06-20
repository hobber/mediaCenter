package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedList;

import main.utils.FileReader;
import main.utils.FileWriter;

public class EbaySearchTerm extends EbaySearchTermBase {

  private EbayAPI reporter;
  private String searchTerm;
  private int id;
  
  public EbaySearchTerm(EbayAPI reporter, FileReader file) throws IOException {
    this.reporter = reporter;
    readValue(file);
  }

  public EbaySearchTerm(EbayAPI reporter, String searchTerm, int id) {
    this.reporter = reporter;
    this.searchTerm = searchTerm;
    this.id = id;
  }
  
  @Override
  public void readValue(FileReader file) throws IOException {
    int length = file.readInt();
    searchTerm = file.readString(length);
    id = file.readInt();
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(searchTerm.length());
    file.writeString(searchTerm);
    file.writeInt(id);
  }
  
  public int getId() {
    return id;
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
      reporter.registerSearchTermResult(this, item.toMinimalItem());
      float price = item.getPrice();
      EbayAPI.AuctionType type = item.getAuctionType();
      if(type == EbayAPI.AuctionType.FIXEDPRICE) {
        sumFixed += price;
        if(minimumFixed == 0.0f || price < minimumFixed)
          minimumFixed = price;
        counterFixed++;
      }
      else if(type == EbayAPI.AuctionType.AUCTION || type == EbayAPI.AuctionType.AUCTIONWITHBIN) {
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
  
  @Override
  public String toString() {
    return id + ": " + searchTerm;
  }
}
