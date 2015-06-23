package main.utils;

import java.util.LinkedList;

public class Path {
  
  private LinkedList<String> path = new LinkedList<String>();
  
  public Path() {    
  }
  
  public Path(JSONArray path) {
    for(int i = 0; i < path.length(); i++) {
      String part = path.getString(i, null);
      if(part == null)
        throw new RuntimeException("Invalid path: " + path);
      this.path.add(part);
    }
  }
  
  public Path add(String path) {
    this.path.add(path);
    return this;
  }
  
  public String pop() {
    return path.removeFirst();
  }
  
  public int length() {
    return path.size();
  }
  
  @Override
  public String toString() {
    String s = "";
    for(int i = 0; i < path.size(); i++)
      s += (i > 0 ? "." : "") + path.get(i);
    return s;
  }
}
