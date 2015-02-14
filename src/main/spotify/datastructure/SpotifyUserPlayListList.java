package main.spotify.datastructure;

import java.util.LinkedList;
import java.util.List;

import main.http.HTTPResponse;
import main.spotify.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpotifyUserPlayListList extends SpotifyPagedElement {
	
	private List<SpotifyPlayList> playLists = new LinkedList<SpotifyPlayList>(); 
	
	public SpotifyUserPlayListList(Spotify spotify) {		
		String userID = spotify.getCurrentUser().getId();
		createAPIRequest("users/"+userID+"/playlists");		
		spotify.signAPIRequest(request);			
		if(addAPIResponse(spotify, request.sendRequest()) == true)	
			isValid = true;
	}
	
	private boolean addAPIResponse(Spotify spotify, HTTPResponse playList) {
		if(playList.isValid() == false)
			return false;
		
		if(handleAPIResponse(playList) == false)
			return false;
		
		try {
			JSONArray items = playList.getJSONArray("items");
			for(int i=0; i<items.length(); i++)
			{
				SpotifyPlayList item = new SpotifyPlayList(spotify, items.getJSONObject(i));				
				playLists.add(item);
			}
			return true;
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int size() {
		return playLists.size();
	}
	
	public SpotifyPlayList getPlayList(int index) {		
		return playLists.get(index);
	}
}
