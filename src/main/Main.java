package main;
import java.util.List;

import main.data.DataController;
import main.data.DataObject;
import main.data.DataQuery;
import main.http.HTTPUtils;
import main.server.Server;
import main.tmdb.TMDB;
import main.tmdb.datastructure.TMDBSearchResult;
import main.tmdb.datastructure.TMDBSeries;
import main.utils.ConfigFile;

public class Main {

//	private static void readAustrianCharts() {
//		HTTPResponse response = HTTPUtils.sendHTTPGetRequest("http://www.austriancharts.at/weekchart.asp?cat=s");
//		String body = response.getBody();
//		body = body.substring(body.indexOf("<tr><td class=\"text\""));
//		body = body.substring(0, body.indexOf("<!--"));
//		while(true) {
//			int indexStart = body.indexOf("<tr>");
//			if(indexStart < 0)
//				break;
//			
//			int indexEnd = body.indexOf("</tr>", indexStart);
//			if(indexEnd < 0)
//				break;
//			
//			String entry = body.substring(indexStart, indexEnd);
//			body = body.substring(indexEnd);
//			
//			int indexInterpret = entry.indexOf("interpret=");
//			if(indexInterpret < 0)
//				continue;
//			int indexTitle = entry.indexOf("&titel=", indexInterpret);
//			if(indexTitle < 0)
//				continue;
//			int indexCategory = entry.indexOf("&cat=", indexTitle);
//			if(indexCategory < 0)
//				continue;
//			
//			String artist = entry.substring(indexInterpret+10, indexTitle);
//			String title = entry.substring(indexTitle+7, indexCategory);
//			System.out.println(artist + " - " + title);						
//		}
//	}
	
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
		
		ConfigFile config = new ConfigFile(configFile);
		if(config.read() == false) {
			return;
		}
						
		DataController dataController = new DataController(config.getElement("config.data"));		
		TMDB tmdb = new TMDB(config.getElement("config.tmdb"));		
		PluginController.startPlugins();
		
		List<TMDBSearchResult> seriesList = tmdb.searchSeries("Once upon a time");
		for(TMDBSearchResult series : seriesList)
			System.out.println(series);
		
		if(Server.run(config.getElement("config.server")) == false)
			return;		
		
//		TMDBSeries series = tmdb.getSeries(39272);
//		System.out.println(series);
////		dataController.add(series);
//		DataQuery query = new DataQuery(TMDBSeries.getClassName());
//		query.addQuery("name", "Once Upon a Time");
//		List<DataObject> selectedObjects = dataController.select(query);		
//		System.out.println("Selection:");
//		for(DataObject object : selectedObjects)
//			System.out.println(object);
		
		
/*  DataSeries series = new DataSeries("Vorstadtweiber");
		//dataController.add(series);
		DataQuery query = new DataQuery(DataSeries.getClassName());
		query.addQuery("name", "Once upon a time");
		List<DataObject> selectedObjects = dataController.select(query);		
		System.out.println("Selection:");
		for(DataObject object : selectedObjects)
			System.out.println(object);*/
		

		//UserContentGroup group = new UserContentGroup("Series", "content/series.png");
		//group.addPage(new SeriesLibraryPage());
		//registerUserContentGroup(group);
		//
		
	
//		HTTPServer server = new HTTPServer();
//		if(server.isOnline() == false) {
//			System.err.println("ERROR: server could not be startet!");
//			return;
//		}
		
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
	//	server.stop();		
	}
}
