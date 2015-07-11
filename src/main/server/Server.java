package main.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Main;
import main.http.HTTPUtils;
import main.plugins.PluginController;
import main.server.content.ContentItem;
import main.server.content.UserContentGroup;
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
	
	private void handleAPIGetRequest(HttpExchange exchange, Map<String, String> parameters) throws IOException {
		ContentItem item = PluginController.handleAPIRequest(parameters);
		
		Headers headers = exchange.getResponseHeaders();
    headers.add("Content-Type", "application/jsonp; charset=ISO-8859-1");
		byte[] response = item.getContentString().getBytes();
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response);		
		os.close();
	}
	
	private void handleAPIPostRequest(HttpExchange exchange, Map<String, String> parameters) throws IOException {    
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
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			String method = exchange.getRequestMethod();
			String uri = exchange.getRequestURI().toString();
			System.out.println("URI: " + uri + " (" + method + ")");
			
			if(uri.startsWith("error?")) {
			  Logger.error(uri);
			  return;
			}
			
			int parametersStart = uri.indexOf('?') + 1;
			if(parametersStart <= 0)
			  parametersStart = 1;
			Map<String, String> parameters = HTTPUtils.splitQueryParameters(uri.substring(parametersStart));			

			if(method.equals("GET")) {
			  if(uri.startsWith("/menu"))
          handleMenuRequest(exchange);        
			  else if(parameters != null && parameters.containsKey("plugin") && parameters.containsKey("page"))
					handleAPIGetRequest(exchange, parameters);					
				else
				  handleFileRequest(exchange, uri);
			}
			else if(method.equals("POST")) {
				if(uri.startsWith("/terminate")) {
					exchange.sendResponseHeaders(200, 0);
					Main.shutdown();
					server.stop(0);
				}
				else if(uri.startsWith("/error")) {
				  System.err.print("ERROR: " + uri.substring(6) + " ");
				  copy(exchange.getRequestBody(), System.out);
				  System.err.println("");
				}
				else if(parameters != null && parameters.containsKey("plugin") && parameters.containsKey("page")) {				  
				  parameters.putAll(HTTPUtils.splitQueryParameters(IOUtils.toString(exchange.getRequestBody())));
          handleAPIPostRequest(exchange, parameters);
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
