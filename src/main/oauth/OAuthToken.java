package main.oauth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

abstract public class OAuthToken {

	public final static String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	protected long expirationTime = 0;
	protected String accessToken = null;	
	
	public OAuthToken(String accessToken, long expirationTime) {				
		this.expirationTime = expirationTime;
		this.accessToken = accessToken;
		OAuthTokenRefresher.addToken(this);		
	}
	
	public boolean checkExpirationTime(String expirationTime) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateFormat.parse(expirationTime));
			if(calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
				return true;
			else
				System.out.println(Calendar.getInstance().getTimeInMillis() + " vs. " + calendar.getTimeInMillis());
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getAccessToken() {
		return accessToken;		
	}	
	
	public boolean willExpireWithin(long millis) {
		if(expirationTime < 0)
			return false;
		
		if(expirationTime+millis > Calendar.getInstance().getTimeInMillis()) {			
			return false;
		}
		return true;
	}
	
	public boolean isValid() {
		return willExpireWithin(0) ? false : true;		
	}
	
	public String getExpirationTime() {
		Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(expirationTime);
    return dateFormat.format(calendar.getTime());
	}
	
	abstract public boolean refresh();
}
