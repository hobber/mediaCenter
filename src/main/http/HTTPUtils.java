package main.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
			return new HTTPResponse(e.getMessage());
		}
	}
	
	public static HTTPResponse sendHTTPPostRequest(HttpPost request) {				
		try {
			return new HTTPResponse(client.execute(request));			
		} catch (IOException e) {
			System.err.println("ERROR: Could not send the request (" + e.getMessage() + ")");
			e.printStackTrace();
			return new HTTPResponse(e.getMessage());
		}
	}
	
	//TODO: check with http://www.w3schools.com/tags/ref_urlencode.asp
	public static String encodeTerm(String term) {
		String s = "";
		byte[] bytes = Charset.forName("UTF-8").encode(term).array();
		for(int i=0; i<bytes.length; i++) {
			byte c = bytes[i];
			if(c == 0)
				continue;
			if(Character.isLetterOrDigit(c) && (int)c <= 127)
				s += (char)c;
			else if(c == '.')
				s += '.';
			else if(c == ' ')
				s += '+';
			else
				s += String.format("%%%02X", c < 0 ? (int)(c+256) : (int)c);			
		}
		return s;
	}
}
