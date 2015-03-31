package main.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.server.content.UserContentGroup;
import main.server.content.UserContentPage;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server implements HttpHandler {
	
	private final static String RESSOURCE_PATH = "./user_interface";
	static final Server INSTANCE = new Server();
	
	private Map<String, UserContentGroup> contentGroups = new HashMap<String, UserContentGroup>();
	
	private Server() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(11111), 0);
			server.createContext("/", this);
			server.setExecutor(null);
			server.start();
			System.out.println("server started...");			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static long copy(InputStream is, OutputStream os) {
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
	
	private JSONObject getContent(String name) {
		int index = name.indexOf(".");
		if(index < 0) {
			System.err.println("ERROR: invalid name " + name);
		}
		
		String groupName = name.substring(0, index);
		String pageName = name.substring(index + 1);		
		
		UserContentGroup group = contentGroups.get(groupName);		
		if(group == null) {
			System.out.println("ERROR: group " + groupName + " not found");			
			return new JSONObject();
		}
		
		UserContentPage page = group.getContentPage(pageName);
		if(page == null) {
			System.out.println("ERROR: page " + pageName + " not found");			
			return new JSONObject();
		}
		
		JSONObject content = new JSONObject();
		try {
			content.put("content", page.toJSON());
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());			
		}
		return content;		
	}
	
	private JSONObject getMenu() {
		JSONObject response = new JSONObject();
		try {		
			for(String groupName : contentGroups.keySet()) {
				UserContentGroup group = contentGroups.get(groupName);
				JSONObject groupElement = new JSONObject();
				groupElement.put("name", groupName);
				groupElement.put("icon", group.getIconPath());
				groupElement.put("type", "menuEntry");
				groupElement.put("id",  groupName);
				response.append("entries", groupElement);
								
				List<UserContentPage> list = group.getContentPages();
				for(UserContentPage page : list) {
					JSONObject element = new JSONObject();
					element.put("name", page.getName());
					element.put("type",  "menuSubEntry");
					element.put("id",  groupName + "." + page.getName());
					response.append("entries", element);
				}
			}
			
			/*
			JSONObject movies = new JSONObject();
			movies.put("name", "Movies");
			moviesHome.put("name", "Home");
			moviesLibrary.put("name", "Library");
			moviesFavourites.put("name", "Favourites");
			moviesSearch.put("name", "Search");

			series.put("name", "Series");
			seriesHome.put("name", "Home");
			seriesLibrary.put("name", "Library");
			seriesFavourites.put("name", "Favourites");
			seriesSearch.put("name", "Search");
			music.put("name", "Music");
			
			images.put("name", "Images");
			
			settings.put("name", "Settings");
			*/
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	private void handleAPIRequest(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		String request = uri.substring(5);
		System.out.println("api: " + uri + " -> " + request);		
		
		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/jsonp; charset=UTF-8");
		
		JSONObject buffer;
		if(request.startsWith("menu"))
			buffer = getMenu();
		else
			buffer = getContent(request.substring(8));
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
	}
	
	public static boolean registerUserContentGroup(UserContentGroup group) {
		String name = group.getName();
		if(INSTANCE.contentGroups.containsKey(name)) {
			System.err.println("ERROR: " + name + " was already added");
			return false;
		}
		
		INSTANCE.contentGroups.put(name, group);		
		return true;
	}
}
