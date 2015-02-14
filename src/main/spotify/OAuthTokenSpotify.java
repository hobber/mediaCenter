package main.spotify;

import java.text.ParseException;
import java.util.Calendar;

import main.http.HTTPResponse;
import main.oauth.OAuthAPI;
import main.oauth.OAuthToken;

public class OAuthTokenSpotify extends OAuthToken {
	
	private String refreshToken;
	private OAuthAPI oauthAPI;
	
	private OAuthTokenSpotify(OAuthAPI oauthAPI, String accessToken, String refreshToken, long expirationTime) {
		super(accessToken, expirationTime);	
		this.oauthAPI = oauthAPI;
		this.refreshToken = refreshToken; 
	}	
	
	static public OAuthTokenSpotify createToken(OAuthAPI oauthAPI, String accessToken, String refreshToken, String expirationTime) {
		Calendar calendar = Calendar.getInstance();		
		try {
		calendar.setTime(dateFormat.parse(expirationTime));
		} catch(ParseException e) {
			System.err.println(expirationTime + " has wrong format (should be " + DATE_FORMAT + ")!");
			return null;
		}
		return new OAuthTokenSpotify(oauthAPI, accessToken, refreshToken, calendar.getTimeInMillis());
	}
	
	static public OAuthTokenSpotify createToken(OAuthAPI oauthAPI, HTTPResponse response) {
		long expirationTime = 0;
		String accessToken = "";
		String refreshToken = "";			
		
		if(response.isValid() == false) {
			System.err.println("ERROR: invalid response for refresh token!");
			return null;
		}
		
		String error = response.getResponseString("error_description"); 
		if(error.length() > 0) {
			System.err.println("ERROR: "+error);
			return null;
		}
	
		String expiresIn = response.getResponseString("expires_in");
		if(expiresIn.length() != 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, Integer.parseInt(expiresIn));
			expirationTime = calendar.getTimeInMillis();	   
		}
		accessToken = response.getResponseString("access_token");
		refreshToken = response.getResponseString("refresh_token");
		
		if(expiresIn.length() == 0 && accessToken.length() == 0) {
			System.err.println("Token has invalid format!");
			return null;
		}
		
  	return new OAuthTokenSpotify(oauthAPI, accessToken, refreshToken, expirationTime);
		 		 
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public boolean refresh() {
		HTTPResponse response = oauthAPI.getRefreshResponse(this);	
		if(response.isValid() == false) {
			System.err.println("ERROR: invalid response for refresh token!");
			return false;
		}
		
		String error = response.getResponseString("error_description"); 
		if(error.length() > 0) {
			System.err.println("ERROR: "+error);
			return false;
		}
	
		String expiresIn = response.getResponseString("expires_in");
		if(expiresIn.length() != 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, Integer.parseInt(expiresIn));
			expirationTime = calendar.getTimeInMillis();	   
		}
		accessToken = response.getResponseString("access_token");
		
		if(expiresIn.length() != 0 && accessToken.length() != 0) {
			System.out.println("Token will expire @ "+getExpirationTime());
			return true;
		}
		
		return false;
	}
}
