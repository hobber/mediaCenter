package main.server;

import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentText;
import main.server.content.UserContentPage;

import org.json.JSONArray;

public class SeriesLibraryPage implements UserContentPage {
	
	public SeriesLibraryPage() {
		
	}

	@Override
	public String getName() {
		return "Library";
	}
	
	@Override
  public JSONArray toJSON() {
		JSONArray content = new JSONArray();
		ContentGroup series = new ContentGroup(30);		
		content.put(series);
		series.put(new ContentText(5, 5, "Once upon a time"));
		ContentGroup season = new ContentGroup(30);
		series.appendSubGroup(season);
		season.put(new ContentText(5, 5, "Season 01"));		
		ContentGroup episode = new ContentGroup(120);
		season.appendSubGroup(episode);
		ContentImage image = new ContentImage(0, 0, 100, 120, "http://image.tmdb.org/t/p/w500/6S8rM2Qq3B3g3dgAnJlilgUc2dE.jpg?api_key=5a18658d75c3eb554e23c1102133c187");
		episode.put(image);
		return content;
  }

}
