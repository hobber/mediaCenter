package main.tmdb.datastructure;

import java.util.LinkedList;
import java.util.List;

import main.server.content.ContentGroup;
import main.server.content.ContentGroupOnDemand;
import main.server.content.ContentImage;
import main.server.content.ContentObject;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.tmdb.TMDB;
import main.utils.JSONArray;
import main.utils.JSONContainer;

public class TMDBSearchResultList implements ContentObject {

	private List<TMDBSearchResult> resultList = new LinkedList<TMDBSearchResult>();
	
	public TMDBSearchResultList(JSONArray results) {		
		for(int i=0; i<results.length(); i++) {
			JSONContainer series = results.getContainer(i);
			Integer id = series.getInt("id", null);
			String name = series.getString("name", null);
			String airDate = series.getString("first_air_date", null);
			String posterPath = series.getString("poster_path", "");
			
			if(id == null || name == null) {
				System.err.println("TMDB: failed to read a search results");
				continue;
			}
			
			resultList.add(new TMDBSearchResult(id, name + " (" + TMDB.getYear(airDate) + ")", posterPath));
		}
  }
	
	@Override
  public ContentPage getPage(String context) {
		if(context == null || context.length() == 0)
			throw new RuntimeException("no valid context provided");
		
		ContentPage page = new ContentPage();
		for(TMDBSearchResult result : resultList) {		
			ContentGroup group = new ContentGroup();
			page.addContentGroup(group);
			group.put(new ContentImage(0, 0, 100, 150, TMDB.getPosterURL(result.getPosterPath(), true)));
			group.put(new ContentText(120, 20, result.getDescription()));	
			group.appendLink(new ContentGroupOnDemand(context, "show="+result.getId()));
		}
		return page;
  }
}
