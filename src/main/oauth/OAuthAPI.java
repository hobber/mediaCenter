package main.oauth;

import main.http.HTTPResponse;

public interface OAuthAPI {
	
	public HTTPResponse getRefreshResponse(OAuthToken token);
	
}
