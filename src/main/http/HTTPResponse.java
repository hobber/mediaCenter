package main.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import main.utils.JSONContainer;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPResponse {

	private JSONContainer jsonBody;
	private String htmlBody = "";
	private String error = "";
	private String htmlEncoding = null;

	public HTTPResponse(String error) {
		this.error = error;
	}
	
	public HTTPResponse(HttpResponse response) {
		int responseCode = response.getStatusLine().getStatusCode(); 
		if(responseCode != 200)		
			error = responseCode + " - " + response.getStatusLine().getReasonPhrase();					
		
		try {
			for(Header header : response.getAllHeaders()) {
				if(header.getName().equals("Content-Type")) {
					int index = header.getValue().indexOf("harset=");
					if(index >= 0) {
						htmlEncoding = header.getValue().substring(index+7);
						break;		
					}
				}			
			}
			htmlBody = EntityUtils.toString(response.getEntity(), htmlEncoding);
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
