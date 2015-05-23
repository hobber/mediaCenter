package main.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.datatypes.MCByte;
import main.data.datatypes.MCFloat;
import main.data.datatypes.MCInteger;
import main.data.datatypes.MCList;
import main.data.datatypes.MCLong;
import main.data.datatypes.MCShort;
import main.data.datatypes.MCString;
import main.utils.FileReader;
import main.utils.FileWriter;

public class DataSchema {
	
	private class DataSchemaByte extends DataSchemaObject<Byte> {		  
		public DataSchemaByte(MCByte valueContainer) {			
			super(valueContainer);
		}		
		public void readValue(FileReader file) throws IOException {
			valueContainer.set(file.readByte());
		}
		public void writeValue(FileWriter file) throws IOException {
			file.writeByte(valueContainer.get());
		}
	}
	
	private class DataSchemaShort extends DataSchemaObject<Short> {
		public DataSchemaShort(MCShort valueContainer) {			
			super(valueContainer);
		}
		public void readValue(FileReader file) throws IOException {
		  valueContainer.set(file.readShort());
		}
		public void writeValue(FileWriter file) throws IOException {
			file.writeShort(valueContainer.get());
		}
	}
	
	private class DataSchemaInt extends DataSchemaObject<Integer> {
		public DataSchemaInt(MCInteger valueContainer) {			
			super(valueContainer);
		}
		public void readValue(FileReader file) throws IOException {
		  valueContainer.set(file.readInt());
		}
		public void writeValue(FileWriter file) throws IOException {
			file.writeInt(valueContainer.get());
		}
	}
	
	private class DataSchemaLong extends DataSchemaObject<Long> { 
    public DataSchemaLong(MCLong valueContainer) {      
      super(valueContainer);
    }
    public void readValue(FileReader file) throws IOException {
      valueContainer.set(file.readLong());
    }
    public void writeValue(FileWriter file) throws IOException {
      file.writeLong(valueContainer.get());
    }
  }
	
	private class DataSchemaFloat extends DataSchemaObject<Float> {	
		public DataSchemaFloat( MCFloat valueContainer) {			
			super(valueContainer);
		}
		public void readValue(FileReader file) throws IOException {
		  valueContainer.set(file.readFloat());
		}
		public void writeValue(FileWriter file) throws IOException {
			file.writeFloat(valueContainer.get());
		}
	}
	
	private class DataSchemaString extends DataSchemaObject<String> {					
		public DataSchemaString(MCString valueContainer) {			
			super(valueContainer);
		}
		public void readValue(FileReader file) throws IOException {
		  int length = file.readInt();
			valueContainer.set(file.readString(length));
		}
		public void writeValue(FileWriter file) throws IOException {
		  int length =  ((MCString)valueContainer).length();
			file.writeInt(length);
			file.writeString(valueContainer.get());
		}
	}
	
	private class DataSchemaIdList extends DataSchemaObject<List<Integer>> {					
		public DataSchemaIdList(MCList<Integer> valueContainer) {			
			super(valueContainer);
		}
		public void readValue(FileReader file) throws IOException {
			int length = file.readInt();
			((MCList<Integer>)valueContainer).clear();
			for(int i=0; i<length; i++)
			  ((MCList<Integer>)valueContainer).add(file.readInt());
		}
		public void writeValue(FileWriter file) throws IOException {
		  List<Integer> list = valueContainer.get();
		  int length = list.size();
			file.writeInt(length);
			for(int i=0; i<length; i++)
			  file.writeInt(list.get(i));
		}
	}
	
	private Map<String, DataSchemaObject<?>> schema = new HashMap<String, DataSchemaObject<?>>();	
	
	public DataSchema() {
	}
	
	public void addByte(String fieldName, MCByte valueContainer) {
	  if(fieldName == null || fieldName == "")
	    throw new RuntimeException("ERROR: invalid field name " + fieldName);
		DataSchemaByte object = new DataSchemaByte(valueContainer);
		schema.put(fieldName,  object);		
	}
	
	public void addShort(String fieldName, MCShort valueContainer) {
	  if(fieldName == null || fieldName == "")
      throw new RuntimeException("ERROR: invalid field name " + fieldName);
		DataSchemaShort object = new DataSchemaShort(valueContainer);
		schema.put(fieldName,  object);		
	}

	public void addInt(String fieldName, MCInteger valueContainer) {
	  if(fieldName == null || fieldName == "")
      throw new RuntimeException("ERROR: invalid field name " + fieldName);
		DataSchemaInt object = new DataSchemaInt(valueContainer);
		schema.put(fieldName,  object);		
	}

  public void addLong(String fieldName, MCLong valueContainer) {
    if(fieldName == null || fieldName == "")
      throw new RuntimeException("ERROR: invalid field name " + fieldName);
    DataSchemaLong object = new DataSchemaLong(valueContainer);
    schema.put(fieldName,  object);    
  }

	public void addFloat(String fieldName, MCFloat valueContainer) {
	  if(fieldName == null || fieldName == "")
      throw new RuntimeException("ERROR: invalid field name " + fieldName);
		DataSchemaFloat object = new DataSchemaFloat(valueContainer);
		schema.put(fieldName,  object);		
	}
	
	public void addString(String fieldName, MCString valueContainer) {
	  if(fieldName == null || fieldName == "")
      throw new RuntimeException("ERROR: invalid field name " + fieldName);
		DataSchemaString object = new DataSchemaString(valueContainer);
		schema.put(fieldName,  object);		
	}
	
	public void addIntegerList(String fieldName, MCList<Integer> valueContainer) {
	  if(fieldName == null || fieldName == "")
      throw new RuntimeException("ERROR: invalid field name " + fieldName);
		DataSchemaIdList object = new DataSchemaIdList(valueContainer);
		schema.put(fieldName,  object);		
	}
	
	public void readValues(FileReader file) throws IOException {
    for(DataSchemaObject<?> schema : schema.values())
      schema.readValue(file);     
  }
	
	public void writeValues(FileWriter file) throws IOException {		
		for(DataSchemaObject<?> schema : schema.values())
			schema.writeValue(file);
	}
	
//	public DataSelector getSelector(List<String> fieldNames) {
//		DataSelector selector = new DataSelector(className);
//		for(String fieldName : fieldNames) {
//			DataSchemaObject<?> schemaObject = schemas.get(fieldName);
//			if(schemaObject == null) {
//				System.out.println("ERROR: " + className + " has no field " + fieldName);
//				continue;
//			}
//			selector.addSchemaObject(schemaObject);
//		}
//		return selector;
//	}
//	
//	public boolean match(DataQuery dataQuery) {
//		for(Query query : dataQuery.getQueries()) {
//			String fieldName = query.getFieldName();
//			DataSchemaObject<?> schemaObject = schemas.get(fieldName);
//			if(schemaObject == null)
//				continue;
//			if(schemaObject.match(query.getMask()) == false)
//				return false;			
//		}
//		return true;
//	}
	
	@Override 
	public String toString() {
		String s = "";
		boolean first = true;
		for(String fieldName : schema.keySet()) {
			s += (first ? "" : ", ") + fieldName + ": " + schema.get(fieldName);
			first = false;
		}
		return s;
	}
}
