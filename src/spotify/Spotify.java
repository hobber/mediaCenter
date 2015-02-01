package spotify;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;

import oauth.Token;
import server.HTTPListener;
import server.HTTPServer;
import utils.XMLFile;

public class Spotify extends HTTPListener {

	private static final String URL_PATH = "spotify";
	private static final String CODE_PARAMETER = "code";
	private static final String SCOPE = "user-read-private";
	
	private String clientID;
	private String clientSecret;
	private String redirectURI;
	private String code;
	private OAuthAPISpotify oauthSpotify;
	
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
		return true;
	}
	
	public String getAuthorizationRequestURL() {
		oauthSpotify = new OAuthAPISpotify(clientID, clientSecret, redirectURI, SCOPE);		
		return oauthSpotify.getAuthorizationUrl();		
	}
	
	/**
	 * requests refresh and access tokens, is called when code was received
	 */
	private void sendAccessRequest() {	
		Token authorizationToken = oauthSpotify.getAuthorizationToken(code);
    if(authorizationToken.failed())
    {
    	System.err.println("ERROR: "+authorizationToken.getError());
    	return;
    }
    else
    	System.out.println("Token will expire @ "+authorizationToken.getExpirationTime());
    
    Token refreshToken = oauthSpotify.getRefreshToken(authorizationToken);
    if(refreshToken.failed())
    {
    	System.err.println("ERROR: "+refreshToken.getError());
    	return;
    }
    else
    	System.out.println("Token will expire @ "+refreshToken.getExpirationTime());
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
