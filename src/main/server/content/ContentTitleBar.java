package main.server.content;


public class ContentTitleBar extends ContentItem {

  private ContentItemList items = new ContentItemList();
  
  public ContentTitleBar(String title) {
    super("titlebar");
    items.add(new ContentText(5, 5, title, ContentText.TextType.TITLE));
    setAttribute("items", items);
  }
  
	public void addContentItem(ContentItem item) {
	  items.add(item);
	}
}
