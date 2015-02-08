package spotify.datastructure;

import org.json.JSONObject;

public class SpotifyArtist extends SpotifyElement {

	private String name;
	
	public SpotifyArtist(JSONObject artist) {
		if(getResponseString(artist, "type").equals("artist") == false)
			return;
		
		spotifyUri = getResponseString(artist, "uri");
		name = getResponseString(artist, "name");
		isValid = true;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name + "@" + spotifyUri;
	}
}
