package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedList;

import main.utils.FileReader;
import main.utils.FileWriter;

public class EbaySearchTerm implements EbaySearchTermBase {

  private EbayAPI api;
  private String searchTerm;
  private int id;
  private boolean deleted = false;
  
  public EbaySearchTerm(EbayAPI api, FileReader file) throws IOException {
    this.api = api;
    readValue(file);
  }

  public EbaySearchTerm(EbayAPI api, String searchTerm, int id) {
    this.api = api;
    this.searchTerm = searchTerm;
    this.id = id;
  }
  
  public String getName() {
    return searchTerm;
  }
  
  public void rename(String searchTerm) {
    this.searchTerm = searchTerm;
  }
  
  public void delete() {
    deleted = true;
  }
  
  public boolean isDeleted() {
    return deleted;
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
    LinkedList<EbayListItem> list = api.findByKeywords(searchTerm);
    for(EbayListItem item : list)
      api.registerSearchTermResult(this, item.toMinimalItem());   
  }
  
  @Override
  public String toString() {
    return id + ": " + searchTerm;
  }
}
