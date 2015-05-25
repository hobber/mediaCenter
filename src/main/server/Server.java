package main.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Main;
import main.http.HTTPUtils;
import main.plugins.PluginController;
import main.server.content.UserContentGroup;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;
import main.utils.Logger;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server implements HttpHandler {
	
	private static final String RESSOURCE_PATH = "./user_interface";
	private static Server INSTANCE;
	private HttpServer server;
	private int portNumber;
	
	private Map<String, UserContentGroup> contentGroups = new HashMap<String, UserContentGroup>();
	
	private Server(ConfigElementGroup config) {
		portNumber = config.getInt("port", 11011);
		if(createServer() == false) {
			HttpPost request = new HttpPost("http://localhost:"+portNumber+"/terminate");
			HTTPUtils.sendHTTPPostRequest(request);
			createServer();
		}
	}
	
	private boolean createServer() {
		try {
			server = HttpServer.create(new InetSocketAddress(portNumber), 0);
			server.createContext("/", this);
			server.setExecutor(null);			
			return true;			
		} catch(Exception e) {
			return false;
		}
	}
	
	public static synchronized boolean run(ConfigElementGroup config) {
		INSTANCE = new Server(config);
		if(INSTANCE.server == null) {
			System.err.println("ERROR: address already in use, close other server instance.");
			return false;
		}
		INSTANCE.server.start();
		return true;
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
	
//	private JSONObject getContent(String name) {
//		int index = name.indexOf(".");
//		if(index < 0) {
//			System.err.println("ERROR: invalid name " + name);
//		}
//		
//		String groupName = name.substring(0, index);
//		String pageName = name.substring(index + 1);		
//		
//		UserContentGroup group = contentGroups.get(groupName);		
//		if(group == null) {
//			System.out.println("ERROR: group " + groupName + " not found");			 
//			return new JSONObject();
//		}
//		
//		UserContentPage page = group.getContentPage(pageName);
//		if(page == null) {
//			System.out.println("ERROR: page " + pageName + " not found");			
//			return new JSONObject();
//		}
//		
//		return page.getPage();		
//	}
//	
//	private JSONObject getMenu() {
//		JSONObject response = new JSONObject();
//		/*
//		try {		
//			for(String groupName : contentGroups.keySet()) {
//				UserContentGroup group = contentGroups.get(groupName);
//				JSONObject groupElement = new JSONObject();
//				groupElement.put("name", groupName);
//				groupElement.put("icon", group.getIconPath());
//				groupElement.put("type", "menuEntry");
//				groupElement.put("id",  groupName);
//				response.append("entries", groupElement);
//								
//				List<UserContentPage> list = group.getContentPages();
//				for(UserContentPage page : list) {
//					JSONObject element = new JSONObject();
//					element.put("name", page.getName());
//					element.put("type",  "menuSubEntry");
//					element.put("id",  groupName + "." + page.getName());
//					response.append("entries", element);
//				}
//			}
//			
//			/*
//			JSONObject movies = new JSONObject();
//			movies.put("name", "Movies");
//			moviesHome.put("name", "Home");
//			moviesLibrary.put("name", "Library");
//			moviesFavourites.put("name", "Favourites");
//			moviesSearch.put("name", "Search");
//
//			series.put("name", "Series");
//			seriesHome.put("name", "Home");
//			seriesLibrary.put("name", "Library");
//			seriesFavourites.put("name", "Favourites");
//			seriesSearch.put("name", "Search");
//			music.put("name", "Music");
//			
//			images.put("name", "Images");
//			
//			settings.put("name", "Settings");
//			
//		} catch(JSONException e) {
//			e.printStackTrace();
//		}*/
//		return response;
//	}
//	
//	private ContentPage handleContextSpecificRequest(String request) {		
//		int indexPoint = request.indexOf(".");
//		int indexParameter = request.indexOf("&");
//		if(indexParameter < 0)
//			indexParameter = request.length() - 1;
//		
//		if(indexPoint < 0 || indexPoint > indexParameter)
//			return new ContentErrorPage(request + " contains no valid context");		
//		
//		String groupName = request.substring(0, indexPoint);
//		UserContentGroup contentGroup = contentGroups.get(groupName);
//		if(contentGroup == null)
//			return new ContentErrorPage("content group " + groupName + " not found");		
//		
//		String pageName = request.substring(indexPoint + 1, indexParameter);
//		String query = request.substring(indexParameter + 1); 
//		return contentGroup.handle(pageName, query);		
//	}
	
	private byte[] convert(String response) {		
		ArrayList<Byte> bytes = new ArrayList<Byte>(response.length());
		for(int i=0; i<response.length(); i++) {
			char c = response.charAt(i);
			if(c <= 127)
				bytes.add((byte)c);
			else { 				
				byte[] tmp = Charset.forName("UTF-8").encode("" + c).array();
				for(int j=0; j<2; j++) {
					String s = String.format("%%%02X", tmp[j] < 0 ? (int)(tmp[j]+256) : (int)tmp[j]);
					for(int l=0; l<3; l++)
						bytes.add((byte)s.charAt(l));
				}
			}
		}
		
		byte[] buffer = new byte[bytes.size()];
		for(int i=0; i< bytes.size(); i++)
			buffer[i] = bytes.get(i);
		return buffer;		
	}
	
	private void handleMenuRequest(HttpExchange exchange) throws IOException {
	  List<ContentMenuEntry> list = PluginController.getMenuEntries();
	  
	  Headers headers = exchange.getResponseHeaders();
    headers.add("Content-Type", "application/jsonp; charset=UTF-8");
    
	  JSONObject buffer = new JSONObject();
	  for(ContentMenuEntry entry : list)
	    buffer.put(entry.getName(), entry.toJSON());
	  
    byte[] response = convert(buffer.toString());
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
    os.write(response);   
    os.close();  
	}
	
	private void handleAPIRequest(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		String request = uri.substring(5);
		
		int index = request.indexOf('.');
		int id = Integer.parseInt(request.substring(3, index));
		int subId = Integer.parseInt(request.substring(index+1));
		
		System.out.println("api: " + uri + " -> " + request + ", " + id + ", " + subId);		
		
		Headers headers = exchange.getResponseHeaders();
		headers.add("Content-Type", "application/jsonp; charset=UTF-8");
		
		JSONObject buffer = PluginController.handleAPIRequest(id, subId, "");
		
		byte[] response = convert(buffer.toString());
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response);		
		os.close();
	}

	private void handleFileRequest(HttpExchange exchange, String uri) throws IOException {
	  String path = RESSOURCE_PATH + uri;
    if(uri.length() == 0)
      path += "/index.html";
    else if(uri.length() == 1)
      path += "index.html";
    
    byte[] response = Files.readAllBytes(Paths.get(path));
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
    os.write(response);
    os.close(); 
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			String method = exchange.getRequestMethod();
			String uri = exchange.getRequestURI().toString();

			if(method.equals("GET")) {
			  if(uri.startsWith("/menu"))
          handleMenuRequest(exchange);        
			  else if(uri.startsWith("/api"))
					handleAPIRequest(exchange);
				else
				  handleFileRequest(exchange, uri);
			}
			else if(method.equals("POST")) {
				if(uri.startsWith("/terminate")) {
					exchange.sendResponseHeaders(200, 0);
					Main.shutdown();
					server.stop(0);
				}
				else {
					System.out.print("post ");
					copy(exchange.getRequestBody(), System.out);
					System.out.println("");
					exchange.sendResponseHeaders(200, 0);
				}
			}
			else {
				exchange.sendResponseHeaders(405, 0);				
			}		
		} catch(Exception e) {
		  Logger.error(e);
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
