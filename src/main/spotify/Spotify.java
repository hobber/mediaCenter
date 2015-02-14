package main.spotify;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import main.http.HTTPListener;
import main.http.HTTPServer;
import main.oauth.OAuthToken;
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
	private OAuthTokenSpotify authorizationToken;
	private int timeout;
	
	private SpotifyUser currentUser = null;
	
	public Spotify(HTTPServer server, XMLFile config) {
	  super(server, URL_PATH);	  
	  if(initialize(config) == false)
	  	return;	  
  }
	
	private boolean initialize(XMLFile config) {
		clientID = config.getString("config.spotify.clientID", "");
		clientSecret = config.getString("config.spotify.clientSecret", "");
		redirectURI = config.getString("config.spotify.redirectURI", "");	
		timeout = config.getInt("config.spotify.timeout", 120000);
				
		if(clientID.length() == 0 || clientSecret.length() == 0 || redirectURI.length() == 0) {
			System.out.println("PLEASE STORE CLIENT ID, CLIENT SECRET AND REDIRECT URI IN CONFIG FILE!");			
			return false;
		}
		oauthSpotify = new OAuthAPISpotify(clientID, clientSecret, redirectURI, SCOPE);
		
		String accessToken = config.getString("config.spotify.authorization.accessToken", "");
		String refreshToken = config.getString("config.spotify.authorization.refreshToken", "");
		String expirationTime = config.getString("config.spotify.authorization.expirationTime", "");
		
		if(accessToken.length() != 0 && refreshToken.length() != 0 && expirationTime.length() != 0) {
			authorizationToken = oauthSpotify.createAuthorizationToken(accessToken, refreshToken, expirationTime);						
		}
		
		return true;
	}
	
	public void storeConfig(XMLFile config) {
		config.add("config.spotify.clientID", clientID);
		config.add("config.spotify.clientSecret", clientSecret);
		config.add("config.spotify.redirectURI", redirectURI);
		config.add("config.spotify.timeout", timeout);
		if(authorizationToken != null && authorizationToken.isValid()) { 
			config.add("config.spotify.authorization.accessToken", authorizationToken.getAccessToken());
			config.add("config.spotify.authorization.refreshToken", authorizationToken.getRefreshToken());
			config.add("config.spotify.authorization.expirationTime", authorizationToken.getExpirationTime());
		}
	}
	
	public void getAuthorization() throws TimeoutException {
		if(isReady())
			return;
		
		if(oauthSpotify == null)
			throw new RuntimeException("ERROR: authorization requires correct config!");					
		
		String authorizationURL = oauthSpotify.getAuthorizationUrl();
		try {
			Desktop.getDesktop().browse(new URI(authorizationURL));
		} catch(IOException | URISyntaxException e) {
			System.out.println("please visit:\n"+authorizationURL);
		}	

		int timeCounter = 0;
		while(isReady() == false && timeCounter <= timeout) {
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {
				e.printStackTrace();				
			}
			timeCounter += 500;
		}		
		
		if(timeCounter > timeout)
			throw new TimeoutException("Did not receive authorization code within "+timeout+"ms");		
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
		if(makeReady() == false)
			return null;
		return new SpotifyUserPlayListList(this);
	}
	
	public SpotifyAlbum getAlbum(String albumId) {
		if(makeReady() == false)
			return null;
		return new SpotifyAlbum(this, albumId);
	}
	
	public void signAPIRequest(SpotifyAPIRequest request) {
		request.addHeader("Authorization", "Bearer "+authorizationToken.getAccessToken());
	}
	
	private boolean makeReady() {
		if(isReady() == false) {
			try {
				getAuthorization();
				return true;
			} catch(TimeoutException e) {
				System.err.println(e.getMessage());
				return false;
			}
		}
		return true;	
	}
	
	/**
	 * requests refresh and access tokens, is called when code was received
	 */
	private void sendAccessRequest() {	
		authorizationToken = oauthSpotify.getAuthorizationToken(code);
    if(authorizationToken.isValid() == false)
    {
    	System.err.println("ERROR: could not send authorization request!");
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

	@Override
  public String handleHTTPGetRequest(Map<String, String> parameters) {
	  if(parameters.containsKey(CODE_PARAMETER) == false) {
	  	if(oauthSpotify == null) {
	  		return "<html><body>Spotify has invalid configuration!</body></html>";
	  	}
	  	String authorizationURL = oauthSpotify.getAuthorizationUrl();
	  	return "<html><body>please visit <a href=\"" +authorizationURL + "\">this</a> to get authorization code.</body></html>";
	  }
	  
	  code = parameters.get(CODE_PARAMETER);
	  sendAccessRequest();
	  return "received code sucessfully";
  }
}
