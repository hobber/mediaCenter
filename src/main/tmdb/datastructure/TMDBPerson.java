package main.tmdb.datastructure;

import java.util.LinkedList;

import main.http.HTTPResponse;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentObject;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.tmdb.TMDB;
import main.tmdb.TMDBRequest;
import main.utils.JSONContainer;

public class TMDBPerson implements ContentObject {
	
	private String name;
	private String profilePath;
	private String homepage;
	private String birthday;
	private String deathday;
	private String placeOfBirth;
	private String biography;
	
	public TMDBPerson(int id) {
		TMDBRequest request = new TMDBRequest("person/" + id);
		request.addQuery("language", "de");
		TMDB.signRequest(request);
		System.out.println("request: " + request.toString());
		HTTPResponse response = request.sendRequest();
		if(response.failed() || response.hasJSONBody() == false)
			throw new RuntimeException("failed to load information about person with ID " + id);
				
		JSONContainer container = response.getJSONBody();
		name = container.getString("name", null);
		if(name == null)
			throw new RuntimeException("failed to load information about person with ID " + id);
		
		profilePath = container.getString("profile_path", "");
		homepage = container.getString("homepage", "");
		birthday = container.getString("birthday", "");
		deathday = container.getString("deathday", "");
		placeOfBirth = container.getString("place_of_birth", "");
		biography = container.getString("biography", "");		
	}

	@Override
  public ContentPage getPage(String context) {
		ContentPage page = new ContentPage();
		ContentGroup group = new ContentGroup();
		page.addContentGroup(group);
		
		group.put(new ContentImage(0, 0, 300, 450, TMDB.getPosterURL(profilePath, false)));
		
		int x = 330, y = 20, stepY = 23;
		group.put(new ContentText(x, y, name, ContentText.TextType.TITLE));
	  return page;
  }

}
