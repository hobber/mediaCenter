package main.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import main.utils.JSONContainer;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPResponse {

	private JSONContainer jsonBody;
	private String htmlBody = "";
	private String error = "";

	public HTTPResponse(HttpResponse response) {
		int responseCode = response.getStatusLine().getStatusCode(); 
		if(responseCode != 200)		
			error = responseCode + " - " + response.getStatusLine().getReasonPhrase();					
		
		try {
			InputStream inputstream = response.getEntity().getContent();
			Scanner scanner = new java.util.Scanner(inputstream);
			Scanner s = scanner.useDelimiter("\\A");
	    htmlBody =  s.hasNext() ? s.next() : "";
	    scanner.close();
	    jsonBody = new JSONContainer(new JSONObject(htmlBody));	    
		} catch(IOException | IllegalStateException | JSONException e) {			
		}
		
 		try {
 			if(jsonBody == null)
 				jsonBody = new JSONContainer(new JSONObject(EntityUtils.toString(response.getEntity())));			
		} catch(IOException | JSONException e) {			
		}
	}
	
	public boolean hasHTMLBody() {
		return htmlBody.length() > 0;
	}
	
	public String getHTMLBody() {
		return htmlBody;
	}
	
	public boolean hasJSONBody() {
		return jsonBody != null;
	}
	
	public JSONContainer getJSONBody() {
		return jsonBody;
	}

	public boolean failed() {
		return error.length() != 0;
	}
	
	public String getError() {
		return error;
	}	
}
