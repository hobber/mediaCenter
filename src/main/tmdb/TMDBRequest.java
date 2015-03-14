package main.tmdb;

import main.http.HTTPResponse;
import main.http.HTTPUtils;

import org.apache.http.client.methods.HttpGet;

public class TMDBRequest {
	
	public static final String API_URL = "https://api.themoviedb.org/3/";
	
	private String path = "";
	private String query = "";
	
	public TMDBRequest(String path) {
		this.path = path;
	}
	
	public TMDBRequest addQuery(String name, String value) {
		if(query.length() == 0)
			query = "?" +name + "=" + value;
		else 
		  query += "&" + name + "=" + value;
		return this;
	}
	
	public HTTPResponse sendRequest() {
		String url = API_URL + path + query;
		HttpGet request = new HttpGet(url);
		
		return HTTPUtils.sendHTTPGetRequest(request);		
	}
}
