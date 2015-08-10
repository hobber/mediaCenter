package main.server.content;


public class ContentOverlay extends ContentItem {
  
  private ContentItemList items = new ContentItemList();
  
  public ContentOverlay(String caption, int width, int height) {
    super("overlay");
    setAttribute("caption", caption);
    setAttribute("width", width);
    setAttribute("height", height); 
    setAttribute("items", items);
  }
  
  public void add(ContentItem item) {   
    items.add(item);
  }
}
