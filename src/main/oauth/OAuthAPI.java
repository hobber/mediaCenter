package main.oauth;

public interface OAuthAPI {
	
	public Response getRefreshResponse(OAuthToken token);
	
}
