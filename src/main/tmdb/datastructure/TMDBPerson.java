package main.tmdb.datastructure;

import org.json.JSONException;

import main.http.HTTPResponse;
import main.server.content.ContentGroup;
import main.server.content.ContentOnClick;
import main.server.content.ContentImage;
import main.server.content.ContentObject;
import main.server.content.ContentOptions;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.tmdb.TMDB;
import main.tmdb.TMDBRequest;
import main.utils.JSONContainer;

public class TMDBPerson implements ContentObject {
	
	private int id;
	private String name;
	private String profilePath;
	private String homepage;
	private String birthday;
	private String deathday;
	private String placeOfBirth;
	private String biography;
	
	public TMDBPerson(int id) {
		this.id = id;
		
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
		
		ContentOptions options = new ContentOptions();
		page.setOptions(options);
		try {
			options.put("groupBoarder", false);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		ContentGroup person = new ContentGroup();
		page.addContentGroup(person);
		
		person.put(new ContentImage(0, 0, 300, 450, TMDB.getPosterURL(profilePath, false)));
		
		int x = 330, y = 20, stepY = 23;
		person.put(new ContentText(x, y, name, ContentText.TextType.TITLE));
		y += 2*stepY;
		
		if(birthday.length() > 0) {
			String birth = "Geburt: " + TMDB.getDate(birthday);
			if(placeOfBirth.length() > 0)
				birth += " (" + placeOfBirth + ")";
			person.put(new ContentText(x, y, birth));
			y += stepY;
		}
		
		if(deathday.length() > 0) {
			person.put(new ContentText(x, y, "Tod: " + TMDB.getDate(deathday)));
			y += stepY;
		}
				
	  if(homepage.length() > 0) {
	  	person.put(new ContentText(x, y, "Homepage: ", homepage));
	  	y += stepY;
	  }			
		
		if(biography.length() > 0)
			person.put(new ContentText(x, y+stepY, "Details: " + biography, ContentText.TextType.BLOCK));		
		
		ContentGroup infos = new ContentGroup();
	  page.addContentGroup(infos);		
		infos.put(new ContentText(10, 10, "Weiter Informationen:", ContentText.TextType.SUBTITLE));
		
		ContentText films = new ContentText(20, 43, "&bull;Filmografie");
		infos.put(films);
		films.appendLink(new ContentOnClick(context, "personCredits="+id));
	  return page;
  }

}
