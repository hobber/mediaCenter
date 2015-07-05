package main.server.content;

import org.json.JSONObject;

public class ContentTextTree extends ContentItem {
  
  public ContentTextTree() {
    data.put("type", "texttree");
  }
  
  public ContentTextTree(int x, int y) {
    data.put("type", "texttree");
    data.put("x", x);
    data.put("y", y);
  }
  
  public void addNode(String id, String title) {
    JSONObject element = new JSONObject();
    element.put("id", id);
    element.put("title", title);
    element.put("children", "load");
    data.append("children", element);
  }
  
  public void addNode(String id, String title, ContentTextTree children) {
    JSONObject element = new JSONObject();
    element.put("id", id);
    element.put("title", title);
    element.put("children", children);
    data.append("children", element);
  }
  
  public void addLeaf(String id, String title) {
    JSONObject element = new JSONObject();
    element.put("id", id);
    element.put("title", title);
    data.append("children", element);
  }
}
