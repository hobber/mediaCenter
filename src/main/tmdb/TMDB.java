package main.tmdb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.http.HTTPResponse;
import main.tmdb.datastructure.TMDBSeason;
import main.tmdb.datastructure.TMDBSeries;
import main.utils.XMLFile.ElementList;

public class TMDB {
	
	private String apiKey;
	private String userName;
	private String password;
	private String requestToken;	
	private String sessionId;
	private int accountId;

	public TMDB(ElementList config) {
		apiKey = config.getString("api_key", null);
		if(apiKey == null)
			throw new RuntimeException("TMDB: no API key provided!");
		
		userName = config.getString("username", null);	
		if(userName == null)
			throw new RuntimeException("TMDB: no username provided!");
		
		password = config.getString("password", null);
		if(password == null)
			throw new RuntimeException("TMDB: no password provided!");
		
		if(getRequestToken() == false)
			throw new RuntimeException("TMDB: could not get session token!");
		
		if(validateLogin() == false)
			throw new RuntimeException("TMDB: could not login!");
		
		if(getSessionId() == false)
			throw new RuntimeException("TMDB: could not get session ID!");
		
		if(getAccountId() == false)
			throw new RuntimeException("TMDB: could not get account ID!");
		
		/*
		getAccountLists();
		getFavouriteMovies();
		getFavouriteSeries();
		getWatchListMovies();
		getWatchListSeries();
		getPopularMovies();*/
		//searchSeries();
		
		TMDBSeries series = getSeries(39272);
		System.out.println(series);
		for(int i=0; i<series.numberOfSeasons(); i++)
			System.out.println(getSeason(39272, i));		
		
		
		System.out.println("TMDB: everything fine :)");
	}
	
	public void signRequest(TMDBRequest request) {
		request.addQuery("api_key", apiKey);
	}
	
	public boolean getRequestToken() {
		TMDBRequest request = new TMDBRequest("authentication/token/new");
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		
		if(response.getResponseBoolean("success", false) == false)
			return false;
		requestToken = response.getResponseString("request_token");
		if(requestToken.length() == 0)
			return false;
		return true;		
	}
	
	public boolean validateLogin() {
		TMDBRequest request = new TMDBRequest("authentication/token/validate_with_login");
		request.addQuery("request_token", requestToken);
		request.addQuery("username", userName);
		request.addQuery("password", password);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		
		return response.getResponseBoolean("success", false);		
	}
	
	public boolean getSessionId() {
		TMDBRequest request = new TMDBRequest("authentication/session/new");
		request.addQuery("request_token", requestToken);
		signRequest(request);
		HTTPResponse response = request.sendRequest();		
		
		if(response.getResponseBoolean("success", false) == false)
			return false;
		sessionId = response.getResponseString("session_id");
		if(sessionId.length() == 0)
			return false;
		return true;
	}
	
	public boolean getAccountId() {
		TMDBRequest request = new TMDBRequest("account");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		
		accountId = response.getResponseInt("id", -1);
		if(accountId < 0)
			return false;
		return true;
	}
	
	public void getAccountLists() {
		TMDBRequest request = new TMDBRequest("account/"+accountId+"/lists");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		System.out.println(response);		
	}
	
	public void getFavouriteMovies() {
		TMDBRequest request = new TMDBRequest("account/"+accountId+"/favorite/movies");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		System.out.println(response);		
	}
	
	public void getFavouriteSeries() {
		TMDBRequest request = new TMDBRequest("account/"+accountId+"/favorite/tv");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		System.out.println(response);		
	}
	
	public void getWatchListMovies() {
		TMDBRequest request = new TMDBRequest("account/"+accountId+"/watchlist/movies");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		System.out.println(response);		
	}
	
	public void getWatchListSeries() {
		TMDBRequest request = new TMDBRequest("account/"+accountId+"/watchlist/tv");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		System.out.println(response);		
	}
	
	public void getPopularMovies() {
		TMDBRequest request = new TMDBRequest("movie/popular");		
		signRequest(request);		
		HTTPResponse response = request.sendRequest();
		System.out.println(response);			
	}
	
	public void searchSeries() {
		TMDBRequest request = new TMDBRequest("search/tv");
		request.addQuery("query", "Once+upon+a+time"); //39272
		signRequest(request);		
		HTTPResponse response = request.sendRequest();
		System.out.println(response);	
	}
	
	public TMDBSeries getSeries(int id) {
		TMDBRequest request = TMDBSeries.createRequest(id);		
		signRequest(request);		
		HTTPResponse response = request.sendRequest();		
		return new TMDBSeries(response);		
		//http://image.tmdb.org/t/p/w500/6S8rM2Qq3B3g3dgAnJlilgUc2dE.jpg?api_key=5a18658d75c3eb554e23c1102133c187
	}
	
	public TMDBSeason getSeason(int seriesId, int season) {
		TMDBRequest request = TMDBSeason.createRequest(seriesId, season);		
		signRequest(request);		
		HTTPResponse response = request.sendRequest();		
		return new TMDBSeason(response);
	}
}
