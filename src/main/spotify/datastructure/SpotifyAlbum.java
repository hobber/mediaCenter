package main.spotify.datastructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import main.spotify.Spotify;
import main.spotify.SpotifyAPIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpotifyAlbum extends SpotifyElement {
	
	private String albumName;
	private Vector<SpotifyArtist> artists = new Vector<SpotifyArtist>();
	private int popularity;
	private String releaseDate;
	private String tracksUrl;
	
	public SpotifyAlbum(Spotify spotify, String albumId) {		
		createAPIRequest("albums/" + albumId);		
		JSONObject album = request.sendRequest();	
		
		if(getResponseSubString(album, "type").equals("album") == false)
			return;
		
		spotifyUri = getResponseString(album, "id");
		albumName = getResponseString(album, "name");
		popularity = getResponseInt(album, "popularity", 0);
		releaseDate = getResponseString(album, "release_date");
		tracksUrl = getResponseSubString(album, "tracks.href");	
		
		try {
			JSONArray artists = album.getJSONArray("artists");
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
