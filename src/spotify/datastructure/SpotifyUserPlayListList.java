package spotify.datastructure;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spotify.Spotify;

public class SpotifyUserPlayListList extends SpotifyPagedElement {
	
	private List<SpotifyPlayList> playLists = new LinkedList<SpotifyPlayList>(); 
	
	public SpotifyUserPlayListList(Spotify spotify) {		
		String userID = spotify.getCurrentUser().getId();
		createAPIRequest("users/"+userID+"/playlists");		
		spotify.signAPIRequest(request);			
		if(addAPIResponse(spotify, request.sendRequest()) == true)	
			isValid = true;
	}
	
	private boolean addAPIResponse(Spotify spotify, JSONObject playList) {
		if(playList == null)
			return false;
		
		if(handleAPIResponse(playList) == false)
			return false;
		
		try {
			JSONArray items = playList.getJSONArray("items");
			for(int i=0; i<items.length(); i++)
			{
				SpotifyPlayList item = new SpotifyPlayList(spotify, items.getJSONObject(i));
				System.out.println(item);
				playLists.add(item);
			}
			return true;
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
