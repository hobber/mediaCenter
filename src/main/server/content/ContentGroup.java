package main.server.content;


public class ContentGroup extends ContentItem {	
	
  private ContentItemList items = new ContentItemList();
  
	public ContentGroup() {
		super("group");
		setAttribute("items", items);
	}
	
	public void add(ContentItem item) {		
	  items.add(item);
	}

	public void useGroupBoarder() {
	  setAttribute("groupBoarder", true);
	}
	
	public void setOnClickParameter(String parameter) {
	  setAttribute("onClickParameter", parameter);
	}
}
