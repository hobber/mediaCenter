package main.plugins.austrianCharts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.plugins.Plugin;
import main.server.menu.ContentMenuEntry;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
import main.utils.XMLElement;
import main.utils.XMLParser;

public class AustrianCharts implements Plugin {  
  
	private LinkedList<ChartEntry> charts = new LinkedList<ChartEntry>();
	private Calendar lastUpdate;
	
	public AustrianCharts() {
//	  try {
//      FileReader reader = new FileReader("AustrianCharts.db");
//      lastUpdate = reader.readTime();
//      int size = reader.readInt();
//      charts = new LinkedList<ChartEntry>();
//      for(int i=0; i<size; i++)
//        charts.add(new ChartEntry(reader));
//    } catch(FileNotFoundException e) {
//      Logger.error(e);
//    } catch(IOException e) {
//      Logger.error(e);
//    }
	}
	
	@Override
	public String getName() {
	  return "Austrian Charts";
	}
	
	@Override
	public void saveState() {
    if(lastUpdate == null) {
      Logger.error("AustrianCharts have not been loaded or updated -> will not write to DB");
      return;
    }
    
    try {
      FileWriter writer = new FileWriter("AustrianCharts.db");
      writer.writeTime(lastUpdate);
      writer.writeInt(charts.size());
      for(ChartEntry entry : charts)
        entry.write(writer);
    } catch(FileNotFoundException e) {
      Logger.error(e);
    } catch(IOException e) {
      Logger.error(e);
    }
  }
	
  @Override
  public ContentMenuEntry getMenuEntry() {
    ContentMenuEntry entry = new ContentMenuEntry(this, ICON_PATH + "music.png");    
    entry.addSubMenuEntry(new AustrianChartsCurrent(this));
    entry.addSubMenuEntry(new AustrianChartsHistory(this));
    return entry;
  }
	
	public void updateDatabase() {
	  HashMap<Integer, ChartEntry> singleMap = new HashMap<Integer, ChartEntry>();
	  Calendar date = Calendar.getInstance();
	  while(date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
	    date.add(Calendar.DATE, -1);
	  
	  for(int w=0; w<5; w++) {	    
	    int day = date.get(Calendar.DATE);
	    int month = date.get(Calendar.MONTH) + 1;
	    int year = date.get(Calendar.YEAR);
	    String url = String.format("http://austriancharts.at/charts/singles/%02d-%02d-%04d", day, month, year);
	    
	    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
	    String body = response.getHTMLBody();
	    XMLElement rootElement = XMLParser.parse(body, false);    
	    
	    List<XMLElement> list = rootElement.searchTags("<tr.+class=\"charts( new)?\".*>");
	    if(list.size() == 0)
	      break;
	    
	    for(XMLElement tag : list) {
	      ChartEntry entry = new ChartEntry(date, tag);
	      int hash = entry.hashCode();
	      ChartEntry existingEntry = singleMap.get(hash);
	      if(existingEntry == null)
	        singleMap.put(hash, entry);
	      else
	        existingEntry.addRanking(entry.getLatestRanking());
	    }
	    
	    date.add(Calendar.DATE, -7);
	  }
	  
	  charts = new LinkedList<ChartEntry>(singleMap.values());
	  Collections.sort(charts, new Comparator<ChartEntry>() {
        @Override
        public int compare(ChartEntry lhs, ChartEntry  rhs) {
            return  rhs.getScore() - lhs.getScore();
        }
    });
	  
	   lastUpdate = Calendar.getInstance();
	}
	
	public List<ChartEntry> getCurrentCharts() {
	  Calendar date = Calendar.getInstance();
	  int day = date.get(Calendar.DATE);
    int month = date.get(Calendar.MONTH) + 1;
    int year = date.get(Calendar.YEAR);
    String url = String.format("http://austriancharts.at/charts/singles/%02d-%02d-%04d", day, month, year);
    
    HTTPResponse response = HTTPUtils.sendHTTPGetRequest(url);
    String body = response.getHTMLBody();
    XMLElement rootElement = XMLParser.parse(body, false);    
    
    LinkedList<ChartEntry> charts = new LinkedList<ChartEntry>();
    List<XMLElement> list = rootElement.searchTags("<tr.+class=\"charts( new)?\".*>");
    for(XMLElement tag : list)
      charts.add(new ChartEntry(date, tag));
    
    return charts;
	}
	
	public void print() {
		for(ChartEntry entry : charts)
			System.out.println(entry);
	}
}
