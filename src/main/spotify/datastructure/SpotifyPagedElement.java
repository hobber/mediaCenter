package main.spotify.datastructure;

import main.http.HTTPResponse;
import main.spotify.SpotifyAPIRequest;

public class SpotifyPagedElement extends SpotifyElement {
	
	private int pageCounter = 0;
	private int totalElements = 0;
	
	@Override
	protected void createAPIRequest(String path) {
		request = new SpotifyAPIRequest(path);
		request.setPage(pageCounter);
	}
	
	protected boolean handleAPIResponse(HTTPResponse response) {
		if(totalElements == 0)
			totalElements = Integer.parseInt(response.getResponseString("total"));
		
		int pageIndex = Integer.parseInt(response.getResponseString("offset")) / SpotifyAPIRequest.PAGE_SIZE;		
		if(pageIndex != pageCounter)
			return false;		
		return true;
	}
	
	public int getTotalElements() {
		return totalElements;
	}
}
