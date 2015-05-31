package main.server.content;

import java.util.LinkedList;

public class ContentTitleBar {

  LinkedList<ContentItem> data = new LinkedList<ContentItem>();
  
	public void addContentItem(ContentItem item) {
		data.add(item);
	}
	
	public void addBackMenu() {
		data.add(new ContentBackButton(8));
	}
	
	public void addSearchMenu(String context) {
		data.add(new ContentSearchField(context, 4));
	}
	
	@Override
	public String toString() {
	  String s = "[";
    for(int i=0; i<data.size(); i++)
      s += (i > 0 ? ", " : "") + data.get(i).getContentString(); 
    return s + "]";
	}
}
