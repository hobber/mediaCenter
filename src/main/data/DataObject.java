package main.data;

import java.io.IOException;

import main.utils.FileReader;
import main.utils.FileWriter;


public abstract class DataObject {
	
	protected DataSchema schema;

	protected DataObject() {
	  schema = createDataSchema();
	}
	
	protected DataObject(FileReader file) throws IOException {
	  schema = createDataSchema();
	  schema.readValues(file);
	}
	
  protected void write(FileWriter file) throws IOException {
		schema.writeValues(file);
  }
  
//	public boolean match(DataQuery dataQuery) {
//		return schema.match(dataQuery);
//	}
//	
//	public final String toString() {
//		return schema.toString();
//	}
  
  protected abstract DataSchema createDataSchema();
}
