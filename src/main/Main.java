package main;
import java.util.concurrent.TimeoutException;

import main.http.HTTPResponse;
import main.http.HTTPServer;
import main.http.HTTPUtils;
import main.media.MP3Player;
import main.oauth.OAuthTokenRefresher;
import main.spotify.Spotify;
import main.spotify.datastructure.SpotifyPlayList;
import main.spotify.datastructure.SpotifySong;
import main.spotify.datastructure.SpotifyUser;
import main.spotify.datastructure.SpotifyUserPlayListList;
import main.tmdb.TMDB;
import main.utils.XMLFile;

public class Main {

	private static void readAustrianCharts() {
		HTTPResponse response = HTTPUtils.sendHTTPGetRequest("http://www.austriancharts.at/weekchart.asp?cat=s");
		String body = response.getBody();
		body = body.substring(body.indexOf("<tr><td class=\"text\""));
		body = body.substring(0, body.indexOf("<!--"));
		while(true) {
			int indexStart = body.indexOf("<tr>");
			if(indexStart < 0)
				break;
			
			int indexEnd = body.indexOf("</tr>", indexStart);
			if(indexEnd < 0)
				break;
			
			String entry = body.substring(indexStart, indexEnd);
			body = body.substring(indexEnd);
			
			int indexInterpret = entry.indexOf("interpret=");
			if(indexInterpret < 0)
				continue;
			int indexTitle = entry.indexOf("&titel=", indexInterpret);
			if(indexTitle < 0)
				continue;
			int indexCategory = entry.indexOf("&cat=", indexTitle);
			if(indexCategory < 0)
				continue;
			
			String artist = entry.substring(indexInterpret+10, indexTitle);
			String title = entry.substring(indexTitle+7, indexCategory);
			System.out.println(artist + " - " + title);						
		}
	}
	
	private static void printUsage() {
		System.out.println("usage: [configFile=config.xml]");
	}
	
	public static void main(String[] args) {
		
		String configFile = "config.xml";
		
		if(args.length == 1)
			configFile = args[0];
		else if(args.length > 1) {
			printUsage();
			return;
		}
		
		XMLFile config = new XMLFile(configFile);
		if(config.read() == false) {
			return;
		}		
	
		HTTPServer server = new HTTPServer();
		if(server.isOnline() == false) {
			System.err.println("ERROR: server could not be startet!");
			return;
		}
		
		TMDB tmdb = new TMDB(config.getElement("config.tmdb")); 
		
/*		
		Spotify spotify = new Spotify(server, config);
		try {
			spotify.getAuthorization();
		} catch(TimeoutException e) {
			System.err.println("ERROR: Spotify could not get authorization");
		}
		
		SpotifyUser user = spotify.getCurrentUser();
		System.out.println("USER: " + user);
		SpotifyUserPlayListList playlists = spotify.getUserPlayListList();
		if(playlists.size() == 0) {
			System.out.println("no playlists found");
			return;
		}
		
		for(int i=0; i<playlists.size(); i++) {
			SpotifyPlayList playlist = playlists.getPlayList(i);
			if(playlist.size() == 0)
				continue;
			
			SpotifySong song = playlist.getSong(0);
			System.out.println("SONG: "+song);
			System.out.println("ALBUM: "+spotify.getAlbum(song.getAlbumId()));
			break;
		}
		
		new MP3Player("media/Adele - Someone Like You.mp3");//.play();
		new MP3Player("music/Echt - 2010.mp3");//.play();

		config.clear();
		spotify.storeConfig(config);
		config.write();		
		
	  OAuthTokenRefresher.stop();
*/
		server.stop();		
	}
}
