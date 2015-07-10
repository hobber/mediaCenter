package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
import main.utils.Path;

public class EbaySearchTermGroup extends EbaySearchTermBase {

  private EbayAPI api;
  private String groupName;
  private LinkedList<EbaySearchTermBase> terms = new LinkedList<EbaySearchTermBase>();
  
  public EbaySearchTermGroup(EbayAPI api, String groupName) {
    this.api = api;
    this.groupName = groupName;
  }
  
  EbaySearchTermGroup(EbayAPI api, FileReader file) throws IOException {
    this.api = api;
    if(file != null)
      readValue(file);
  }
  
  public String getName() {
    return groupName;
  }
  
  public boolean add(Path path, EbaySearchTermBase term) {
    if(path.length() == 0) {
      terms.add(term);
      return true;
    }
    else {
      String name = path.pop();
      for(EbaySearchTermBase termBase : terms) {
        if(termBase instanceof EbaySearchTermGroup && ((EbaySearchTermGroup)termBase).getName().equals(name)) {
          ((EbaySearchTermGroup)termBase).add(path, term);
          return true;
        }
      }
      Logger.error("EbaySearchTermGroup " + path + " unknown!");
      return false;
    }
  }
  
  public LinkedList<EbaySearchTermBase> getTerms() {
    return terms;
  }
  
  public void update() {
    for(EbaySearchTermBase term : terms)
      term.update();
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    groupName = file.readString(file.readInt());
    int size = file.readInt();
    for(int i = 0; i < size; i++)
      terms.add(api.readSearchTermBase(file));
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(groupName.length());
    file.writeString(groupName);
    file.writeInt(terms.size());
    for(EbaySearchTermBase term : terms) {
      file.writeByte((byte)(term instanceof EbaySearchTerm ? 0 : 1));
      term.writeValue(file);
      System.out.println("write " + term);
    }
  }
  
  @Override
  public String toString() {
    String s = groupName + ": {";
    for(int i = 0; i < terms.size(); i++)
      s += (i > 0 ? ", " : "") + terms.get(i).toString();
    return s + "}";
  }
}
