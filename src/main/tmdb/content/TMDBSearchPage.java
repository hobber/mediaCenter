package main.tmdb.content;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.server.content.ContentBackButton;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentSearchField;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;
import main.tmdb.TMDB;
import main.tmdb.datastructure.TMDBSearchResult;

public class TMDBSearchPage implements UserContentPage {

	public enum Type { MOVIES, SERIES	};
	
	private TMDB tmdb;
	private Type type;
	private UserContentGroup group;
	
	public TMDBSearchPage(TMDB tmdb, Type type) {
		this.tmdb = tmdb;
		this.type = type;
  }
	
	@Override
  public String getName() {
	  return "Search";
  }

	@Override
  public JSONObject toJSON() {
		JSONObject page = new JSONObject();	
		try {
			JSONArray menu = new JSONArray();
			page.put("menu", menu);
			menu.put(new ContentSearchField(group.getName() + ".Search", 10));

			JSONArray content = new JSONArray();
			page.put("content", content);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		return page;
  }
	
	@Override
	public void setGroup(UserContentGroup group) {
		this.group = group;
	}
	
	@Override
	public JSONObject handle(String query) {
		int index = query.indexOf("=");
		if(index < 0) {
			System.err.println("ERROR: invalid search query " + query);
			return new JSONObject();
		}
		
		String searchTerm = query.substring(index+1);
		if(searchTerm.length() == 0) {
			System.err.println("ERROR: empty search query is not allowed");
			return new JSONObject(); 
		}
		
		List<TMDBSearchResult> results = tmdb.searchSeries(searchTerm);
		JSONObject page = new JSONObject();			
		try {			
			JSONArray content = new JSONArray();
			page.put("content", content);
						
			for(TMDBSearchResult result : results) {		
				ContentGroup group = new ContentGroup(150);
				content.put(group);
				if(result.getPosterPath() != null && result.getPosterPath().length() > 0)
					group.put(new ContentImage(0, 0, 100, 150, tmdb.getPosterURL(result.getPosterPath())));
				group.put(new ContentText(120, 20, result.getDescription()));						
			}
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		return page;
	}
}
