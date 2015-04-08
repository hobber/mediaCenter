package main.tmdb.datastructure;

import java.util.Collections;
import java.util.LinkedList;

import main.server.content.ContentGroup;
import main.server.content.ContentGroupOnDemand;
import main.server.content.ContentImage;
import main.server.content.ContentItem;
import main.server.content.ContentObject;
import main.server.content.ContentPage;
import main.server.content.ContentTable;
import main.server.content.ContentText;
import main.tmdb.TMDB;
import main.tmdb.TMDBRequest;
import main.utils.JSONArray;
import main.utils.JSONContainer;

public class TMDBCredits implements ContentObject {

	public enum Type {MOVIE, SERIES};	
	
	private LinkedList<TMDBCharacter> characters = new LinkedList<TMDBCharacter>();
	
	public static TMDBRequest createRequest(int id, Type type) {
		TMDBRequest request;
		if(type == Type.MOVIE)
			request = new TMDBRequest("movie/"+id+"/credits");
		else 
			request = new TMDBRequest("tv/"+id+"/credits");
		request.addQuery("language", "de");
		return request;
	}
	
	public TMDBCredits(JSONContainer cast) {		
		JSONArray array = cast.getArray("cast");
		for(int i=0; i<array.length(); i++)
			characters.push(new TMDBCharacter(array.getContainer(i)));
		Collections.sort(characters, TMDBCharacter.COMPARATOR);
	}

	@Override
  public ContentPage getPage(String context) {	
		ContentPage page = new ContentPage();
	  ContentGroup group = new ContentGroup();
	  page.addContentGroup(group);
	  
	  group.put(new ContentText(10, 5, "Besetzung:", ContentText.TextType.SUBTITLE));	  
	  
	  ContentTable table = new ContentTable(10, 35, 3, 60);
	  group.put(table);
	  for(TMDBCharacter character : characters) {
	  	LinkedList<ContentItem> columns = new LinkedList<ContentItem>();
	  	columns.add(new ContentImage(0, 0, 40, 60, TMDB.getPosterURL(character.getProfilePath(), true)));
	  	columns.add(new ContentText(0, 20, character.getCharacterName()));
	  	ContentText actor = new ContentText(0, 20, character.getActorName());	  	
	  	columns.add(actor);
	  	actor.appendLink(new ContentGroupOnDemand(context, "person=" + character.getActorId()));
	  	table.addRow(columns);
	  }
	  return page;
  }	
}
