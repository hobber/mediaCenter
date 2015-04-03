package main.tmdb.content;

import main.server.content.ContentBackButton;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TMDBLibraryPage implements UserContentPage {	
	
	public TMDBLibraryPage() {
		
	}

	@Override
	public String getName() {
		return "Library";
	}
	
	@Override
  public JSONObject toJSON() {
		JSONObject page = new JSONObject();	
		try {
			JSONArray menu = new JSONArray();
			page.put("menu", menu);
			menu.put(new ContentBackButton(0));
			
			JSONArray content = new JSONArray();
			page.put("content", content);
			
			ContentGroup series = new ContentGroup(30);		
			content.put(series);
			
			series.put(new ContentText(5, 5, "Once upon a time"));
			ContentGroup season = new ContentGroup(30);
			series.appendSubGroup(season);
			season.put(new ContentText(5, 5, "Season 01"));		
			ContentGroup episode = new ContentGroup(120);
			season.appendSubGroup(episode);
			ContentImage image = new ContentImage(0, 0, 100, 120, "http://image.tmdb.org/t/p/w500/6S8rM2Qq3B3g3dgAnJlilgUc2dE.jpg");
			episode.put(image);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}		
		return page;
  }
	
	@Override
	public void setGroup(UserContentGroup group) {		
	}
	

	@Override
	public JSONObject handle(String query) {
		System.err.println("ERROR: TMDBLibraryPage does not support any queries");
		return new JSONObject();
	}
}
