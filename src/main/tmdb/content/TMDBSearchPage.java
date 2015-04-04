package main.tmdb.content;

import main.server.content.ContentBackButton;
import main.server.content.ContentErrorPage;
import main.server.content.ContentGroup;
import main.server.content.ContentGroupOnDemand;
import main.server.content.ContentMenu;
import main.server.content.ContentOptions;
import main.server.content.ContentPage;
import main.server.content.ContentSearchField;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;
import main.tmdb.TMDB;
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
  public ContentPage getPage() {
		ContentPage page = new ContentPage();			
		page.setMenu(ContentMenu.createSearchMenu(context));		
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
		if(task.equals("show") == true) {
			if(type == Type.SERIES)
				return showSeries(term);
			else
				return new ContentErrorPage("movies are currently not supported");
		}
		if(task.equals("cast") == true)
			return credits(term);
		if(task.equals("similar") == true)
			return similar(term);
		
		return new ContentErrorPage(query + " is not supported");
	}
	
	private ContentPage search(String searchTerm) {		
		if(searchTerm.length() == 0)
			return new ContentErrorPage("empty search query is not allowed");		
		
		if(type == Type.MOVIES)
			return new ContentErrorPage("search for movies is currently not implemented");
		
		TMDBSearchResultList results = tmdb.searchSeries(searchTerm);
		ContentPage page = results.getPage(context);
		page.setMenu(ContentMenu.createSearchMenu(context));
		return page;		
	}
	
	private ContentPage showSeries(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed"); 		
		
		int seriesId = Integer.parseInt(id);
		TMDBSeries series = tmdb.getSeries(seriesId);		
		
		ContentPage page = series.getPage(null);		
		try {						
			page.setMenu(ContentMenu.createBackMenu());
			
			ContentOptions options = new ContentOptions();
			page.setOptions(options);
			options.put("groupBoarder", false);
			
		  ContentGroup infos = new ContentGroup();
		  page.addContentGroup(infos);		
			infos.put(new ContentText(10, 10, "Weiter Informationen:", ContentText.TextType.SUBTITLE));			
		  
			ContentGroup cast = new ContentGroup();
			page.addContentGroup(cast);			
			cast.put(new ContentText(20, 10, "&bull;Besetzung"));				
			cast.putContentGroupOnDemand(new ContentGroupOnDemand(context, "cast="+id));
			
			ContentGroup similar = new ContentGroup();
			page.addContentGroup(similar);
			similar.put(new ContentText(20, 5, "&bull;Ähnliche Serien"));				
			similar.putContentGroupOnDemand(new ContentGroupOnDemand(context, "similar="+id));			
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		return page;
	}
	
	private ContentPage credits(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed");		
		
		if(type == Type.SERIES)
			return tmdb.getSeriesCredits(Integer.parseInt(id)).getPage(null);
		else
			return new ContentErrorPage("movies are currently not supported"); 					
	}
	
	private ContentPage similar(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed");		
		
		if(type == Type.SERIES)
			return tmdb.getSimilarSeries(Integer.parseInt(id)).getPage(context);
		else
			return new ContentErrorPage("movies are currently not supported"); 							
	}
}
