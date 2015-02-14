package main;
import java.util.concurrent.TimeoutException;

import main.http.HTTPServer;
import main.oauth.OAuthTokenRefresher;
import main.spotify.Spotify;
import main.spotify.datastructure.SpotifyPlayList;
import main.spotify.datastructure.SpotifySong;
import main.spotify.datastructure.SpotifyUser;
import main.spotify.datastructure.SpotifyUserPlayListList;
import main.utils.XMLFile;

public class Main {
	
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
		
		config.clear();
		spotify.storeConfig(config);
		config.write();		
		
	  OAuthTokenRefresher.stop();
		server.stop();
	}
}