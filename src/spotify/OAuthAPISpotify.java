package spotify;

import oauth.OAuthRequest;
import oauth.Request;
import oauth.Response;
import oauth.Token;

public class OAuthAPISpotify {

	private static final String GRANT_TYPE = "grant_type";
	private static final String GRANT_AUTHORIZATION = "authorization_code";
	private static final String GRANT_REFRESH = "refresh_token";
	private static final String REFRESH_TOKEN = "refresh_token";  
	private static final String CLIENT_ID = "client_id";
	private static final String CLIENT_SECRET = "client_secret";
	private static final String REDIRECT_URI = "redirect_uri";
	private static final String RESPONSE_TYPE = "response_type";
	private static final String RESPONSE_AUTHORIZE = "code";
	private static final String CODE = "code";
	private static final String SCOPE = "scope";
	private static final String AUTHORIZE_ENDPOINT = "https://accounts.spotify.com/authorize/";
	private static final String ACCESS_TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
  
	private String clientID;
	private String clientSecret;
	private String redirectURI;
	private String scope;

	public OAuthAPISpotify(String clientID, String clientSecret, String redirectURI, String scope)
	{
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.redirectURI = redirectURI;
		this.scope = scope;
	}
	
  public String getAuthorizationUrl() {		
		String url = AUTHORIZE_ENDPOINT +
        "?" + CLIENT_ID + "=" + clientID +
        "&" + RESPONSE_TYPE + "=" + RESPONSE_AUTHORIZE +
        //"&state=" + state +
        "&" + SCOPE + "=" + scope +
        "&" + REDIRECT_URI + "=" + redirectURI;		
		return url;
  }
	
	public Token getAuthorizationToken(String code)
  {
    OAuthRequest request = new OAuthRequest(Request.Verb.POST, ACCESS_TOKEN_ENDPOINT);    
    request.addBodyParameter(GRANT_TYPE, GRANT_AUTHORIZATION);
    request.addBodyParameter(CODE, code);
    request.addBodyParameter(REDIRECT_URI, redirectURI);
    request.addBodyParameter(CLIENT_ID, clientID);
    request.addBodyParameter(CLIENT_SECRET, clientSecret);
    
    Response response = request.send();
    return new Token(response);    
  }
	
	public Token getRefreshToken(Token authorizationToken)
  {
    OAuthRequest request = new OAuthRequest(Request.Verb.POST, ACCESS_TOKEN_ENDPOINT);    
    request.addBodyParameter(GRANT_TYPE, GRANT_REFRESH);
    request.addBodyParameter(REFRESH_TOKEN, authorizationToken.getRefreshToken());
    request.addBodyParameter(CLIENT_ID, clientID);
    request.addBodyParameter(CLIENT_SECRET, clientSecret);
    
    Response response = request.send();
    return new Token(response);    
  }
}
