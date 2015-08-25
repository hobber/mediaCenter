package main.server.content;

public class ContentGenericItem extends ContentItem {

  public ContentGenericItem(String type, String value) {
    super("generic");
    super.setAttribute("typeTag", type);
    super.setAttribute("value", value);
  }
  
  public ContentGenericItem setAttribute(String name, String value) {
    super.setAttribute(name, value);
    return this;
  }
}
