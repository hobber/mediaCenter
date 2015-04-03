package main.tmdb.datastructure;

import java.util.HashMap;
import java.util.Vector;

import main.tmdb.TMDBRequest;
import main.utils.JSONArray;
import main.utils.JSONContainer;

public class TMDBGenreList {

	private HashMap<Integer, String> genreList = new HashMap<Integer, String>(); 
	
	public static TMDBRequest createRequestMovie() {
		TMDBRequest request = new TMDBRequest("genre/movie/list");
		request.addQuery("language", "de");
		return request;
	}
	
	public static TMDBRequest createRequestSeries() {
		TMDBRequest request = new TMDBRequest("genre/tv/list");
		request.addQuery("language", "de");
		return request;
	}
	
	public TMDBGenreList() {		
	}
	
	public void add(JSONArray genres) {		
		for(int i=0; i<genres.length(); i++) {			
			JSONContainer genre = genres.getContainer(i);
			Integer id = genre.getInt("id", null);
			String name = genre.getString("name", null);
			if(id == null || name == null) {
				System.err.println("ERROR: invalid genre " + genre);
				continue;
			}
			genreList.put(id, name);							
		}
	}	
	
	public String get(Integer id) {
		return genreList.get(id);
	}
	
	@Override
	public String toString() {
		String s = "";
		boolean first = true;
		for(Integer id : genreList.keySet()) {
			s += (first? "" : ", ") + id + ": " + genreList.get(id);
			first = false;
		}
		return s;
	}
}
