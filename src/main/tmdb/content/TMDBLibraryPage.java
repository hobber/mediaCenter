package main.tmdb.content;

import main.server.content.ContentBackButton;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentTitleBar;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;

public class TMDBLibraryPage implements UserContentPage {	
	
	public TMDBLibraryPage() {
		
	}

	@Override
	public String getName() {
		return "Library";
	}
	
	@Override
  public ContentPage getPage() {
		ContentPage page = new ContentPage();			
		ContentTitleBar menu = new ContentTitleBar();
		page.setTitleBar(menu);
		menu.addFixedWidthText(new ContentBackButton(0));

		ContentGroup series = new ContentGroup();		
		page.addContentGroup(series);

		series.add(new ContentText(5, 5, "Once upon a time"));
		ContentGroup season = new ContentGroup();
		series.appendLink(season);
		season.add(new ContentText(5, 5, "Season 01"));		
		ContentGroup episode = new ContentGroup();
		season.appendLink(episode);
		ContentImage image = new ContentImage(0, 0, 100, 120, "http://image.tmdb.org/t/p/w500/6S8rM2Qq3B3g3dgAnJlilgUc2dE.jpg");
		episode.add(image);

		return page;
  }
	
	@Override
	public void setGroup(UserContentGroup group) {		
	}
	

	@Override
	public ContentPage handle(String query) {
		System.err.println("ERROR: TMDBLibraryPage does not support any queries");
		return new ContentPage();
	}
}
