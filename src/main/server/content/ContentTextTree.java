package main.server.content;


public class ContentTextTree extends ContentItem {
  
  private static class ContentTextTreeNode extends ContentItem {
    
    public ContentTextTreeNode(String id, String title, boolean isLeafe) {
      super("texttreenode");
      setAttribute("id", id);
      setAttribute("title", title);
      if(isLeafe == false)
        setAttribute("children", "load");
    }
  }
  
  private ContentItemList children = new ContentItemList();
  
  public ContentTextTree(int x, int y) {
    super("texttree");
    setAttribute("x", x);
    setAttribute("y", y);
    setAttribute("children", children);
  }
  
  public void addNode(String id, String title) {
    children.add(new ContentTextTreeNode(id, title, false));
  }
  
  public void addLeafe(String id, String title) {
    children.add(new ContentTextTreeNode(id, title, true));
  }
}
