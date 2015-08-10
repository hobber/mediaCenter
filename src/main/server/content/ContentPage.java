package main.server.content;

import java.util.List;


public class ContentPage extends ContentItem {

	private ContentItemList content = new ContentItemList();
	
	public ContentPage() {
	  super("page");
	}
	
	public ContentPage(String title) {
	  super("page");
	  setAttribute("titlebar", new ContentTitleBar(title));
	  setAttribute("page", content);
	}
	
	public ContentPage(ContentTitleBar titleBar) {
    super("page");
    setAttribute("titlebar", titleBar);
    setAttribute("page", content);
  }
	
	public ContentGroup createContentGroup() {
	  ContentGroup group = new ContentGroup();
	  content.add(group);
	  return group;
	}
	
	public void addContentGroup(ContentGroup group) {				
		content.add(group);
	}
	
	public void addContentGroups(List<ContentGroup> groups) {
	  for(ContentGroup group : groups)
	    content.add(group);
	}
}
