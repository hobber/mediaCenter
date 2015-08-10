package main.tmdb.datastructure;

import java.util.LinkedList;

import main.http.HTTPResponse;
import main.server.content.ContentGroup;
import main.server.content.ContentObject;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.tmdb.TMDB;
import main.tmdb.TMDBRequest;
import main.utils.JSONArray;
import main.utils.JSONContainer;

public class TMDBEpisodeList implements ContentObject {
	
	private LinkedList<LinkedList<TMDBEpisode>> episodes = new LinkedList<LinkedList<TMDBEpisode>>(); 

	public TMDBEpisodeList(int id) {		
		for(int season=1; ; season++) {
			TMDBRequest request = new TMDBRequest("tv/" + id + "/season/" + season);
			request.addQuery("language", "de");
			TMDB.signRequest(request);
			HTTPResponse response = request.sendRequest();
			if(response.failed() || response.hasJSONBody() == false)
				break;
			
			LinkedList<TMDBEpisode> list = new LinkedList<TMDBEpisode>();
			episodes.add(list);
			JSONContainer container = response.getJSONBody();
			JSONArray episodes = container.getArray("episodes");
			for(int i=0; i<episodes.length(); i++)
				list.add(new TMDBEpisode(episodes.getContainer(i)));			
		}
	}
	
	@Override
  public ContentPage getPage(String context) {
	  ContentPage page = new ContentPage();
	  for(int i=0; i<episodes.size(); i++) {
	  	LinkedList<TMDBEpisode> season = episodes.get(i);
	  	ContentGroup group = new ContentGroup();
	  	page.addContentGroup(group);
	  	group.add(new ContentText(5, 5, "Staffel " + (i+1), ContentText.TextType.SUBTITLE));
	  	
	  	for(int j=0; j<season.size(); j++) {	  		
	  		page.addContentGroup(season.get(j).getContentGroup(context));
	  	}
	  }
	  return page;
  }
	
}
