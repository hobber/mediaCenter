package main.plugins.ebay;

import java.io.IOException;

import main.data.DataSchemaObjectInterface;
import main.utils.FileWriter;

public abstract interface EbaySearchTermBase extends DataSchemaObjectInterface {
  
  public void writeValue(FileWriter file) throws IOException;
  
  public void update();
  
  public String getName();
  
  public void rename(String name);
  
  public void delete();
  
  public boolean isDeleted();
  
  public String toString();
}
