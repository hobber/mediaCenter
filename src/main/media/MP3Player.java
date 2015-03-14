package main.media;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javazoom.jl.player.Player;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

/**
 * based on http://introcs.cs.princeton.edu/java/faq/mp3/MP3.java.html
 * using JLayer
 */
public class MP3Player {
	
	private String fileName;
	private Player player; 

	public MP3Player(String fileName) {
		this.fileName = fileName;
		
		try {
			MP3File file = new MP3File(fileName);
			if(file.hasID3v1Tag() == false)
				return;
			AbstractID3v2 tags = file.getID3v2Tag();	
			
			System.out.println("artist: " + tags.getLeadArtist());
			System.out.println("title:  " + tags.getSongTitle());
			System.out.println("album:  " + tags.getAlbumTitle());
			System.out.println("number: " + tags.getTrackNumberOnAlbum());
			System.out.println("year:   " + tags.getYearReleased());
			System.out.println("genre:  " + tags.getSongGenre());
		} catch(TagException | IOException e) {
			e.printStackTrace();
		}
	}

	public void close() { 
		if (player != null) 
			player.close(); 
	}

	public void play() {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			player = new Player(bis);						
		}
		catch (Exception e) {
			System.err.println("ERROR: failed to read file " + fileName);			
		}

		// run in new thread to play in background
		new Thread() {
			public void run() {
				try { 
					player.play(); 
				}
				catch (Exception e) { 
					System.err.println("ERROR: failed to play file " + fileName); 
				}
			}
		}.start();
	}
}