package main.spotify;

import java.util.Map;

import main.http.HTTPListener;
import main.oauth.Token;
import main.spotify.datastructure.SpotifyAlbum;
import main.spotify.datastructure.SpotifyUser;
import main.spotify.datastructure.SpotifyUserPlayListList;
import main.utils.XMLFile;

public class Spotify extends HTTPListener {

	private static final String URL_PATH = "spotify";
	private static final String CODE_PARAMETER = "code";
	private static final String SCOPE = "user-read-private";
	
	private String clientID;
	private String clientSecret;
	private String redirectURI;
	private String code;
	private OAuthAPISpotify oauthSpotify;
	private Token authorizationToken;
	
	private SpotifyUser currentUser = null;
	
	public Spotify(XMLFile config) {
	  super(URL_PATH);	  
	  readConfig(config);
  }
	
	private boolean readConfig(XMLFile config) {
		clientID = config.getString("config.spotify.clientID", "");
		clientSecret = config.getString("config.spotify.clientSecret", "");
		redirectURI = config.getString("config.spotify.redirectURI", "");		
				
		if(clientID.length() == 0 || clientSecret.length() == 0 || redirectURI.length() == 0) {
			System.out.println("PLEASE STORE CLIENT ID, CLIENT SECRET AND REDIRECT URI IN CONFIG FILE!");			
			return false;
		}
		
		String accessToken = config.getString("config.spotify.authorization.accessToken", "");
		String refreshToken = config.getString("config.spotify.authorization.refreshToken", "");
		String expirationTime = config.getString("config.spotify.authorization.expirationTime", "");
		
		if(accessToken.length() != 0 && refreshToken.length() != 0 && expirationTime.length() != 0) {
			authorizationToken = new Token(accessToken, refreshToken, expirationTime);						
		}
		
		return true;
	}
	
	public void storeConfig(XMLFile config) {
		config.add("config.spotify.clientID", clientID);
		config.add("config.spotify.clientSecret", clientSecret);
		config.add("config.spotify.redirectURI", redirectURI);
		if(authorizationToken != null && authorizationToken.isValid()) { 
			config.add("config.spotify.authorization.accessToken", authorizationToken.getAccessToken());
			config.add("config.spotify.authorization.refreshToken", authorizationToken.getRefreshToken());
			config.add("config.spotify.authorization.expirationTime", authorizationToken.getExpirationTime());
		}
	}
	
	public boolean hasValidAuthorizationToken() {
		return authorizationToken != null && authorizationToken.isValid();
	}
	
	public String getAuthorizationRequestURL() {
		oauthSpotify = new OAuthAPISpotify(clientID, clientSecret, redirectURI, SCOPE);		
		return oauthSpotify.getAuthorizationUrl();		
	}
	
	/**
	 * requests refresh and access tokens, is called when code was received
	 */
	private void sendAccessRequest() {	
		authorizationToken = oauthSpotify.getAuthorizationToken(code);
    if(authorizationToken.failed())
    {
    	System.err.println("ERROR: "+authorizationToken.getError());
    	return;
    }
    else
    	System.out.println("Token will expire @ "+authorizationToken.getExpirationTime());
    /*
    Token refreshToken = oauthSpotify.getRefreshToken(authorizationToken);
    if(refreshToken.failed())
    {
    	System.err.println("ERROR: "+refreshToken.getError());
    	return;
    }
    else
    	System.out.println("Token will expire @ "+refreshToken.getExpirationTime());
    */
	}
	
	public boolean isReady() {
		return authorizationToken != null && authorizationToken.isValid();
	}
	
	public SpotifyUser getCurrentUser() {
		if(currentUser == null)
			currentUser = new SpotifyUser(this); 
		return currentUser;
	}
	
	public SpotifyUserPlayListList getUserPlayListList() {			
		return new SpotifyUserPlayListList(this);
	}
	
	public SpotifyAlbum getAlbum(String albumId) {
		return new SpotifyAlbum(this, albumId);
	}
	
	public void signAPIRequest(SpotifyAPIRequest request) {
		request.addHeader("Authorization", "Bearer "+authorizationToken.getAccessToken());
	}

	@Override
  public String handleHTTPGetRequest(Map<String, String> parameters) {
	  if(parameters.containsKey(CODE_PARAMETER) == false) {
	  	return "<html><body>please visit <a href=\"" +getAuthorizationRequestURL() + "\">this</a> to get authorization code.</body></html>";
	  }
	  
	  code = parameters.get(CODE_PARAMETER);
	  sendAccessRequest();
	  return "received code sucessfully";
  }
}
