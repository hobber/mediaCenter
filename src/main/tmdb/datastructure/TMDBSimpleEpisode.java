package main.tmdb.datastructure;

import org.json.JSONException;
import org.json.JSONObject;

public class TMDBSimpleEpisode {

	private String name;
	private int seasonNumber;
	private int episodeNumber;
	private String airDate;
	private String stillPath;
	private String overview;
	private double averageVote;
	
	public TMDBSimpleEpisode(JSONObject episode) {
    try {
    	name = episode.getString("name");
    	seasonNumber = episode.getInt("season_number");
    	episodeNumber = episode.getInt("episode_number");
    	airDate = episode.getString("air_date");    	
    	overview = episode.getString("overview");
    	averageVote = episode.getDouble("vote_average");
    	stillPath = episode.getString("still_path");    		
    } catch(JSONException e) {
    	System.err.println(e.getMessage());
    }
	}
	
	@Override
	public String toString() {
		return String.format("%02d: %s (%s)\n", episodeNumber, name, airDate);
	}
}
