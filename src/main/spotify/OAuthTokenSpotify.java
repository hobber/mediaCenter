package main.spotify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import main.oauth.OAuthAPI;
import main.oauth.OAuthRequest;
import main.oauth.Request;
import main.oauth.Response;
import main.oauth.OAuthToken;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	static public OAuthTokenSpotify createToken(OAuthAPI oauthAPI, Response response) {
		long expirationTime = 0;
		String accessToken = "";
		String refreshToken = "";			
					
		//TODO: use getString, getInt method!!
		try {
      JSONObject responseObject = new JSONObject(response.getBody());
      
      Iterator<?> iterator = responseObject.keys();    
  		while(iterator.hasNext()){
  			String key = (String)iterator.next();
  			try {
  				String element = responseObject.get(key).toString();  				
  			  if(key.equals("expires_in") == true) {
  			  	Calendar calendar = Calendar.getInstance();
  			  	calendar.add(Calendar.SECOND, Integer.parseInt(element));
  			  	expirationTime = calendar.getTimeInMillis();
  			  }
  			  else if(key.equals("token_type") == true);
  			  else if(key.equals("refresh_token") == true)
  			  	refreshToken = element;
  			  else if(key.equals("access_token") == true)
  			  	accessToken = element;
  			  else if(key.equals("error") == true);
  			  else if(key.equals("error_description")) {
  			  	System.err.println("ERROR: " + element);
  			  	return null;
  			  }
  			  else 
  			  	System.err.println("unknown element: "+key+" = "+element);  			  
  			} catch(JSONException e) {
  				System.err.println(e.getMessage());
  				return null;
  			}
  		}
  		return new OAuthTokenSpotify(oauthAPI, accessToken, refreshToken, expirationTime);
		} catch(JSONException e) {
			System.err.println(e.getMessage());
			return null;
		}	  		 
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public boolean refresh() {
		Response response = oauthAPI.getRefreshResponse(this);
		if(response == null)
			return false;
		
		return false;
	}
}
