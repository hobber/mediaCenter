package main.plugins.ebay;

import java.io.IOException;
import java.util.HashMap;

import main.data.DataSchemaObjectInterface;
import main.utils.FileReader;
import main.utils.FileWriter;

public class EbayItemStorage implements DataSchemaObjectInterface {
  
  private HashMap<Long, EbayMinimalItem> items = new HashMap<Long, EbayMinimalItem>();

  public EbayItemStorage(FileReader file) throws IOException {
    readValue(file);
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    int size = file.readInt();
    for(int i = 0; i < size; i++) {
      EbayMinimalItem item = new EbayMinimalItem(file);
      items.put(item.getId(), item);
      System.out.println("read " + item);
    }
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(items.size());
    for(EbayMinimalItem item : items.values()) {
      item.write(file);
      System.out.println("write " + item);
    }
  }
  
  public void add(EbayMinimalItem item) {
    items.put(item.getId(), item);
  }
}
