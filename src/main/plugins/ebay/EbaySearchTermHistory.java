package main.plugins.ebay;

import java.io.IOException;
import java.util.HashMap;

import main.data.DataSchemaObjectInterface;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
import main.utils.Path;

public class EbaySearchTermHistory implements DataSchemaObjectInterface {

  private EbayAPI api;
  private EbaySearchTermGroup terms;
  private HashMap<Integer, EbaySearchTerm> searchTerms = new HashMap<Integer, EbaySearchTerm>();
  
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
    Logger.log("Ebay: update search term history");
    terms.update();
  }
  
  public boolean searchTermHasCategory(int searchTermId) {
    EbaySearchTerm term = getSearchTerm(searchTermId);
    if(term == null) {
      Logger.error("Ebay: did not found requested search term " + searchTermId);
      return false;
    }
    return term.hasCategory();
  }
  
  public EbaySearchTerm getSearchTerm(int searchTermId) {
    return searchTerms.get(searchTermId);
  }
  
  private void updateSearchTermMap(EbaySearchTermGroup group) {
    for(EbaySearchTermBase entry : group.getTerms())
      if(entry instanceof EbaySearchTerm)
        searchTerms.put(((EbaySearchTerm)entry).getId(), (EbaySearchTerm)entry);
      else
        updateSearchTermMap((EbaySearchTermGroup)entry);
  }

  @Override
  public void readValue(FileReader file) throws IOException {
    terms = new EbaySearchTermGroup(api, file);
    updateSearchTermMap(terms);
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    terms.writeValue(file);
  }
}
