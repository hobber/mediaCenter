package main.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPUtils {

	private static HttpClient client = HttpClientBuilder.create().build();
	private static String USER_AGENT = "MediaCenter/1.0";
	
	public static JSONObject sendHTTPGetRequest(String url) {	
		/*
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
		*/
		HttpGet request = new HttpGet(url);		
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept", "application/json");
		return sendHTTPGetRequest(request);
	}
	
	public static JSONObject sendHTTPGetRequest(HttpGet request) {		
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
		
		try {
			return new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch(IOException | JSONException e) {
			System.err.println("ERROR: Could not read response!");
			return null;
		}
	}
}
