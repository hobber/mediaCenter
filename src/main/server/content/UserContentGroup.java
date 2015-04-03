package main.server.content;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

public class UserContentGroup {

	private String name;
	private String iconPath;
	private HashMap<String, UserContentPage> pages = new HashMap<String, UserContentPage>();
	private LinkedList<UserContentPage> pageList = new LinkedList<UserContentPage>();
	
	public UserContentGroup(String name, String iconPath) {
		this.name = name;	
		this.iconPath = iconPath;
	}
	
	public void addPage(UserContentPage page) {
		pages.put(page.getName(), page);
		pageList.add(page);
		page.setGroup(this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getIconPath() {
		return iconPath;
	}
	
	public List<UserContentPage> getContentPages() {		
		return pageList;
	}
	
	public UserContentPage getContentPage(String name) {
		return pages.get(name);
	}
	
	public JSONObject handle(String pageName, String query) {
		UserContentPage page = pages.get(pageName);
		if(page == null) {
			System.err.println("ERROR: group " + name + " has no page with name " + pageName);
			return new JSONObject();
		}
		return page.handle(query);
	}
}