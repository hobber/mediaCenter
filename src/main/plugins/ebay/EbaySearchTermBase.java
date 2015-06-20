package main.plugins.ebay;

import java.io.IOException;

import main.data.DataSchemaObjectInterface;
import main.utils.FileWriter;

public abstract class EbaySearchTermBase implements DataSchemaObjectInterface {
  
  public abstract void writeValue(FileWriter file) throws IOException;
  
  public abstract void update();
  
  public abstract String toString();
}
