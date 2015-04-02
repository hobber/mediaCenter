package main.server.content;

import org.json.JSONObject;

public interface UserContentPage {

	public String getName();
	public JSONObject toJSON();
	public void setGroup(UserContentGroup group);
	public JSONObject handle(String query);
}
