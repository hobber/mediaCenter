package main.plugins.austrianCharts;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import main.data.DataObject;
import main.data.DataSchema;
import main.data.datatypes.MCInteger;
import main.data.datatypes.MCString;
import main.http.HTTPResponse;
import main.http.HTTPUtils;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.XMLElement;
import main.utils.XMLParser;

class ChartEntry extends DataObject {
  
  private int hashCode;
  private MCString interpret;
  private MCString title;
  private MCString detailUrl;
  private MCInteger score;
  private Ranking ranking;
  
  private String videoUrl;
  
  public ChartEntry(Calendar date, XMLElement tag) {      
    List<XMLElement> columns = tag.getChildren();
    String currentRanking = columns.get(0).getChild(0).getValue();
    String previousRanking = columns.get(1).getChild(0).getValue();
    if(previousRanking == null)
      previousRanking = "NEW";
    ranking.add(new RankingEntry(date, currentRanking, previousRanking));
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
  
  public ChartEntry(FileReader file) throws IOException {
    super(file);
    hashCode = (interpret.get() + title.get()).hashCode();
  }
  
  public void write(FileWriter file) throws IOException {
    schema.writeValues(file);
  }
  
  public void addRanking(RankingEntry ranking) {
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
  
  public RankingEntry getLatestRanking() {
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
    for(RankingEntry rank : ranking)
      System.out.println(rank);
  }
  
  @Override
  public String toString() {
    if(ranking.isEmpty())
      return "? (  ?): " + interpret + " - " + title;
    RankingEntry currentRanking = ranking.getLast();
    return currentRanking.getCurrentRanking() + " (" + currentRanking.getPreviousRanking() + "): " + interpret + " - " + title;
  }
  
  @Override
  public int hashCode() {
    return hashCode;
  }
  
  private void sortRanking() {
    ranking.sort(new Comparator<RankingEntry>() {
      @Override
      public int compare(RankingEntry  lhs, RankingEntry  rhs) {
          return  lhs.compareTo(rhs);
      }
    });
  }
  
  @Override
  protected DataSchema createDataSchema() {
    interpret = new MCString();
    title = new MCString();
    detailUrl = new MCString();
    score = new MCInteger();
    ranking = new Ranking();
    
    DataSchema schema = new DataSchema();
    schema.addString("interpret", interpret);
    schema.addString("title", title);
    schema.addString("detailUrl", detailUrl);
    schema.addInt("score", score);
    schema.addDataSchemaObject("ranking", ranking);
    return schema;
  }
}
