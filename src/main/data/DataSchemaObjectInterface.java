package main.data;

import java.io.IOException;

import main.utils.FileReader;
import main.utils.FileWriter;

public interface DataSchemaObjectInterface {

  public abstract void readValue(FileReader file) throws IOException;
  public abstract void writeValue(FileWriter file) throws IOException;
  
}
