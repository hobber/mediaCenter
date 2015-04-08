package main.tmdb.datastructure;

import java.util.Comparator;

import main.utils.JSONContainer;

public class TMDBCharacter {

	public static final Comparator<TMDBCharacter> COMPARATOR = new Comparator<TMDBCharacter>() {
		@Override
	  public int compare(TMDBCharacter lhs, TMDBCharacter rhs) {
		  return lhs.order - rhs.order;
	  }
	};
	
	private int id;
	private String character;
	private String name;
	private String profilePath;
	private int order;
	
	public TMDBCharacter(JSONContainer character) {
		this.id = character.getInt("id", -1);
		this.character = character.getString("character", null);
		this.name = character.getString("name", null);
		
		if(this.id < 0 || this.character == null || this.name == null)
			throw new RuntimeException("could not get information about a character.");
		
		this.profilePath = character.getString("profile_path", "");
		this.order = character.getInt("order", 0);
	}
	
	public String getCharacterName() {
		return character;
	}
	
	public String getActorName() {
		return name;
	}
	
	public int getActorId() {
		return id;
	}
	
	public String getProfilePath() {
		return profilePath;
	}	
	
	@Override
	public String toString() {
		return character + " (" + name + ")";
	}
}
