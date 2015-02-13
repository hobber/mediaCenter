package main.spotify;

import java.util.HashMap;
import java.util.Map;

import main.http.HTTPUtils;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

public class SpotifyAPIRequest {

	public static final Integer PAGE_SIZE = 50;
	public static final String API_URL = "https://api.spotify.com/v1/";
	
	private String path = "";
	private String query = "";
	private Map<String, String> headers = new HashMap<String, String>();	
	
	public SpotifyAPIRequest(String path) {
		this.path = path;
	}
	
	public SpotifyAPIRequest addQuery(String name, String value) {
		if(query.length() == 0)
			query = "?" +name + "=" + value;
		else 
		  query += "&" + name + "=" + value;
		return this;
	}
	
	public SpotifyAPIRequest addHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}
	
	public SpotifyAPIRequest setPage(int pageIndex) {
		if(pageIndex >= 0) {
			addQuery("limit", PAGE_SIZE.toString());
			addQuery("offset", ((Integer)(PAGE_SIZE*pageIndex)).toString());
		}
		return this;
	}
	
	public JSONObject sendRequest() {
		String url = API_URL + path + query;
		HttpGet request = new HttpGet(url);
		for (Map.Entry<String, String> header : headers.entrySet())
		request.setHeader(header.getKey(), header.getValue());		
		return HTTPUtils.sendHTTPGetRequest(request);
	}
}
