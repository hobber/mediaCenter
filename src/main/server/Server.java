package main.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server implements HttpHandler {

	private final static String RESSOURCE_PATH = "./user_interface";
	
	public Server() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(11111), 0);
			server.createContext("/", this);
			server.setExecutor(null); // creates a default executor
			server.start();
			System.out.println("started...");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String readFile(String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(fileName));
			return new String(encoded, Charset.defaultCharset());
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public static long copy(InputStream is, OutputStream os) {
		byte[] buf = new byte[8192];
		long total = 0;
		int len = 0;
		try {
			while (-1 != (len = is.read(buf))) {
				os.write(buf, 0, len);
				total += len;
			}
		} catch (IOException ioe) {
			throw new RuntimeException("error reading stream", ioe);
		}
		return total;
	}
	
	public JSONObject getContent(String name) {
		JSONObject response = new JSONObject();
		try {
			JSONObject movies = new JSONObject();
			movies.put("name", "Movies");
			movies.put("icon", "content/movie.png");
			movies.put("type",  "menuEntry");
			response.put("entries", movies);
		} catch(JSONException e) {
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("return " + response.toString());
		return response;
	}
	
	public JSONObject getMenu() {
		JSONObject response = new JSONObject();
		try {		
			JSONObject movies = new JSONObject();
			movies.put("name", "Movies");
			movies.put("icon", "content/movie.png");
			movies.put("type",  "menuEntry");
			movies.put("id",  "movies");
			response.append("entries", movies);
			
			JSONObject moviesHome = new JSONObject();
			moviesHome.put("name", "Home");
			moviesHome.put("type",  "menuSubEntry");
			moviesHome.put("id",  "movies.home");
			response.append("entries", moviesHome);
			
			JSONObject moviesLibrary = new JSONObject();
			moviesLibrary.put("name", "Library");
			moviesLibrary.put("type",  "menuSubEntry");
			moviesLibrary.put("id",  "movies.library");
			response.append("entries", moviesLibrary);
			
			JSONObject moviesSearch = new JSONObject();
			moviesSearch.put("name", "Search");
			moviesSearch.put("type",  "menuSubEntry");
			moviesSearch.put("id",  "movies.search");
			response.append("entries", moviesSearch);
			
			JSONObject series = new JSONObject();
			series.put("name", "Series");
			series.put("icon", "content/series.png");
			series.put("type",  "menuEntry");
			series.put("id",  "series");
			response.append("entries", series);
			
			JSONObject seriesHome = new JSONObject();
			seriesHome.put("name", "Home");
			seriesHome.put("type",  "menuSubEntry");
			seriesHome.put("id",  "series.home");
			response.append("entries", seriesHome);
			
			JSONObject seriesLibrary = new JSONObject();
			seriesLibrary.put("name", "Library");
			seriesLibrary.put("type",  "menuSubEntry");
			seriesLibrary.put("id",  "series.library");
			response.append("entries", seriesLibrary);
			
			JSONObject seriesSearch = new JSONObject();
			seriesSearch.put("name", "Search");
			seriesSearch.put("type",  "menuSubEntry");
			seriesSearch.put("id",  "series.search");
			response.append("entries", seriesSearch);
						
			JSONObject music = new JSONObject();
			music.put("name", "Music");
			music.put("icon", "content/music.png");
			music.put("type",  "menuEntry");
			music.put("id",  "music");
			response.append("entries", music);
			
			JSONObject images = new JSONObject();
			images.put("name", "Images");
			images.put("icon", "content/image.png");
			images.put("type",  "menuEntry");
			images.put("id",  "images");
			response.append("entries", images);	
			
			JSONObject settings = new JSONObject();
			settings.put("name", "Settings");
			settings.put("icon", "content/settings.png");
			settings.put("type",  "menuEntry");
			settings.put("id",  "settings");
			response.append("entries", settings);	
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public void handleAPIRequest(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		String type = uri.substring(5);
		System.out.println("api: " + uri + " -> " + type);		
		
		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/jsonp; charset=UTF-8");
		
		JSONObject buffer;
		if(type.startsWith("menu"))
			buffer = getMenu();
		else
			buffer = getContent(type);
		String response = buffer.toString();
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes("UTF-8"));
		os.close();	  
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {		
		if(exchange.getRequestMethod().equals("GET")) {
			String uri = exchange.getRequestURI().toString();
			if(uri.startsWith("/api")) {
				handleAPIRequest(exchange);
			}
			else {
				String path = RESSOURCE_PATH + uri;
//		  System.out.println("get " + path);
				byte[] response = Files.readAllBytes(Paths.get(path));
				exchange.sendResponseHeaders(200, response.length);
				OutputStream os = exchange.getResponseBody();
				os.write(response);
				os.close();	
			}
		}
		else if(exchange.getRequestMethod().equals("POST")) {
			System.out.print("post ");
	  	copy(exchange.getRequestBody(), System.out);
	  	System.out.println("");
			exchange.sendResponseHeaders(200, 0);
		}
		else {
			exchange.sendResponseHeaders(405, 0);
		}		
//		for (String key : exchange.getRequestHeaders().keySet()) {
//			System.out.print("header: " + key + " = " );
//			for (String value : exchange.getRequestHeaders().get(key)) {
//				System.out.println(value);
//			}
//		}
	}
}
