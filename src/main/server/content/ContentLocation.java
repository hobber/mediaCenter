package main.server.content;

import java.util.Map.Entry;

import main.utils.SortedMap;

public class ContentLocation {

  private SortedMap<String, String> parameters = new SortedMap<String, String>();
  
  public ContentLocation(String plugin, String page) {
    parameters.put("plugin", plugin);
    parameters.put("page", page);
  }
  
  public ContentLocation(String plugin, String page, String parameter) {
    parameters.put("plugin", plugin);
    parameters.put("page", page);    
    parameters.put("parameter", parameter);
  }
  
  @Override
  public String toString() {
    String s = "{";
    int counter = 0;
    for(Entry<String, String> entry : parameters.entrySet())
      s += (counter++ > 0 ? ", " : "") + "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
    return s + "}";
  }
}
