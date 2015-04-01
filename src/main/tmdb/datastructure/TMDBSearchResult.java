package main.tmdb.datastructure;

public class TMDBSearchResult {

	private int id;
	private String description;
	private String posterPath;
	
	public TMDBSearchResult(int id, String description, String posterPath) {
		this.id = id;
		this.description = description;
		this.posterPath = posterPath;
	}
	
	@Override
	public String toString() {
		return String.format("%04d: %s", id, description);
	}
}
