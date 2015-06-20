package main.plugins.ebay;

import java.io.IOException;
import java.util.LinkedList;

import main.utils.FileReader;
import main.utils.FileWriter;

public class EbaySearchTermGroup extends EbaySearchTermBase {

  private EbayAPI reporter;
  private LinkedList<EbaySearchTermBase> terms = new LinkedList<EbaySearchTermBase>();
  
  public EbaySearchTermGroup(EbayAPI reporter) {
    this.reporter = reporter;
  }
  
  EbaySearchTermGroup(EbayAPI reporter, FileReader file) throws IOException {
    this.reporter = reporter;
    if(file != null)
      readValue(file);
  }
  
  public void add(EbaySearchTermBase term) {
    terms.add(term);
  }
  
  public void update() {
    for(EbaySearchTermBase term : terms)
      term.update();
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    int size = file.readInt();
    for(int i = 0; i < size; i++)
      terms.add(reporter.readSearchTermBase(file));
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(terms.size());
    for(EbaySearchTermBase term : terms) {
      file.writeByte((byte)(term instanceof EbaySearchTerm ? 0 : 1));
      term.writeValue(file);
      System.out.println("write " + term);
    }
  }
  
  @Override
  public String toString() {
    String s = "{";
    for(int i = 0; i < terms.size(); i++)
      s += (i > 0 ? ", " : "") + terms.get(i).toString();
    return s + "}";
  }
}
