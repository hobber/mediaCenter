package main.server.content;

import java.util.List;


public class ContentPage extends ContentItem {

	private ContentItemList content = new ContentItemList();
	
	public ContentPage(ContentLocation location) {
	  super("page");
	  setAttribute("location", location);
	  setAttribute("page", content);
	}
	
	public ContentPage(ContentLocation location, String title) {
	  super("page");
	  setAttribute("location", location);
	  setAttribute("titlebar", new ContentTitleBar(title));
	  setAttribute("page", content);
	}
	
	public ContentPage(ContentLocation location, ContentTitleBar titleBar) {
    super("page");
    setAttribute("location", location);
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
