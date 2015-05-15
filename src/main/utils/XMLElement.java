package main.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class XMLElement {
  
  public enum Type {
    ROOT,
    ELEMENT,
    VALUE,
    COMMENT,
    SCRIPT
  };
  
  private Type type;
  private String tagName;
  private String value;
  private String startTag;
  private String endTag;
  private LinkedList<XMLElement> children = new LinkedList<XMLElement>();
  
  public XMLElement() {   
    this.type = Type.ROOT;
  }
  
  public XMLElement(Type type, String value) {
    this.type = type;
    this.value = value;
  }
  
  public XMLElement(String startTag, String tagName) {
    this.type = Type.ELEMENT;      
    this.startTag = startTag;
    this.tagName = tagName;
  }    
  
  public void addChild(XMLElement child) {
    children.add(child);
  }
  
  public void setEndTag(String endTag) {
    this.endTag = endTag;
  }
  
  public Type getType() {
    return type;
  }
  
  public String getStartTag() {
    return startTag;
  }
  
  public String getTagName() {
    return tagName;
  }
  
  public String getValue() {
    return value;
  }
  
  public XMLElement getChild(int index) {
    return (index >= children.size() ? null : children.get(index));
  }
  
  public List<XMLElement> getChildren() {
    return children;
  }
  
  public List<XMLElement> searchTags(String regex) {
    List<XMLElement> list = new LinkedList<XMLElement>();
    Pattern pattern = Pattern.compile(regex);      
    searchTag(list, pattern);
    return list;
  }
  
  @Override
  public int hashCode() {
    if(tagName == null)
      return 0;
    return tagName.hashCode();
  }
  
  @Override
  public boolean equals(Object value) {      
    if(value instanceof XMLElement == false)
      return false;
    if(type != Type.ELEMENT || ((XMLElement)value).tagName == null)
      return false;
    return ((XMLElement)value).tagName.equals(tagName);
  }
  
  @Override
  public String toString() {
    if(type == Type.ELEMENT)
      return tagName;
    return type.name();
  }
  
  public void printHierarchy() {      
    printIntended("");
  }
  
  private void printIntended(String intend) {      
    if(type == Type.ELEMENT) {
      System.out.println(intend + startTag);
      for(XMLElement child : children)
        child.printIntended(intend + "  ");
      if(endTag != null)
        System.out.println(intend + endTag);
    }
    else if(type == Type.ROOT){
      System.out.println("ROOT node has " + children.size() + " children");
      for(XMLElement child : children)
        child.printIntended("");
    }
    else if(type == Type.SCRIPT)
      System.out.println(intend + "SCRIPT");
    else
      System.out.println(intend + value);
  }
  
  private void searchTag(List<XMLElement> list, Pattern pattern) {
    if(startTag != null && pattern.matcher(startTag).matches())
      list.add(this);
      
    for(XMLElement child : children)
      child.searchTag(list, pattern);
  }
}
