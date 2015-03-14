package main.tmdb.datastructure;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;

public class TMDBGenreList {

	private Vector<String> genreList = new Vector<String>(); 
	
	public TMDBGenreList(JSONArray genres) {
		add(genres);
	}
	
	public void add(JSONArray genres) {		
		for(int i=0; i<genres.length(); i++) {
			try {
				genreList.add(genres.getJSONObject(i).getString("name"));
			} catch(JSONException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public void add(String genre) {
		genreList.add(genre);
	}
	
	@Override
	public String toString() {
		String s = "";
		for(int i=0; i<genreList.size(); i++)
			s += (i > 0 ? ", " : "") + genreList.get(i);
		return s;
	}
}
