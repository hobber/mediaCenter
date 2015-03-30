package main.http;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class HTTPUtils {

	private static HttpClient client = HttpClientBuilder.create().build();
	private static String USER_AGENT = "MediaCenter/1.0";
	
	public static HTTPResponse sendHTTPGetRequest(String url) {		
		HttpGet request = new HttpGet(url);		
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept", "application/json");
		return sendHTTPGetRequest(request);
	}
	
	public static HTTPResponse sendHTTPGetRequest(HttpGet request) {				
		try {
			return new HTTPResponse(client.execute(request));			
		} catch (IOException e) {
			System.err.println("ERROR: Could not send the request!");
			return null;
		}
	}
}
