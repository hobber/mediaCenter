package main.tmdb.content;

import main.server.content.ContentErrorPage;
import main.server.content.ContentGroup;
import main.server.content.ContentGroupOnDemand;
import main.server.content.ContentPage;
import main.server.content.ContentSearchField;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;
import main.tmdb.TMDB;
import main.tmdb.datastructure.TMDBCredits;
import main.tmdb.datastructure.TMDBSearchResultList;
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
	public ContentPage handle(String query) {
		
		int index = query.indexOf("=");
		if(index < 0)
			return new ContentErrorPage("invalid search query " + query);	
		
		String task = query.substring(0, index);
		String term = query.substring(index+1);		
		if(task.equals("search") == true)
			return search(term);
		if(task.equals("show") == true)
			return show(term);
		if(task.equals("cast") == true)
			return credits(term);
		
		return new ContentErrorPage(query + " is not supported");
	}
	
	private ContentPage search(String searchTerm) {		
		if(searchTerm.length() == 0)
			return new ContentErrorPage("empty search query is not allowed");		
		
		if(type == Type.MOVIES)
			return new ContentErrorPage("search for movies is currently not implemented");
		
		TMDBSearchResultList results = tmdb.searchSeries(searchTerm);
		return results.getPage(context);		
	}
	
	private ContentPage show(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed"); 		
		
		int seriesId = Integer.parseInt(id);
		TMDBSeries series = tmdb.getSeries(seriesId);
		
		// tv/{id}/similar, videos
		
		ContentPage page = series.getPage("");		
		try {						
			JSONObject options = new JSONObject();
			page.setOptions(options);
			options.put("groupBoarder", false);
			
		  ContentGroup infos = new ContentGroup();
		  page.addContentGroup(infos);		
			infos.put(new ContentText(10, 10, "Weiter Informationen:", ContentText.TextType.SUBTITLE));			
		  
			ContentGroup cast = new ContentGroup();
			page.addContentGroup(cast);			
			cast.put(new ContentText(20, 10, "&bull;Besetzung"));				
			cast.putContentGroupOnDemand(new ContentGroupOnDemand(context, "cast="+id));
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		return page;
	}
	
	private ContentPage credits(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed");		
		
		TMDBCredits credits = tmdb.getSeriesCredits(Integer.parseInt(id));	
		return credits.getPage("");					
	}
}
