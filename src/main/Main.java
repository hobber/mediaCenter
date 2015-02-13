package main;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import main.http.HTTPServer;
import main.spotify.Spotify;
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
		if(server.isOnline() == false)
			return;
		
		Spotify spotify = new Spotify(config);
		server.addListener(spotify);
		if(spotify.hasValidAuthorizationToken() == false) {			
			String authorizationURL = spotify.getAuthorizationRequestURL();
			try {
				Desktop.getDesktop().browse(new URI(authorizationURL));
			} catch(IOException | URISyntaxException e) {
				System.out.println("please visit:\n"+authorizationURL);
			}	

			while(spotify.isReady() == false) {
				try {
					Thread.sleep(500);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		/*
		//SpotifyUser user = spotify.getCurrentUser();
		//System.out.println(user);
		SpotifyUserPlayListList playlist = spotify.getPlayList();
		*/
		spotify.storeConfig(config);
		config.write();
		
		server.stop();
	}
}
