package main.plugins.ebay;

import java.io.IOException;

import main.data.DataSchemaObjectInterface;
import main.utils.FileReader;
import main.utils.FileWriter;

public class EbaySearchTermHistory implements DataSchemaObjectInterface {

  private EbayReporter reporter;
  private EbaySearchTermGroup terms;
  
  public EbaySearchTermHistory(EbayReporter reporter) {
    this.reporter = reporter;
    terms = new EbaySearchTermGroup(reporter);
  }
  
  public EbaySearchTermHistory(EbayReporter reporter, FileReader file) throws IOException {
    this.reporter = reporter;
    if(file != null)
      readValue(file);
    else
      terms = new EbaySearchTermGroup(reporter, null);
  }
  
  public void addSearchTerm(EbaySearchTermBase term) {
    terms.add(term);
  }
  
  public void update() {
    terms.update();
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    terms = new EbaySearchTermGroup(reporter, file);
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    terms.writeValue(file);
  }
}
