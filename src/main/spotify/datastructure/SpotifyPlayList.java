package main.spotify.datastructure;

import java.util.LinkedList;
import java.util.List;

import main.http.HTTPResponse;
import main.spotify.Spotify;
import main.spotify.SpotifyAPIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link} https://developer.spotify.com/web-api/object-model/#playlist-object-simplified
 */
public class SpotifyPlayList extends SpotifyPagedElement {
	
	private String name;
	private String ownerId;
	private String tracksUrl;
	private boolean isCollaborative;
	private boolean isPublic;
	private List<SpotifySong> songs = new LinkedList<SpotifySong>();
	
	public SpotifyPlayList(Spotify spotify, JSONObject playList) {
		if(getResponseString(playList, "type").equals("playlist") == false)
			return;
		
		spotifyUri = getResponseString(playList, "uri");
		name = getResponseString(playList, "name");		
		tracksUrl = getResponseSubString(playList, "tracks.href");
		ownerId = getResponseSubString(playList, "owner.id");		
		isCollaborative = getResponseBoolean(playList, "collaborative", false);
		isPublic = getResponseBoolean(playList, "public", false);
		
		if(loadSongs(spotify) == true)
			isValid = true;
	}
	
	private boolean loadSongs(Spotify spotify) {
		String path = tracksUrl.substring(SpotifyAPIRequest.API_URL.length(), tracksUrl.length());
		createAPIRequest(path);
		spotify.signAPIRequest(request);
		
		HTTPResponse response = request.sendRequest();			
		if(response.isValid() == false)
			return false;
		
		try {
			JSONArray items = response.getJSONArray("items");
			for(int i=0; i<items.length(); i++) {
				SpotifySong song = new SpotifySong(items.getJSONObject(i));				
				songs.add(song);
			}
			return true;
		} catch(JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int size() {
		return songs.size();
	}
	
	public SpotifySong getSong(int index) {
		return songs.get(index);
	}
	
	@Override
	public String toString() {
		return name + " (" + ownerId + ": " + tracksUrl + ") @ " + spotifyUri;
	}

}
