package main.server.content;


public interface UserContentPage {

	public String getName();
	public ContentPage getPage();
	public void setGroup(UserContentGroup group);
	public ContentPage handle(String query);	
}
