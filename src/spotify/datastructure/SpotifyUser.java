package spotify.datastructure;

import org.json.JSONObject;

import spotify.Spotify;

/**
 * {@link} https://developer.spotify.com/web-api/get-current-users-profile/
 */
public class SpotifyUser extends SpotifyElement {

	private String id;
	private String displayName;		
	
	public SpotifyUser(Spotify spotify) {	
		createAPIRequest("me");
		spotify.signAPIRequest(request);	
		
		JSONObject user = request.sendRequest();			
		if(user == null)
			return;
				
		if(getResponseString(user, "type").equals("user") == false)
			return;
		
		id = getResponseString(user, "id");
		spotifyUri = getResponseString(user, "uri");
		displayName = getResponseString(user, "display_name");
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
