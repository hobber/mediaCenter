package http;

import java.util.Map;

public abstract class HTTPListener {
	
	private String urlPath;
	
	public HTTPListener(String urlPath) {
		this.urlPath = urlPath;
	}
	
	public String getURLPath() {
		return urlPath;
	}
	
	public abstract String handleHTTPGetRequest(Map<String, String> parameters);
}
