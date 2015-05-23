package main.plugins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import main.data.DataCollection;
import main.data.DataObject;
import main.data.DataSchema;
import main.data.datatypes.MCInteger;
import main.data.datatypes.MCString;
import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.utils.ConfigFile;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
import main.utils.XMLElement;
import main.utils.XMLParser;

public class AustrianCharts implements DataCollection {  
  
  private static class Ranking {
    
    private Calendar date;
    private String currentRanking;
    private String previousRanking;
    
    public Ranking(Calendar date, String currentRanking, String previousRanking) {
      this.date = (Calendar)date.clone();
      this.currentRanking = currentRanking;
      this.previousRanking = previousRanking;
    }
    
    public Calendar getDate() {
      return date;
    }
    
    public String getCurrentRanking() {
      return currentRanking;
    }
    
    public String getPreviousRanking() {
      return previousRanking;      
    }
    
    @Override
    public String toString() {
      return String.format("%s %2s (%3s)", ConfigFile.dateToString(date), currentRanking, previousRanking);
    }
  }
  
	private static class Entry extends DataObject {
		
	  private int hashCode;
		private MCString interpret;
		private MCString title;
		private MCString detailUrl;
		private MCInteger score;
		private LinkedList<Ranking> ranking = new LinkedList<Ranking>();
		
		private String videoUrl;
		
		public Entry(Calendar date, XMLElement tag) {		  
		  List<XMLElement> columns = tag.getChildren();
      String currentRanking = columns.get(0).getChild(0).getValue();
      String previousRanking = columns.get(1).getChild(0).getValue();
      if(previousRanking == null)
        previousRanking = "NEW";
      ranking.add(new Ranking(date, currentRanking, previousRanking));
      XMLElement caption = columns.get(4).getChild(0);
      detailUrl.set("http://austriancharts.at" + caption.getAttribute("href"));
      interpret.set(caption.getChild(0).getChild(0).getValue());
      String title = caption.getChild(2).getValue();
      if(title == null)
        title = caption.getChild(3).getValue();
      this.title.set(title);
      hashCode = (interpret.get() + title).hashCode();
      score.set(76 - Integer.parseInt(currentRanking));
		}
		
		public Entry(FileReader file) throws IOException {
		  super(file);
		  hashCode = (interpret.get() + title.get()).hashCode();
		}
		
		public void write(FileWriter file) throws IOException {
		  schema.writeValues(file);
		}
		
		public void addRanking(Ranking ranking) {
		  if(this.ranking.getFirst().getDate().compareTo(ranking.getDate()) < 0)
		    this.ranking.addFirst(ranking);
		  else if(this.ranking.getLast().getDate().compareTo(ranking.getDate()) > 0)
		    this.ranking.addLast(ranking);
		  else {
		    this.ranking.add(ranking);
		    sortRanking();
		  }
		  
		  score.add(76 - Integer.parseInt(ranking.getCurrentRanking()));
		}
		
		public String getInterpret() {
		  return interpret.get();
		}
		
		public String getTitle() {
		  return title.get();
		}
		
		public int getScore() {
		  return score.get();
		}
		
		public Ranking getLatestRanking() {
		  return ranking.getFirst();
		}
		
		public void loadDetails() {
		  HTTPResponse response = HTTPUtils.sendHTTPGetRequest(detailUrl.get());
      String body = response.getHTMLBody();
      
      XMLElement rootElement = XMLParser.parse(body, false);
      List<XMLElement> videos = rootElement.searchTags("<iframe[^>]* src=\"https://www.youtube.com[^>]*>");
      if(videos.isEmpty() == false) {
        videoUrl = videos.get(0).getAttribute("src");
        int index = videoUrl.indexOf('?');
        if(index >= 0)
          videoUrl = videoUrl.substring(0, index);
      }
		}		
		
		void printRankingHistory() {
		  for(Ranking rank : ranking)
		    System.out.println(rank);
		}
		
		@Override
    public String toString() {
		  if(ranking.isEmpty())
		    return "? (  ?): " + interpret + " - " + title;
      Ranking currentRanking = ranking.getLast();
      return currentRanking.getCurrentRanking() + " (" + currentRanking.getPreviousRanking() + "): " + interpret + " - " + title;
    }
    
		@Override
		public int hashCode() {
		  return hashCode;
		}
		
