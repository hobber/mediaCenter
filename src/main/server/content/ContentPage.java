package main.server.content;

import java.util.LinkedList;

public class ContentPage extends ContentItem {

	private LinkedList<ContentGroup> groups = new LinkedList<ContentGroup>();
	private ContentOptions options;
	private ContentTitleBar titleBar;
	
	public ContentPage() {
	}
	
	public ContentPage(String title) {
	  titleBar = new ContentTitleBar();
    titleBar.addContentItem(new ContentText(5, 5, title, ContentText.TextType.TITLE));
	}
	
	public ContentGroup createContentGroup() {
	  ContentGroup group = new ContentGroup();
	  groups.add(group);
	  return group;
	}
	
	public void addContentGroup(ContentGroup group) {				
		groups.add(group);
	}
	
	public void merge(ContentPage page) {
	  for(int i=0; i<page.groups.size(); i++)
	    groups.add(page.groups.get(i));
	}
	
	public void setOptions(ContentOptions options) {
		this.options = options;
	}
	
	public void setTitleBar(ContentTitleBar menu) {
		this.titleBar = menu;
	}
	
	public String getContentString() {
	  String s = "{\"type\": \"page\", \"options\": " + options + ", \"titlebar\": " + titleBar + ", \"page\": [";
	  for(int i=0; i<groups.size(); i++)
	    s += (i > 0 ? ", " : "") + groups.get(i).getContentString(); 
	  return s + "]}";
	}
}
