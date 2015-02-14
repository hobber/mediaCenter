package main.http;

import java.util.Map;

public abstract class HTTPListener {
	
	private String urlPath;
	
	public HTTPListener(HTTPServer server, String urlPath) {
		this.urlPath = urlPath;
		server.addListener(this);
	}
	
	public String getURLPath() {
		return urlPath;
	}
	
	public abstract String handleHTTPGetRequest(Map<String, String> parameters);
}
