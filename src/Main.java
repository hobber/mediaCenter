import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import oauth.Token;
import server.HTTPServer;
import spotify.OAuthAPISpotify;
import spotify.Spotify;
import utils.XMLFile;

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
		String authorizationURL = spotify.getAuthorizationRequestURL();
		try {
			Desktop.getDesktop().browse(new URI(authorizationURL));
		} catch(IOException | URISyntaxException e) {
			System.out.println("please visit:\n"+authorizationURL);
		}			
	}
}
