package main.htmlParser;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.utils.Logger;

public class AustrianCharts {

	private class Entry {
		
		private String currentRanking;
		private String previousRanking;
		private String interpret;
		private String title;
		private boolean austrianSong;
		private String link;
		
		public Entry(String currentRanking, String previousRanking, String interpret, String title, boolean austrianSong, String link) {
			this.currentRanking = currentRanking;
			this.previousRanking = previousRanking;
			this.interpret = interpret;
			this.title = title;
			this.austrianSong = austrianSong;
		}
		
		public String getLink() {
			return link;
		}
		
		@Override
		public String toString() {
			return currentRanking + "(" + previousRanking + "): " + 
		         interpret + " - " + title + (austrianSong ? " [AT]" : "");
		}		
	}
	
	private LinkedList<Entry> charts = new LinkedList<Entry>();
	
	public AustrianCharts() {
		parse("http://austriancharts.at/weekchart.asp?cat=s");
	}
	
	public AustrianCharts(int year, int month, int date) {		
		parse(String.format("http://austriancharts.at/weekchart.asp?cat=s&date=%04d%02d%02d", year, month, date));
	}
	
	private boolean parse(String url) {
		HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
		String body = response.getHTMLBody();
		if(body.startsWith("<") == false) {
			Logger.error("AustrianCharts: invalid URL " + url);
			return false;
		}		

		body = body.substring(body.indexOf("<tr><td class=\"text\""));
		body = body.substring(0, body.indexOf("<!--"));
		while(true) {
			int indexStart = body.indexOf("<tr>");
			if(indexStart < 0)
				break;

			int indexEnd = body.indexOf("</tr>", indexStart);
			if(indexEnd < 0)
				break;

			String entry = body.substring(indexStart, indexEnd);
			body = body.substring(indexEnd);
			
			int indexCurrentRanking = getRegexPosition(entry, "<b>", 1) + 3;
			if(indexCurrentRanking < 3)
				break;
			
			int indexPreviousRanking = getRegexPosition(entry, "<b>", 2) + 3;
			if(indexPreviousRanking < 3)
				break;
			
			int indexInterpret = entry.indexOf("<a href=\"showitem.asp?interpret=");
			if(indexInterpret < 0)
				break;
			indexInterpret = entry.indexOf("<b>", indexInterpret) + 3;
			
			int indexTitle = entry.indexOf("<br>", indexInterpret) + 4;
			if(indexTitle < 0)
				break;
			
			int indexLink = getRegexPosition(entry, "onclick=\"location\\.href='[^>]", 1) + 24;
			if(indexLink < 24)
				break;			
			
			String currentRanking = entry.substring(indexCurrentRanking, entry.indexOf("</b>", indexCurrentRanking));
			String previousRanking = entry.substring(indexPreviousRanking, entry.indexOf("</b>", indexPreviousRanking));
			if(previousRanking.getBytes()[0] == '<') //<img src="/images/neu.gif" border=0>
				previousRanking = "-";
			else if(previousRanking.getBytes()[0] == 'R') //RE
				previousRanking = "-";
			
			String title = entry.substring(indexTitle, entry.indexOf("</a>", indexTitle));
			String interpret = entry.substring(indexInterpret, entry.indexOf("</b>", indexInterpret));
			boolean austrianSong = false;
			if(interpret.endsWith(" [AT]")) {
				interpret = interpret.substring(0, interpret.length() - 5);
				austrianSong = true;			
			}
			
			String link = entry.substring(indexLink, entry.indexOf("'", indexLink));
			
			charts.add(new Entry(currentRanking, previousRanking, interpret, title, austrianSong, link));			
		}		
		return true;
	}
	
	public void print() {
		for(Entry entry : charts)
			System.out.println(entry);
	}		
	
	private int getRegexPosition(String text, String regex, int index) {
		Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);

    for(int i=0; i<index; i++)
    	if(matcher.find() == false)
    		return -1;
    return matcher.start();
	}
}
