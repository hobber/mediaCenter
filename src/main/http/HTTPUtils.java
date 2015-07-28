package main.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import main.utils.Logger;

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
	
	public static String replaceSpaces(String term) {
	  return term.replaceAll(" ", "+");
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
			else {
			  
				s += String.format("%%%02X", c < 0 ? (int)(c+256) : (int)c);
			}
		}
		return s;
	}
	
	public static Map<String, String> splitQueryParameters(String parameters) throws UnsupportedEncodingException {
	  final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
	  if(parameters == null)
	    return query_pairs;
	  
	  final String[] pairs = parameters.split("&");
	  for (String pair : pairs) {
	    final int idx = pair.indexOf("=");
	    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
	    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
      if(query_pairs.put(key, value) != null)
        Logger.error("parameter string contains parameter " + key + " multiple times");
	  }
	  return query_pairs;
	}
	
	public static void saveWebImage(URL url, String fileName) throws IOException {
    InputStream is = url.openStream();
    OutputStream os = new FileOutputStream(fileName);

    byte[] b = new byte[2048];
    int length;

    while ((length = is.read(b)) != -1) {
      os.write(b, 0, length);
    }

    is.close();
    os.close();
  }
}
