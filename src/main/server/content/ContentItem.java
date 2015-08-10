package main.server.content;

import java.util.LinkedList;
import java.util.List;


public abstract class ContentItem {
  
  private static class ContentItemAttribute {
    
    String name;
    Object value;
    
    public ContentItemAttribute(String name, Object value) {
      this.name = name;
      this.value = value;
    }
    
    public String getName() {
      return name;
    }
    
    public Object getValue() {
      return value;
    }
  }
  
  protected List<ContentItemAttribute> attributes = new LinkedList<ContentItemAttribute>();
  
  protected ContentItem(String type) {
    attributes.add(new ContentItemAttribute("type", type));
  }
  
  protected void setAttribute(String name, Object value) {
    attributes.add(new ContentItemAttribute(name, value));
  }
  
  @Override
  public String toString() {
    String s = "{";
    for(int i = 0; i < attributes.size(); i++) {
      ContentItemAttribute attribute = attributes.get(i);
      Object value = attribute.getValue();
      if(value instanceof String)
        s += (i > 0 ? ", " : "") + "\"" + attribute.getName() + "\": \"" + escape(attribute.getValue().toString()) + "\"";
      else
        s += (i > 0 ? ", " : "") + "\"" + attribute.getName() + "\": " + attribute.getValue();
    }
    return s + "}";
  }  
  
  private String escape(String value) {
    String s = value;
    s = s.replaceAll("\"", "\\\\\"");
    return s;
  }
}
