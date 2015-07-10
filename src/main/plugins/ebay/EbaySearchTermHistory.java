package main.plugins.ebay;

import java.io.IOException;

import main.data.DataSchemaObjectInterface;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Path;

public class EbaySearchTermHistory implements DataSchemaObjectInterface {

  private EbayAPI api;
  private EbaySearchTermGroup terms;
  
  public EbaySearchTermHistory(EbayAPI api) {
    this.api = api;
    terms = new EbaySearchTermGroup(api, "");
  }
  
  public EbaySearchTermHistory(EbayAPI api, FileReader file) throws IOException {
    this.api = api;
    readValue(file);    
  }
  
  public void addSearchTerm(Path path, EbaySearchTermBase term) {
    terms.add(path, term);
  }
  
  public EbaySearchTermGroup getTerms() {
    return terms;
  }
  
  public void update() {
    terms.update();
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    terms = new EbaySearchTermGroup(api, file);
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    terms.writeValue(file);
  }
}
