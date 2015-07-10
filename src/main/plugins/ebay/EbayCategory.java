package main.plugins.ebay;

import java.util.LinkedList;
import java.util.List;

import main.utils.JSONContainer;

public class EbayCategory {

  private String name;
  private long id;
  private boolean leaf;
  private LinkedList<EbayCategory> children = new LinkedList<EbayCategory>();
  
  public EbayCategory(JSONContainer definition) {
    name = definition.getString("CategoryName", null);
    id = Long.parseLong(definition.getString("CategoryID", null));
    leaf = definition.getBoolean("LeafCategory", false);
  }
  
  public void addChild(EbayCategory child) {
    children.add(child);
  }
  
  public String getName() {
    return name;
  }
  
  public long getId() {
    return id;
  }
  
  public boolean isLeaf() {
    return leaf;
  }
  
  public List<EbayCategory> getChildren() {
    return children;
  }
  
  @Override
  public String toString() {
    return name + " (" + id + ")";
  }
}
