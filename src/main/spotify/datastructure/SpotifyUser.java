package main.spotify.datastructure;

import main.http.HTTPResponse;
import main.spotify.Spotify;

/**
 * {@link} https://developer.spotify.com/web-api/get-current-users-profile/
 */
public class SpotifyUser extends SpotifyElement {

	private String id;
	private String displayName;		
	
	public SpotifyUser(Spotify spotify) {	
		createAPIRequest("me");
		spotify.signAPIRequest(request);	
		
		HTTPResponse response = request.sendRequest();			
		if(response.isValid() == false)
			return;
				
		if(response.getResponseString("type").equals("user") == false)
			return;
		
		id = response.getResponseString("id");
		spotifyUri = response.getResponseString("uri");
		displayName = response.getResponseString("display_name");
		if(displayName.equals("null"))
			displayName = id;			
		isValid = true;					
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		if(isValid() == false)
			return "invalid user data";
		return id + " (" + displayName + ") @ " + spotifyUri;
	}
}
