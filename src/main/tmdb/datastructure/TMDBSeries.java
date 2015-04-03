package main.tmdb.datastructure;

import java.util.LinkedList;
import java.util.List;

import main.data.DataBuffer;
import main.data.DataObject;
import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentText;
import main.server.content.ContentObject;
import main.tmdb.TMDB;
import main.tmdb.TMDBRequest;
import main.utils.JSONArray;
import main.utils.JSONContainer;

public class TMDBSeries extends DataObject implements ContentObject {
	
	public static TMDBRequest createRequest(int id) {
		TMDBRequest request = new TMDBRequest("tv/"+id);
		request.addQuery("language", "de");
		return request;
	}
	
	public TMDBSeries(JSONContainer series) {
		super(getClassName());
		
		schema.setValue("tmdbId", series.getInt("id", 0));
		schema.setValue("name", series.getString("name", ""));
		schema.setValue("numberOfSeasons", series.getInt("number_of_seasons", 0));
		schema.setValue("numberOfEpisodes", series.getInt("number_of_episodes", 0));
		schema.setValue("completed", (byte)(series.getBoolean("in_production", true) ? 0 : 1));
		schema.setValue("popularity", series.getFloat("vote_average", 0.0f));		
		schema.setValue("posterPath", series.getString("poster_path", ""));
		schema.setValue("summary", series.getString("overview", ""));
		schema.setValue("homepage", series.getString("homepage", ""));
		
		JSONArray runtimes = series.getArray("episode_run_time");
		short minRunTime = -1, maxRunTime = -1;
		for(int i=0; i<runtimes.length(); i++) {
			short runtime = runtimes.getShort(i, (short)0);
			if(minRunTime < 0 || runtime < minRunTime)
				minRunTime = runtime;
			if(maxRunTime < 0 || runtime > maxRunTime)
				maxRunTime = runtime;
		}
		schema.setValue("minRunTime", minRunTime);
		schema.setValue("maxRunTime", maxRunTime);
			
		
		JSONArray genres = series.getArray("genres");
		LinkedList<Integer> genreList = new LinkedList<Integer>();
		for(int i=0; i<genres.length(); i++)
			genreList.add(genres.getContainer(i).getInt("id", 0));
    schema.setValue("genreIds", genreList);
	}
	
	public TMDBSeries(DataBuffer buffer) {	
		super(getClassName(), buffer);			
	}
	
	@Override
	protected void createSchema() {
		schema.createSchemaInt("tmdbId");
		schema.createSchemaString("name");			
		schema.createSchemaInt("numberOfSeasons");
		schema.createSchemaInt("numberOfEpisodes");
		schema.createSchemaShort("minRunTime");
		schema.createSchemaShort("maxRunTime");
		schema.createSchemaByte("completed");
		schema.createSchemaFloat("popularity");
		schema.createSchemaString("posterPath");
		schema.createSchemaString("summary");
		schema.createSchemaString("homepage");
		schema.createSchemaIdList("genreIds");
	}
	
	@Override
	public ContentGroup getContentGroup() {
		ContentGroup group = new ContentGroup();
		group.put(new ContentImage(0, 0, 300, 450, TMDB.getPosterURL(schema.getString("posterPath"), false)));		
		
		int x = 330, y = 20, stepY = 23;
		group.put(new ContentText(x, y, schema.getString("name"), ContentText.TextType.TITLE));
		y += stepY * 2;
		
		String genres = "Genres: ";
		List<Integer> genreIds = schema.getIdList("genreIds");
		for(int i=0; i<genreIds.size(); i++)
			genres += (i > 0 ? ", " : "") + TMDB.getGenreName(genreIds.get(i));
		group.put(new ContentText(x, y, genres)); 
		y += stepY;
				
		short minRunTime = schema.getShort("minRunTime");
		short maxRunTime = schema.getShort("maxRunTime");
		if(minRunTime > 0) {
			if(minRunTime == maxRunTime)
				group.put(new ContentText(x, y, "Dauer: " + minRunTime + "min"));
			else
				group.put(new ContentText(x, y, "Dauer: " + minRunTime + "-" + maxRunTime + "min"));
			y += stepY;
		}
		
		int numberOfSeasons = schema.getInt("numberOfSeasons");
		int numberOfEpisodes = schema.getInt("numberOfEpisodes");
		boolean completed = schema.getByte("completed") > 0 ? true : false;		
		group.put(new ContentText(x, y, "Folgen: " + numberOfEpisodes + (completed ? "" : "+") + 
				                            " in " + numberOfSeasons + " Staffel" + (numberOfSeasons > 0 ? "n" : "")));
		y += stepY;
		
		group.put(new ContentText(x, y, "Popularity: " + Math.round(schema.getFloat("popularity")*10.0)/10.0));
		y += stepY;
	  
	  String homepage = schema.getString("homepage");
	  if(homepage.length() > 0) {
	  	group.put(new ContentText(x, y, "Homepage: ", homepage));
	  	y += stepY;
	  }
				
		group.put(new ContentText(x, y+stepY, "Inhalt: " + schema.getString("summary"), ContentText.TextType.BLOCK));
		return group;
	}
	
	public static String getClassName() {
		return TMDBSeries.class.getName();
	}	
}
