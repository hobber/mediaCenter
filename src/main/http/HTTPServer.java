package main.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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
				exchange.sendResponseHeaders(200, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

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
	
	public void stop() {
		server.stop(0);
	}
	
	public void addListener(HTTPListener listener) {
		handler.addListener(listener);
	}
}