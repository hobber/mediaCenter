package main.plugins.ebay;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.data.DataSchemaObjectInterface;
import main.http.HTTPUtils;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
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
    
    public long getEndTime() {
      return item.getEndTime();
    }
    
    public int getSearchTermId() {
      return searchTermId;
    }
    
    public EbayMinimalItem getItem() {
      return item;
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
  
  private EbayAPI api;
  private SortedMap<Long, Item> completedItems = new SortedMap<Long, Item>();
  private SortedMap<Long, Item> activeItems = new SortedMap<Long, Item>();
  private Map<Integer, List<EbayMinimalItem>> itemMap = new HashMap<Integer, List<EbayMinimalItem>>();
  private int imageCounter;

  public EbayItemStorage(EbayAPI api) {
    this.api = api;
  }
  
  public EbayItemStorage(EbayAPI api, FileReader file) throws IOException {
    this.api = api;
    if(file != null)
      readValue(file);
  }
  
  public void update() {
    long now = Calendar.getInstance().getTimeInMillis();
    Iterator<Map.Entry<Long, Item>> iterator = activeItems.entrySet().iterator();
    int completed = 0;
    while(iterator.hasNext()) {
      Map.Entry<Long, Item> entry = iterator.next();
      if(entry.getValue().getEndTime() <= now) {
        String id = Long.toString(entry.getValue().getItemId());
        EbayFullItem item = api.findByItemId(id);
        if(item != null) {
          iterator.remove();
          int searchTermId = entry.getValue().getSearchTermId();
          int imageId = saveImageAndGetId(item.getImage());
          completedItems.put(entry.getKey(), new Item(searchTermId, item.toMinimalItem(imageId)));
          completed++;
        }
        else 
          Logger.error("could not complete item " + entry.getValue());
      }
    }
    Logger.log("have " + activeItems.size() + " active items after update, " + completed + (completed == 1 ? " was" : " were")+ " completed");
    sortCompletedItemsBySearchTermIds();
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    imageCounter = file.readInt();
    int size = file.readInt();
    for(int i = 0; i < size; i++) {
      Item item = new Item(file);
      completedItems.put(item.getItemId(), item);
    }
    size = file.readInt();
    for(int i = 0; i < size; i++) {
      Item item = new Item(file);
      activeItems.put(item.getItemId(), item);
    }
    Logger.log("read " + completedItems.size() + " completed items + " + activeItems.size() + " active items");
    sortCompletedItemsBySearchTermIds();
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(imageCounter);
    completedItems.sortForValues(Item.comparator);
    file.writeInt(completedItems.size());
    for(Entry<Long, Item> entry : completedItems.entrySet()) {
      entry.getValue().writeValue(file);
    }    
    activeItems.sortForValues(Item.comparator);
    file.writeInt(activeItems.size());
    for(Entry<Long, Item> entry : activeItems.entrySet()) {
      entry.getValue().writeValue(file);
    }
  }
  
  public boolean knowsItemId(long itemId) {
    return completedItems.containsKey(itemId) || activeItems.containsKey(itemId);
  }
  
  public int saveImageAndGetId(String imageUrl) {
    try {
      HTTPUtils.saveWebImage(new URL(imageUrl), String.format("data/images/ebay/%09d.jpg", imageCounter));
      int id = imageCounter;
      imageCounter++;
      return id;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }
  
  public void add(EbaySearchTerm searchTerm, EbayMinimalItem item) {
    activeItems.put(item.getId(), new Item(searchTerm.getId(), item));
  }
  
  public List<EbayMinimalItem> getItemsForSearchTerm(int searchTermId) {
    List<EbayMinimalItem> list = itemMap.get(searchTermId);
    if(list == null)
      return new LinkedList<EbayMinimalItem>();
    return list;
  }
  
  public void filterResultsForCategory(int searchTermId, long categoryId) {
    List<EbayMinimalItem> list = itemMap.remove(searchTermId);
    
    // TODO
    
  }

  private void sortCompletedItemsBySearchTermIds() {
    completedItems.sortForValues(Item.comparator);
    itemMap = new HashMap<Integer, List<EbayMinimalItem>>();
    for(Item entry : completedItems.values()) {
      int termId = entry.getSearchTermId();
      if(itemMap.containsKey(termId) == false)
        itemMap.put(termId, new LinkedList<EbayMinimalItem>());
      itemMap.get(termId).add(entry.getItem());
    }
  }
}
