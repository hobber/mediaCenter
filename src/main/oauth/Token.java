package main.oauth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class Token {

	private final static String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	
	private long expirationTime = 0;
	private String tokenType = null;
	private String refreshToken = null;
	private String accessToken = null;	
	private String error = null;
	
	public Token(String accessToken, String refreshToken, String expirationTime) {
		if(checkExpirationTime(expirationTime) == false)
		{
			error = "invalide expiration time!";
			return;
		}
		
		try {
			Calendar calendar = Calendar.getInstance();		
			calendar.setTime(new SimpleDateFormat(DATE_FORMAT).parse(expirationTime));
			this.expirationTime = calendar.getTimeInMillis();
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.tokenType = "Bearer";
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkExpirationTime(String expirationTime) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new SimpleDateFormat(DATE_FORMAT).parse(expirationTime));
			if(calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
				return true;
			else
				System.out.println(Calendar.getInstance().getTimeInMillis() + " vs. " + calendar.getTimeInMillis());
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Token(Response response) {
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
  			  else if(key.equals("token_type") == true)
  			  	tokenType = element;
  			  else if(key.equals("refresh_token") == true)
  			  	refreshToken = element;
  			  else if(key.equals("access_token") == true)
  			  	accessToken = element;
  			  else if(key.equals("error") == true);
  			  else if(key.equals("error_description"))
  			  	error = element;
  			  else 
  			  	System.err.println("unknown token: "+key+" = "+element);  			  
  			} catch(JSONException e) {
  				System.err.println(e.getMessage());
  			}
  		}
    } catch(JSONException e) {
    	e.printStackTrace();    	
    }    
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public String getAccessToken() {
		return accessToken;		
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public boolean failed() {
		return error != null;
	}
	
	public String getError() {
		return error;
	}
	
	public boolean isValid() {
		if(failed() == true) {
			System.out.println("Token is invalid: " + error);
			return false;
		}
		if(expirationTime <= Calendar.getInstance().getTimeInMillis()) {
			System.out.println("Token is expired");
			return false;
		}
		return true;
	}
	
	public String getExpirationTime() {
		Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(expirationTime);
    return new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime());
	}
}
