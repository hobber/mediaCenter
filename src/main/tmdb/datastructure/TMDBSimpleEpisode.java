package main.tmdb.datastructure;

import main.server.content.ContentGroup;
import main.server.content.ContentText;
import main.utils.JSONContainer;

public class TMDBSimpleEpisode {

	private String name;
	private int episodeNumber;
	private String airDate;
	private String overview;
	private double averageVote;
	
	public TMDBSimpleEpisode(JSONContainer episode) {
		name = episode.getString("name", "");	
		episodeNumber = episode.getInt("episode_number", 0);
		airDate = episode.getString("air_date", "");    	
		overview = episode.getString("overview", "");
		averageVote = episode.getDouble("vote_average", 0.0);				
	}
	
	@Override
	public String toString() {
		return String.format("%02d: %s (%s)", episodeNumber, name, airDate);
	}
	
	public ContentGroup getContentGroup(String context) {
		ContentGroup group = new ContentGroup();		
		String summary = episodeNumber + ": " + name + " (" + airDate + ")";
		if(averageVote > 0)
			summary += "     Bewertung: " + averageVote;
		group.put(new ContentText(5, 5, summary));		
		group.put(new ContentText(5, 28, "Inhalt: " + overview));
		return group;
	}
}
