package main.tmdb.content;

import main.server.content.ContentErrorPage;
import main.server.content.ContentGroup;
import main.server.content.ContentGroupOnDemand;
import main.server.content.ContentMenu;
import main.server.content.ContentOptions;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;
import main.tmdb.TMDB;
import main.tmdb.datastructure.TMDBEpisodeList;
import main.tmdb.datastructure.TMDBPerson;
import main.tmdb.datastructure.TMDBSearchResultList;
import main.tmdb.datastructure.TMDBSeries;

import org.json.JSONException;

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
		if(task.equals("episodes") == true)
			return episodes(term);
		if(task.equals("person") == true)
			return person(term);
		
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
		  	
			ContentText cast = new ContentText(20, 43, "&bull;Besetzung");
			infos.put(cast);
			cast.appendLink(new ContentGroupOnDemand(context, "cast="+id));
						
			ContentText similar = new ContentText(20, 66, "&bull;Ähnliche Serien");
			infos.put(similar);
			similar.appendLink(new ContentGroupOnDemand(context, "similar="+id));
			
			ContentText episodes = new ContentText(20, 89, "&bull;Episoden");
			infos.put(episodes);
			episodes.appendLink(new ContentGroupOnDemand(context, "episodes="+id));			
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		return page;
	}
	
	private ContentPage credits(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed");		
		
		if(type == Type.SERIES)
			return tmdb.getSeriesCredits(Integer.parseInt(id)).getPage(context);
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
	
	private ContentPage episodes(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed");		
		
		if(type == Type.SERIES)
			return new TMDBEpisodeList(Integer.parseInt(id)).getPage(context);
		else
			return new ContentErrorPage("movies are currently not supported"); 				
	}
	
	private ContentPage person(String id) {
		if(id.length() == 0)
			return new ContentErrorPage("empty id is not allowed");				
		return new TMDBPerson(Integer.parseInt(id)).getPage(context); 				
	}
}
