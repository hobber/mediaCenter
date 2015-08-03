package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedList;

import main.utils.FileReader;
import main.utils.FileWriter;

public class EbaySearchTerm implements EbaySearchTermBase {

  private EbayAPI api;
  private String searchTerm;
  private int id;
  private long categoryId = -1;
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
    categoryId = file.readLong();
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(searchTerm.length());
    file.writeString(searchTerm);
    file.writeInt(id);
    file.writeLong(categoryId);
  }
  
  public int getId() {
    return id;
  }
  
  public void setCategoryId(long categoryId) {
    this.categoryId = categoryId; 
  }
  
  public boolean hasCategory() {
    return categoryId != -1;
  }
  
  public void update() {
    LinkedList<EbayListItem> list = api.findByKeywords(searchTerm);
    for(EbayListItem item : list) {
      if(categoryId >= 0 && Long.parseLong(item.getCategoryId()) != categoryId)
        continue;
      
      if(api.knowsItemId(Long.parseLong(item.getItemId())))
        continue;
      int imageId = api.saveImageAndGetId(item.getImage());
      api.registerSearchTermResult(this, item.toMinimalItem(imageId));
      
      String categoryId = item.getCategoryId();
      String categoryName = item.getCategoryName();
      if(categoryId != null && categoryName != null)
        api.registerCategory(Long.parseLong(categoryId), categoryName);
    }
  }
  
  @Override
  public String toString() {
    return id + ": " + searchTerm + (categoryId >= 0 ? " (" + categoryId + ")" : "");
  }
}
