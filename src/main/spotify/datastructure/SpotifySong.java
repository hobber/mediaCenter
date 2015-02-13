package main.spotify.datastructure;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpotifySong extends SpotifyElement {
	
	private String songName;
	private List<SpotifyArtist> artists = new LinkedList<SpotifyArtist>();
	private String songUrl;
	private String albumId;
	private int durationMs;
	private int trackNumber;
	private int discNumber;	
	
	public SpotifySong(JSONObject song) {
		JSONObject track;
		try {
			track = song.getJSONObject("track");
		} catch(JSONException e) {
			e.printStackTrace();
			return;
		}
		
		if(getResponseSubString(track, "type").equals("track") == false)
			return;
		
		spotifyUri = getResponseString(track, "id");
		songName = getResponseString(track, "name");
		songUrl = getResponseSubString(track, "external_urls.spotify");
		albumId = getResponseSubString(track, "album.id");
		durationMs = getResponseInt(track, "duration_ms", 0);
		trackNumber = getResponseInt(track, "track_number", 0);
		discNumber = getResponseInt(track, "disc_number", 0);			
		
		try {
			JSONArray artists = track.getJSONArray("artists");
			for(int i=0; i<artists.length(); i++)
				this.artists.add(new SpotifyArtist(artists.getJSONObject(i)));
		} catch(JSONException e) {
			e.printStackTrace();
			return;
		}
		
		isValid = true;
	}
	
	public String getAlbumId() {
		return albumId;
	}
	
	@Override
	public String toString() {
		String s = "";
		for(int i=0; i<artists.size(); i++)
			if(i == 0)
				s += artists.get(i).getName();
			else if(i == 1)
				s += " feat. " + artists.get(i).getName();
			else
				s += ", " + artists.get(i).getName();
		return s + " - " + songName + " @ " + spotifyUri;
	}
}
