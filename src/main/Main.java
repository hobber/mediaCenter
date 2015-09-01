package main;
import main.plugins.PluginController;
import main.plugins.creator.CreatorPlugin;
import main.plugins.ebay.EbayPlugin;
import main.server.Server;
import main.server.content.ContentImage;
import main.utils.ConfigFile;
import main.utils.Logger;

public class Main {
	
	private static void printUsage() {
		System.out.println("usage: [configFile=config.xml]");
	}
	
	public static void shutdown() {
	  PluginController.shutdown();
	  Logger.closeLogFile();
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
		
		Logger.setup(config.getElement("config.logger"));
		
		if(Server.run(config.getElement("config.server")) == false)
		  return;   
		
		try {
		  
//		  PluginController.register(new AustrianCharts());
		  PluginController.register(new CreatorPlugin(config.getElement("config.plugins.creator")));
//		  PluginController.register(new EbayPlugin(config.getElement("config.plugins.ebay")));
		  
		  
		  
		  PluginController.update();
		  
		} catch(Throwable e) {
			Logger.error(e);
		}

//		DataController dataController = new DataController(config.getElement("config.data"));		
//		TMDB tmdb = TMDB.create(config.getElement("config.tmdb"));				
		
//		List<TMDBSearchResult> seriesList = tmdb.searchSeries("Once upon a time");
//		for(TMDBSearchResult series : seriesList)
//			System.out.println(series);
		
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
