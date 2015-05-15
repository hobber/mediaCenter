package main.htmlParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.utils.Logger;
import main.utils.XMLElement;
import main.utils.XMLParser;

public class AustrianCharts {  
  
	private class Entry {
		
		private String currentRanking;
		private String previousRanking;
		private String interpret;
		private String title;
		private boolean austrianSong;
		
		public Entry(String currentRanking, String previousRanking, String interpret, String title) {
			this.currentRanking = currentRanking;
			this.previousRanking = previousRanking;
			this.interpret = interpret;
			this.title = title;
		}
		
		@Override
		public String toString() {
			return currentRanking + " (" + previousRanking + "): " + interpret + " - " + title;
		}		
	}
	
	private LinkedList<Entry> charts = new LinkedList<Entry>();
	
	public AustrianCharts() {
		read("http://austriancharts.at/charts/singles");
	}
	
	public AustrianCharts(int year, int month, int date) {		
		read(String.format("http://austriancharts.at/charts/singles/%02d-%02d-%04d", date, month, year));
	}
	
	private boolean read(String url) {
	  HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    String body = response.getHTMLBody();
    if(body.startsWith("<") == false) {
      Logger.error("AustrianCharts: invalid URL " + url);
      return false;
    }
    
    XMLElement rootElement = XMLParser.parse(body, false);    
    
    List<XMLElement> list = rootElement.searchTags("<tr.*class[ ]*=[ ]*\"charts( new)?\".*>");
    if(list.size() == 0) {
      Logger.error("AustrianCharts: failed to read charts from URL " + url);
      return false;
    }
    
    for(XMLElement tag : list) {
      List<XMLElement> columns = tag.getChildren();
      String currentRanking = columns.get(0).getChild(0).getValue();
      String previousRanking = columns.get(1).getChild(0).getValue();
      if(previousRanking == null)
        previousRanking = "NEW";
      XMLElement caption = columns.get(4).getChild(0);
      String interpret = caption.getChild(0).getChild(0).getValue();
      String title = caption.getChild(2).getValue();
      if(title == null)
        title = caption.getChild(3).getValue();      
      charts.add(new Entry(currentRanking, previousRanking, interpret, title));
    }
    return true;
	}
	
	public void print() {
		for(Entry entry : charts)
			System.out.println(entry);
	}
}
