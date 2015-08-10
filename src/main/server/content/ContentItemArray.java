package main.server.content;

import java.util.LinkedList;
import java.util.List;

public class ContentItemArray {

  private LinkedList<List<ContentItem>> rows = new LinkedList<List<ContentItem>>();
  
  public ContentItemArray() {
    
  }
  
  public void addRow(List<ContentItem> row) {
    rows.add(row);
  }
  
  @Override
  public String toString() {
    String s = "[";
    for(int i = 0; i< rows.size(); i++) {
      List<ContentItem> row = rows.get(i);
      s += i > 0 ? ", [" : "[";
      for(int j = 0; j < row.size(); j++)
        s+= (j > 0 ? ", " : "") + row.get(j);
      s += "]";
    }
    return s + "]";
  }
}
