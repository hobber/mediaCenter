package main.server.content;

public class ContentEventOnClick extends ContentItem {

  public ContentEventOnClick(int selectId) {
    super("onclick");
    setAttribute("selectId", selectId);
  }

}
