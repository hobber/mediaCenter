package main.tmdb.datastructure;

import main.data.DataBuffer;
import main.data.DataObject;
import main.http.HTTPResponse;
import main.tmdb.TMDBRequest;
import main.utils.JSONContainer;

import org.json.JSONArray;

public class TMDBSeries extends DataObject {
	
	public static TMDBRequest createRequest(int id) {
		TMDBRequest request = new TMDBRequest("tv/"+id);
		request.addQuery("language", "de");
		return request;
	}
	
	public TMDBSeries(JSONContainer series) {
		super(getClassName());
		System.out.println(series);
		schema.setValue("name", series.getString("name", ""));
//		genreList = new TMDBGenreList(series.getJSONArray("genres"));
		schema.setValue("numberOfSeasons", series.getInt("number_of_seasons", 0));
		schema.setValue("numberOfEpisodes", series.getInt("number_of_episodes", 0));
		schema.setValue("completed", (byte)(series.getBoolean("in_production", true) ? 1 : 0));
		schema.setValue("popularity", series.getFloat("popularity", 0.0f));
//		schema.setValue("posterId", new TMDBPoster(series.getResponseString("poster_path"));
		schema.setValue("summary", series.getString("overview", ""));		
	}
	
	public TMDBSeries(DataBuffer buffer) {	
		super(getClassName(), buffer);			
	}
	
	@Override
	protected void createSchema() {		
		schema.createSchemaString("name");
//		schema.createSchemaIdList("genreIds");		
		schema.createSchemaInt("numberOfSeasons");
		schema.createSchemaInt("numberOfEpisodes");
		schema.createSchemaByte("completed");
		schema.createSchemaFloat("popularity");
//		schema.createSchemaInt("posterId");
		schema.createSchemaString("summary");
	}
	
	public static String getClassName() {
		return TMDBSeries.class.getName();
	}
	
//	@Override
//	public String toString() {
//		String s = "Series:     " + name;
//		s     += "\nGenres:     " + genreList.toString();
//		s     += "\nSeasons:    " + numberOfSeasons;
//		s     += "\nEpisodes:   " + numberOfEpisodes;
//		s     += "\nCompleted:  " + (completed ? "yes" : "no");
//		s     += "\nPopularity: " + String.format("%.2f",popularity);
//		s     += "\nSummary:    " + summary;
//		return s;
//	}
}
