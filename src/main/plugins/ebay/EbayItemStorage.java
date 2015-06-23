package main.plugins.ebay;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import main.data.DataSchemaObjectInterface;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.SortedMap;

public class EbayItemStorage implements DataSchemaObjectInterface {
  
  private static class Item implements DataSchemaObjectInterface {

    public static final Comparator<Item> comparator = new Comparator<Item>() {
      @Override
      public int compare(Item lhs, Item rhs) {
        return lhs.item.getEndTime().compareTo(rhs.item.getEndTime());
      }
    };
    
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
  
  
  
  
  private SortedMap<Long, Item> items = new SortedMap<Long, Item>();

  public EbayItemStorage() {
  }
  
  public EbayItemStorage(FileReader file) throws IOException {
    if(file != null)
      readValue(file);
  }
  
  public void update() {
    
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
    items.sortForValues(Item.comparator);
    file.writeInt(items.size());
    for(Entry<Long, Item> entry : items.entrySet()) {
      entry.getValue().writeValue(file);
      System.out.println("write " + entry.getValue());
    }
  }
  
  public void add(EbaySearchTerm searchTerm, EbayMinimalItem item) {
    items.put(item.getId(), new Item(searchTerm.getId(), item));
  }
  
  public List<Long> getIdList() {
    List<Long> list = new LinkedList<Long>();
    for(Entry<Long, Item> entry : items.entrySet())
      list.add(entry.getValue().getItemId());
    return list;
  }
}
