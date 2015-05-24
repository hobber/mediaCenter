package main.plugins.austrianCharts;

import java.io.IOException;
import java.util.Calendar;

import main.data.DataSchemaObjectInterface;
import main.utils.ConfigFile;
import main.utils.FileReader;
import main.utils.FileWriter;

class RankingEntry implements DataSchemaObjectInterface {
  
  private Calendar date;
  private String currentRanking;
  private String previousRanking;
  
  private RankingEntry() {      
  }
  
  public RankingEntry(Calendar date, String currentRanking, String previousRanking) {
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
  
  public int compareTo(RankingEntry entry) {
    return date.compareTo(entry.date);
  }
  
  @Override
  public String toString() {
    return String.format("%s %2s (%3s)", ConfigFile.dateToString(date), currentRanking, previousRanking);
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    date = file.readTime();
    currentRanking = file.readString(file.readInt());
    previousRanking = file.readString(file.readInt());
  }
  
  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeTime(date);
    file.writeInt(currentRanking.length());
    file.writeString(currentRanking);
    file.writeInt(previousRanking.length());
    file.writeString(previousRanking);
  }
  
  public static RankingEntry createFromFile(FileReader file) throws IOException {
    RankingEntry entry = new RankingEntry();
    entry.readValue(file);
    return entry;
  }
}
