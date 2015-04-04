package main.tmdb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import main.Plugin;
import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.server.Server;
import main.server.content.UserContentGroup;
import main.tmdb.content.TMDBSearchPage;
import main.tmdb.datastructure.TMDBCredits;
import main.tmdb.datastructure.TMDBGenreList;
import main.tmdb.datastructure.TMDBSearchResult;
import main.tmdb.datastructure.TMDBSearchResultList;
import main.tmdb.datastructure.TMDBSeries;
import main.utils.ConfigElementGroup;
import main.utils.JSONArray;
import main.utils.JSONContainer;

public class TMDB extends Plugin {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private static TMDB INSTANCE;
	
	private String apiKey;
	private String userName;
	private String password;
	private String requestToken;	
	private String sessionId;
	private int accountId;
	private LinkedList<String> posterSizes = new LinkedList<String>();
	
	private ConfigElementGroup config;
 
	private TMDBGenreList genres = new TMDBGenreList(); 
	
	private TMDB(ConfigElementGroup config) {
		super("TMDB");
		this.config = config;
	}
	
	public static TMDB create(ConfigElementGroup config) {
		INSTANCE = new TMDB(config);
		return INSTANCE;
	}
	
	public void start() {
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
		
		if(getConfiguration() == false)
			throw new RuntimeException("TMDB: could not get configuration!");
		
		if(getGenres() == false)
			throw new RuntimeException("TMDB: could not get genre list!");	
		
		if(createContentPages() == false)
			throw new RuntimeException("TMDB: could not create content pages!");
	}
	
	private void signRequest(TMDBRequest request) {
		request.addQuery("api_key", apiKey);
	}
	
	private boolean getRequestToken() {
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
	
	private boolean validateLogin() {
		TMDBRequest request = new TMDBRequest("authentication/token/validate_with_login");
		request.addQuery("request_token", requestToken);
		request.addQuery("username", userName);
		request.addQuery("password", password);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		
		return response.getResponseBoolean("success", false);		
	}
	
	private boolean getSessionId() {
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
	
	private boolean getAccountId() {
		TMDBRequest request = new TMDBRequest("account");
		request.addQuery("session_id", sessionId);
		signRequest(request);
		HTTPResponse response = request.sendRequest();
		
		accountId = response.getResponseInt("id", -1);
		if(accountId < 0)
			return false;
		return true;
	}

	private boolean getConfiguration() {
		TMDBRequest request = new TMDBRequest("configuration");
		signRequest(request);
		HTTPResponse response = request.sendRequest();	
		if(response.isValid() == false)
			return false;

		JSONContainer container = response.getJSONBody();
		JSONArray posterSizes = container.getArray("images.poster_sizes");
		for(int i=0; i<posterSizes.length(); i++)			
			this.posterSizes.add(posterSizes.getString(i, ""));
		return true;
	}
	
	private boolean getGenres() {
		TMDBRequest requestMovie = TMDBGenreList.createRequestMovie();
		signRequest(requestMovie);
		HTTPResponse responseMovie = requestMovie.sendRequest();
		if(responseMovie.isValid() == false)
			return false;
		genres.add(responseMovie.getJSONBody().getArray("genres"));
		
		TMDBRequest requestSeries = TMDBGenreList.createRequestSeries();
		signRequest(requestSeries);
		HTTPResponse responseSeries = requestSeries.sendRequest();
		if(responseSeries.isValid() == false)
			return false;
		genres.add(responseSeries.getJSONBody().getArray("genres"));	
		
		return true;
	}
	
	private boolean createContentPages() {
		UserContentGroup group = new UserContentGroup("Series", "content/series.png");
//		group.addPage(new TMDBLibraryPage());
		group.addPage(new TMDBSearchPage(this, TMDBSearchPage.Type.SERIES));
		return Server.registerUserContentGroup(group);		
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
		
	public TMDBSearchResultList searchSeries(String seriesName) {
		TMDBRequest request = new TMDBRequest("search/tv");
		request.addQuery("query", HTTPUtils.encodeTerm(seriesName));
		signRequest(request);
		
		JSONContainer response = request.sendRequest().getJSONBody();
		if(response == null)
			throw new RuntimeException("TMDB: failed to search for " + seriesName);
		
		return new TMDBSearchResultList(response.getArray("results"));
	}
	
	public TMDBSeries getSeries(int id) {
		TMDBRequest request = TMDBSeries.createRequest(id);		
		signRequest(request);		
		HTTPResponse response = request.sendRequest();		
		return new TMDBSeries(response.getJSONBody());
	}
	
	public TMDBCredits getSeriesCredits(int id) {
		TMDBRequest request = TMDBCredits.createRequest(id, TMDBCredits.Type.SERIES);		
		signRequest(request);		
		HTTPResponse response = request.sendRequest();		
		return new TMDBCredits(response.getJSONBody());
	}
	
	/*
	public TMDBSeason getSeason(int seriesId, int season) {
		TMDBRequest request = TMDBSeason.createRequest(seriesId, season);		
		signRequest(request);		
		HTTPResponse response = request.sendRequest();		
		return new TMDBSeason(response);
	}
	*/	
	
	public static String getPosterURL(String posterPath, boolean preview) {
		if(preview)
			return "http://image.tmdb.org/t/p/" + INSTANCE.posterSizes.getFirst()  + posterPath;
		return "http://image.tmdb.org/t/p/" + INSTANCE.posterSizes.getLast() + posterPath;
	}
	
	public static String getGenreName(Integer id) {
		return INSTANCE.genres.get(id);
	}
	
	public static String getYear(String date) {
		if(date == null)
			return "?";
				
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DATE_FORMAT.parse(date));
			return Integer.toString(calendar.get(Calendar.YEAR));
		} catch(Exception e) {
			System.err.println("ERROR: " + e);
			return "?";
		}		
	}
}
