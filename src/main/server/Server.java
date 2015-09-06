package main.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import main.Main;
import main.http.HTTPUtils;
import main.plugins.PluginController;
import main.server.content.ContentItem;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;
import main.utils.Logger;

import org.apache.commons.io.IOUtils;
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
	
	private void handleMenuRequest(HttpExchange exchange) throws IOException {
	  List<ContentMenuEntry> list = PluginController.getMenuEntries();
	  
	  Headers headers = exchange.getResponseHeaders();
    headers.add("Content-Type", "application/jsonp; charset=ISO-8859-1");
    
	  JSONObject buffer = new JSONObject();
	  for(ContentMenuEntry entry : list)
	    buffer.put(entry.getName(), entry.toJSON());
	  
    byte[] response = buffer.toString().getBytes();
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
    os.write(response);   
    os.close();  
	}
	
	private void handleAPIGetRequest(HttpExchange exchange, RequestParameters parameters) throws IOException {
		ContentItem item = PluginController.handleAPIRequest(parameters);
		
		Headers headers = exchange.getResponseHeaders();
    headers.add("Content-Type", "application/jsonp; charset=ISO-8859-1");    
		byte[] response = item.toString().getBytes();
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response);		
		os.close();
	}
	
	private void handleAPIPostRequest(HttpExchange exchange, RequestParameters parameters) throws IOException {    
    PluginController.handleAPIRequest(parameters);
    
    byte[] response = "OK".getBytes();
    Headers headers = exchange.getResponseHeaders();
    headers.set("Content-Type","text/plain");
    exchange.sendResponseHeaders(200, 2);
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
	
	private void handleOauthResponse(HttpExchange exchange) throws IOException {
    String msg = "hello";
    byte[] response = msg.getBytes();
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
			System.out.println("URI: " + uri + " (" + method + ")");
			
			RequestParameters parameters = new RequestParameters(uri);
			
			if(method.equals("GET")) {
			  if(uri.startsWith("/menu")) {
          handleMenuRequest(exchange);
			  }
			  else if(uri.startsWith("/oauth/")) {
			    handleOauthResponse(exchange);
			  }
			  else if(parameters.contains("plugin") && parameters.contains("page")) {
					handleAPIGetRequest(exchange, parameters);
			  }
				else {
				  handleFileRequest(exchange, uri);
				}
			}
			else if(method.equals("POST")) {
			  if(uri.startsWith("/terminate")) {
					exchange.sendResponseHeaders(200, 0);
					Main.shutdown();
					server.stop(0);
				}
				else if(uri.startsWith("/error")) {				  
				  Logger.error(parameters.get("file") + ":" + parameters.get("line") + " - " + IOUtils.toString(exchange.getRequestBody()));
				  exchange.sendResponseHeaders(200, 0);
				}
				else if(parameters.contains("plugin") && parameters.contains("page")) {
				  parameters.add(IOUtils.toString(exchange.getRequestBody()));
          handleAPIPostRequest(exchange, parameters);
				}
				else {
				  Logger.error("unhandled post: " + uri + " " + IOUtils.toString(exchange.getRequestBody()));
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
}