		private void sortRanking() {
		  Collections.sort(ranking, new Comparator<Ranking>() {
        @Override
        public int compare(Ranking  lhs, Ranking  rhs) {
            return  lhs.date.compareTo(rhs.date);
        }
		  });
		}
		
		@Override
		protected DataSchema createDataSchema() {
		  interpret = new MCString();
	    title = new MCString();
	    detailUrl = new MCString();
	    score = new MCInteger();
		  
	    DataSchema schema = new DataSchema();
	    schema.addString("interpret", interpret);
	    schema.addString("title", title);
	    schema.addString("detailUrl", detailUrl);
	    schema.addInt("score", score);
	    return schema;
	  }
	}
	
	private LinkedList<Entry> charts = new LinkedList<Entry>();
	
	public AustrianCharts() {	  
	}
	
	public AustrianCharts(Calendar date) {	  
	  int day = date.get(Calendar.DATE);
	  int month = date.get(Calendar.MONTH) + 1;
	  int year = date.get(Calendar.YEAR);
	  String url = String.format("http://austriancharts.at/charts/singles/%02d-%02d-%04d", day, month, year);

	  HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    String body = response.getHTMLBody();
    XMLElement rootElement = XMLParser.parse(body, false);    
    
    List<XMLElement> list = rootElement.searchTags("<tr.+class=\"charts( new)?\".*>");
    if(list.size() == 0) {
      Logger.error("AustrianCharts: failed to read charts from URL " + url);
      return;
    }
    
    for(XMLElement tag : list)     
      charts.add(new Entry(date, tag));
    System.out.println("history");
    charts.getFirst().printRankingHistory();    
	}
	
	public void updateDatabase() {
	  HashMap<Integer, Entry> singleMap = new HashMap<Integer, Entry>();
	  Calendar date = Calendar.getInstance();
	  while(date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
	    date.add(Calendar.DATE, -1);
	  
	  for(int w=0; w<5; w++) {	    
	    int day = date.get(Calendar.DATE);
	    int month = date.get(Calendar.MONTH) + 1;
	    int year = date.get(Calendar.YEAR);
	    String url = String.format("http://austriancharts.at/charts/singles/%02d-%02d-%04d", day, month, year);
	    System.out.println(url);
	    
	    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
	    String body = response.getHTMLBody();
	    XMLElement rootElement = XMLParser.parse(body, false);    
	    
	    List<XMLElement> list = rootElement.searchTags("<tr.+class=\"charts( new)?\".*>");
	    if(list.size() == 0)
	      break;
	    
	    for(XMLElement tag : list) {
	      Entry entry = new Entry(date, tag);
	      int hash = entry.hashCode();
	      Entry existingEntry = singleMap.get(hash);
	      if(existingEntry == null)
	        singleMap.put(hash, entry);
	      else
	        existingEntry.addRanking(entry.getLatestRanking());
	    }
	    
	    date.add(Calendar.DATE, -7);
	  }
	  
	  charts = new LinkedList<Entry>(singleMap.values());
	  Collections.sort(charts, new Comparator<Entry>() {
        @Override
        public int compare(Entry lhs, Entry  rhs) {
            return  rhs.score.get() - lhs.score.get();
        }
    });
	  
	  for(Entry entry : charts)
	    System.out.println(String.format("%4d: %s - %s", entry.getScore(), entry.getInterpret(), entry.getTitle()));	  
	}
	
	public boolean writeToDB() {
	  try {
	    FileWriter writer = new FileWriter("AustrianCharts.db");
	    writer.writeInt(charts.size());
	    for(Entry entry : charts)
	      entry.write(writer);
	    return true;
	  } catch(FileNotFoundException e) {
	    Logger.error(e);
	  } catch(IOException e) {
	    Logger.error(e);
	  }
	  return false;
	}
	
	public boolean readFromDB() {
	  try {
	    FileReader reader = new FileReader("AustrianCharts.db");
	    int size = reader.readInt();
	    System.out.println("size: " + size);
	    charts = new LinkedList<Entry>();
	    for(int i=0; i<size; i++)
	      charts.add(new Entry(reader));
	    return true;
	  } catch(FileNotFoundException e) {
      Logger.error(e);
    } catch(IOException e) {
      Logger.error(e);
    }
	  return false;
	}
	
	public void print() {
		for(Entry entry : charts)
			System.out.println(entry);
	}
}
