package main.tmdb.datastructure;


import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import main.http.HTTPResponse;
import main.server.content.ContentGroup;
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

public class TMDBPersonCredits implements ContentObject {
	
	private static class SingleCredit {
		
		public static final Comparator<SingleCredit> COMPARATOR = new Comparator<SingleCredit>() {
			@Override
		  public int compare(SingleCredit lhs, SingleCredit rhs) {
				if(lhs.year == null)
					if(rhs.year == null)
						return 0;
					else
						return rhs.year;
				else
					if(rhs.year == null)
						return -lhs.year;
					else
						return rhs.year - lhs.year;
		  }
		};
		
		private String character;
		private String title;
		private String posterPath;
		private Integer year;
		
		public SingleCredit(JSONContainer credit) {
			character = credit.getString("character", "");
			title = credit.getString("name", credit.getString("title", null));
			if(title == null)
				throw new RuntimeException("failed to load a cast (" + credit + ")");
			
			posterPath = credit.getString("poster_path", "");
			year = TMDB.getYear(credit.getString("release_date", ""));
		}
		
		public String getCharacter() {
			return character;
		}
		
		public String getTitle() {
			if(year != null)
				return title + " (" + year + ")";
			return title;
		}
		
		public String getPosterPath() {
			return posterPath;
		}		
	}
	
	private LinkedList<SingleCredit> creditList = new LinkedList<SingleCredit>(); 
	
	public TMDBPersonCredits(int id) {		
		TMDBRequest request = new TMDBRequest("person/" + id + "/combined_credits");
		request.addQuery("language", "de");
		TMDB.signRequest(request);
		System.out.println("request: " + request.toString());
		HTTPResponse response = request.sendRequest();
		if(response.failed() || response.hasJSONBody() == false)
			throw new RuntimeException("failed to load cast of person with ID " + id);
				
		JSONArray casts = response.getJSONBody().getArray("cast");
		for(int i=0; i<casts.length(); i++)
			creditList.add(new SingleCredit(casts.getContainer(i)));
		Collections.sort(creditList, SingleCredit.COMPARATOR);
	}
	
	@Override
  public ContentPage getPage(String context) {
		ContentPage page = new ContentPage();
	  ContentGroup group = new ContentGroup();
	  page.addContentGroup(group);
	  
	  group.put(new ContentText(10, 5, "Filmografie:", ContentText.TextType.SUBTITLE));	  
	  
	  ContentTable table = new ContentTable(10, 35, 3, 60);
	  group.put(table);
	  for(SingleCredit credit: creditList) {
	  	LinkedList<ContentItem> columns = new LinkedList<ContentItem>();
	  	columns.add(new ContentImage(0, 0, 40, 60, TMDB.getPosterURL(credit.getPosterPath(), true)));	  	
	  	columns.add(new ContentText(0, 20, credit.getTitle()));	  	
	  	columns.add(new ContentText(0, 20, credit.getCharacter()));
	  	table.addRow(columns);
	  }
	  return page;
	}

}
