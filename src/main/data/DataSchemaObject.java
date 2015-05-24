package main.data;

import java.io.IOException;

import main.data.datatypes.MCDatatype;
import main.utils.FileReader;
import main.utils.FileWriter;

public abstract class DataSchemaObject<Type> implements DataSchemaObjectInterface {

  protected MCDatatype<Type> valueContainer;
	
	protected DataSchemaObject(MCDatatype<Type> valueContainer) {
	  if(valueContainer == null)
	    throw new RuntimeException("ERROR: value container is null");
	  this.valueContainer = valueContainer;
	}
	
	public void set(Type value) {
	  valueContainer.set(value);  
	}
	
	public Type get() {
	  return valueContainer.get();
	}
	
	public abstract void readValue(FileReader file) throws IOException;
  public abstract void writeValue(FileWriter file) throws IOException;
	
	@Override
	public String toString() {
	  return valueContainer.toString();
	}
}
