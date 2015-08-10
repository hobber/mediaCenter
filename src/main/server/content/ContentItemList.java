package main.server.content;

import java.util.LinkedList;

public class ContentItemList {
  
  private LinkedList<ContentItem> list = new LinkedList<ContentItem>();
  
  public ContentItemList() {
    
  }
  
  public void add(ContentItem item) {
    list.add(item);
  }
  
  @Override
  public String toString() {
    String s = "[";
    for(int i = 0; i < list.size(); i++)
      s += (i > 0 ? ", " : "") + list.get(i);
    return s + "]";
  }
}
