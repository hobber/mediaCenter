package main.tmdb.content;

import java.util.List;

import main.server.content.ContentGroup;
import main.server.content.ContentGroupOnDemand;
import main.server.content.ContentImage;
import main.server.content.ContentSearchField;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;
import main.tmdb.TMDB;
import main.tmdb.datastructure.TMDBSearchResult;
import main.tmdb.datastructure.TMDBSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TMDBSearchPage implements UserContentPage {

	public enum Type { MOVIES, SERIES	};
	
	private TMDB tmdb;
	private Type type;
	private String context;
	
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
			menu.put(new ContentSearchField(context, 10));

			JSONArray content = new JSONArray();
			page.put("content", content);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		return page;
  }
	
	@Override
	public void setGroup(UserContentGroup group) {
		context = group.getName() + ".Search";
	}
	
	@Override
	public JSONObject handle(String query) {
		
		int index = query.indexOf("=");
		if(index < 0) {
			System.err.println("ERROR: invalid search query " + query);
			return new JSONObject();
		}
		
		String task = query.substring(0, index);
		String term = query.substring(index+1);		
		if(task.equals("search") == true)
			return search(term);
		if(task.equals("show") == true)
			return show(term);
		
		System.err.println("ERROR: " + query + " is not supported");
		return new JSONObject();
	}
	
	private JSONObject search(String searchTerm) {		
		if(searchTerm.length() == 0) {
			System.err.println("ERROR: empty search query is not allowed");
			return new JSONObject(); 
		}
		
		List<TMDBSearchResult> results;
		if(type == Type.SERIES)
			results = tmdb.searchSeries(searchTerm);		
		else
			throw new RuntimeException("search for movies is currently not implemented");
		
		JSONObject page = new JSONObject();			
		try {			
			JSONArray content = new JSONArray();
			page.put("content", content);
						
			for(TMDBSearchResult result : results) {		
				ContentGroup group = new ContentGroup();
				content.put(group);
				//if(result.getPosterPath() != null && result.getPosterPath().length() > 0)
					group.put(new ContentImage(0, 0, 100, 150, TMDB.getPosterURL(result.getPosterPath(), true)));
				group.put(new ContentText(120, 20, result.getDescription()));	
				group.putContentGroupOnDemand(new ContentGroupOnDemand(context, "show="+result.getId()));
			}
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		return page;
	}
	
	private JSONObject show(String id) {
		if(id.length() == 0) {
			System.err.println("ERROR: empty id is not allowed");
			return new JSONObject(); 
		}
		
		TMDBSeries series = tmdb.getSeries(Integer.parseInt(id));
		
		// tv/{id}/similar, videos, credits
		
		JSONObject page = new JSONObject();			
		try {			
			JSONArray content = new JSONArray();
			page.put("content", content);					
		  content.put(series.getContentGroup());			
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		return page;
	}
}
