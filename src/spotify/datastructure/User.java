package spotify.datastructure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link} https://developer.spotify.com/web-api/get-current-users-profile/
 */
public class User {

	private boolean valid = false;
	private String id;
	private String uri;
	private String displayName;	
	
	public User(JSONObject user) {
		if(user == null)
			return;
		
		try {
			if(user.getString("type").equals("user") == false)
				return;
			id = getString(user, "id");
			uri = getString(user, "uri");
			displayName = getString(user, "display_name");
			if(displayName.equals("null"))
				displayName = id;			
			valid = true;			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		if(isValid() == false)
			return "invalid user data";
		return id + " (" + displayName + ") @ " + uri;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	private String getString(JSONObject user, String key) {
		try {
			Object value = user.get(key);
			if(value == null)
				return "";
			return value.toString();				
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
}
