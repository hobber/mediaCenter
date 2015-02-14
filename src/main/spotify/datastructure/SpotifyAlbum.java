package main.spotify.datastructure;

import java.util.Vector;

import main.http.HTTPResponse;
import main.spotify.Spotify;

import org.json.JSONArray;
import org.json.JSONException;

public class SpotifyAlbum extends SpotifyElement {
	
	private String albumName;
	private Vector<SpotifyArtist> artists = new Vector<SpotifyArtist>();
	private int popularity;
	private String releaseDate;
	private String tracksUrl;
	
	public SpotifyAlbum(Spotify spotify, String albumId) {		
		createAPIRequest("albums/" + albumId);		
		HTTPResponse response = request.sendRequest();	
		if(response.isValid() == false)
			return;
		
		if(response.getResponseSubString("type").equals("album") == false)
			return;
		
		spotifyUri = response.getResponseString("id");
		albumName = response.getResponseString("name");
		popularity = response.getResponseInt("popularity", 0);
		releaseDate = response.getResponseString("release_date");
		tracksUrl = response.getResponseSubString("tracks.href");	
		
		try {
			JSONArray artists = response.getJSONArray("artists");
			for(int i=0; i<artists.length(); i++)
				this.artists.add(new SpotifyArtist(artists.getJSONObject(i)));
		} catch(JSONException e) {
			e.printStackTrace();
			return;
		}
		
		isValid = true;
	}
	
	@Override
	public String toString() {
		String s = albumName + " - ";
		for(int i=0; i<artists.size(); i++)
			if(i == 0)
				s += artists.get(i).getName();
			else
				s += ", " + artists.get(i).getName();
		return s + " @ " + spotifyUri;
	}
}
