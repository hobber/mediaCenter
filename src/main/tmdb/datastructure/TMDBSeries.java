package main.tmdb.datastructure;

import main.http.HTTPResponse;
import main.tmdb.TMDBRequest;

import org.json.JSONArray;

public class TMDBSeries {

	private String name;
	private TMDBGenreList genreList;
	private int numberOfSeasons;
	private int numberOfEpisodes;
	private boolean completed;
	private double popularity;
	private TMDBPoster posterPath;
	private String summary;
	
	public static TMDBRequest createRequest(int id) {
		TMDBRequest request = new TMDBRequest("tv/"+id);
		request.addQuery("language", "de");
		return request;
	}
	
	public TMDBSeries(HTTPResponse series) {
		name = series.getResponseString("name");					
		genreList = new TMDBGenreList(series.getJSONArray("genres"));
		numberOfSeasons = series.getResponseInt("number_of_seasons", 0);
		numberOfEpisodes = series.getResponseInt("number_of_episodes", 0);
		completed = series.getResponseBoolean("in_production", true);
		popularity = series.getResponseDouble("popularity", 0.0);
		posterPath = new TMDBPoster(series.getResponseString("poster_path"));
		summary = series.getResponseString("overview");		
	}
	
	public int numberOfSeasons() {
		return numberOfSeasons;
	}
	
	@Override
	public String toString() {
		String s = "Series:     " + name;
		s     += "\nGenres:     " + genreList.toString();
		s     += "\nSeasons:    " + numberOfSeasons;
		s     += "\nEpisodes:   " + numberOfEpisodes;
		s     += "\nCompleted:  " + (completed ? "yes" : "no");
		s     += "\nPopularity: " + String.format("%.2f",popularity);
		s     += "\nSummary:    " + summary;
		return s;
	}
}
