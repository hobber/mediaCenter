package main.server.content;

import java.util.LinkedList;
import java.util.List;

public class UserContentGroup {

	private String name;
	private String iconPath;
	private List<UserContentPage> pages = new LinkedList<UserContentPage>();
	
	public UserContentGroup(String name, String iconPath) {
		this.name = name;	
		this.iconPath = iconPath;
	}
	
	public void addPage(UserContentPage page) {
		pages.add(page);
	}
	
	public String getName() {
		return name;
	}
	
	public String getIconPath() {
		return iconPath;
	}
	
	public List<UserContentPage> getContentPages() {
		return pages;
	}
	
	public UserContentPage getContentPage(String name) {
		for(UserContentPage page : pages)
			if(page.getName().equals(name))
				return page;
		return null;
	}
}
