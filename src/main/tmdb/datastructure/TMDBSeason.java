package main.tmdb.datastructure;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.http.HTTPResponse;
import main.tmdb.TMDBRequest;

public class TMDBSeason {
	
	private String airDate;
	private String name;
	private String overview;
	private String posterPath;
	private int seasonNumber;
	private Vector<TMDBSimpleEpisode> episodeList = new Vector<TMDBSimpleEpisode>();
	
	public static TMDBRequest createRequest(int seriesId, int season) {		
		TMDBRequest request = new TMDBRequest("tv/"+seriesId+"/season/"+season);
		request.addQuery("language", "de");
		return request;
	}
	
	public TMDBSeason(HTTPResponse season) {
		airDate = season.getResponseString("air_date");
		name = season.getResponseString("name");
		overview = season.getResponseString("overview");
	  posterPath = season.getResponseString("poster_path");
	  seasonNumber = season.getResponseInt("season_number", 0);
	  
		try {
			JSONArray episodes = season.getJSONArray("episodes");			
			for(int i=0; i<episodes.length(); i++)
				episodeList.addElement(new TMDBSimpleEpisode(episodes.getJSONObject(i)));				
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public String toString() {
		String s = name + ":" + "\n";
		for(TMDBSimpleEpisode episode : episodeList)
			s += "    " + episode;
		return s;
	}
}
