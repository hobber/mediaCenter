package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HTTPServer {

	static class SimpleHTTPHandler implements HttpHandler {
		
		private Map<String, HTTPListener> listeners = new HashMap<String, HTTPListener>();
		
		public void addListener(HTTPListener listener) {
			listeners.put(listener.getURLPath(), listener);
		}
		
		public void handle(HttpExchange exchange) throws IOException {
			String method = exchange.getRequestMethod();
			if(method.equals("GET"))
				handleGetRequest(exchange);
			else
				throw new RuntimeException("HTTPServer: unsupported method " + method);			
		}
		
		private Map<String, String> parseParameters(HttpExchange exchange) {
			Map<String, String> parameters = new HashMap<String, String>();
			String query = exchange.getRequestURI().getQuery();
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				if (pair.length > 1)
					parameters.put(pair[0], pair[1]);
				else
					parameters.put(pair[0], "");        
			}
			return parameters;
		}
		
		private void handleGetRequest(HttpExchange exchange) {								
			try {				
				Map<String, String> parameters = parseParameters(exchange);
				String path = exchange.getRequestURI().getPath().replaceFirst(CONTEXT, "");	
				
				if(listeners.containsKey(path) == false)
				{
					System.err.println("unknown path: " + path);
					return;
				}
				String response = listeners.get(path).handleHTTPGetRequest(parameters);
				
				/*
				String response = "<html>";
				response += "  <head><title>MediaCenter</title></head>";
				response += "  <body><h1>MediaCenter</h1>";
				response += "    <ul>";
				for (Map.Entry<String, String> entry : parameters.entrySet())				
					response += "      <li>"+entry.getKey() + " = " + entry.getValue() + "</li>";				
				response += "    </ul>";
				response += "  </body>";
				response += "</html>";
				*/
				
				exchange.sendResponseHeaders(200, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String USER_AGENT = "MediaCenter/1.0";
	private static final String CONTEXT = "/mediaCenter/";
	private com.sun.net.httpserver.HttpServer server;
	private SimpleHTTPHandler handler;
	private boolean online = false;

	public HTTPServer() {
		try {
			handler = new SimpleHTTPHandler();
			server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8080), 0);
			server.createContext(CONTEXT, handler);
			server.setExecutor(null);
			server.start();		
			online = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public void addListener(HTTPListener listener) {
		handler.addListener(listener);
	}
	
	public static JSONObject sendHTTPGetRequest(String url, boolean readResponse) {	
		try {
			URL resourceUrl = new URL(url);			
			HttpURLConnection conn = (HttpURLConnection)resourceUrl.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0...");
			switch (conn.getResponseCode())
	     {
	        case HttpURLConnection.HTTP_MOVED_PERM:
	        case HttpURLConnection.HTTP_MOVED_TEMP:
	        	System.out.println("LOCATION: "+ conn.getHeaderField("Location"));
	          break;
	        default:
	        	System.out.println("OK");
	     }
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		HttpGet request = new HttpGet(url);		
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept", "application/json");
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
			response = client.execute(request);			
		} catch (IOException e) {
			System.err.println("ERROR: Could not send the request!");
			return null;
		} 

		int responseCode = response.getStatusLine().getStatusCode(); 
		if(responseCode != 200)
		{
			System.err.println("Error: sending request failed (" + responseCode +
					" - " + response.getStatusLine().getReasonPhrase() + ")!");
			return null;
		}		
		
		if(readResponse == false)
			return null;
		
		try {
			return new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch(IOException | JSONException e) {
			System.err.println("ERROR: Could not read response!");
			return null;
		}
	}
}