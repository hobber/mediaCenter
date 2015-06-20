package main.plugins.ebay;

import java.io.IOException;
import java.util.HashMap;

import main.data.DataSchemaObjectInterface;
import main.utils.FileReader;
import main.utils.FileWriter;

public class EbayItemStorage implements DataSchemaObjectInterface {
  
  private class Item implements DataSchemaObjectInterface {

    private int searchTermId;
    private EbayMinimalItem item;
    
    public Item(int searchTermId, EbayMinimalItem item) {
      this.searchTermId = searchTermId;
      this.item = item;
    }
    
    public Item(FileReader file) throws IOException {
      readValue(file);
    }
    
    public long getItemId() {
      return item.getId();
    }
    
    @Override
    public void readValue(FileReader file) throws IOException {
      searchTermId = file.readInt();
      item = new EbayMinimalItem(file);
    }

    @Override
    public void writeValue(FileWriter file) throws IOException {
      file.writeInt(searchTermId);
      item.write(file);
    } 
    
    @Override
    public String toString() {
      return item.toString() + ", searchTerm " + searchTermId;
    }
  }
  
  private HashMap<Long, Item> items = new HashMap<Long, Item>();

  public EbayItemStorage() {
  }
  
  public EbayItemStorage(FileReader file) throws IOException {
    if(file != null)
      readValue(file);
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    int size = file.readInt();
    for(int i = 0; i < size; i++) {
      Item item = new Item(file);
      items.put(item.getItemId(), item);
      System.out.println("read " + item);
    }
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(items.size());
    for(Item item : items.values()) {
      item.writeValue(file);
      System.out.println("write " + item);
    }
  }
  
  public void add(EbaySearchTerm searchTerm, EbayMinimalItem item) {
    items.put(item.getId(), new Item(searchTerm.getId(), item));
  }
}
