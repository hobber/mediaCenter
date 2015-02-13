package main.spotify.datastructure;

import main.spotify.SpotifyAPIRequest;

import org.json.JSONObject;

public class SpotifyPagedElement extends SpotifyElement {
	
	private int pageCounter = 0;
	private int totalElements = 0;
	
	@Override
	protected void createAPIRequest(String path) {
		request = new SpotifyAPIRequest(path);
		request.setPage(pageCounter);
	}
	
	protected boolean handleAPIResponse(JSONObject response) {
		if(totalElements == 0)
			totalElements = Integer.parseInt(getResponseString(response, "total"));
		
		int pageIndex = Integer.parseInt(getResponseString(response, "offset")) / SpotifyAPIRequest.PAGE_SIZE;		
		if(pageIndex != pageCounter)
			return false;		
		return true;
	}
	
	public int getTotalElements() {
		return totalElements;
	}
}
